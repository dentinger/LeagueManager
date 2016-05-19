package org.dentinger.tutorial.loader.nodefirst;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.dentinger.tutorial.util.RetriableTask;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class NFLeagueLoader {
  private static Logger logger = LoggerFactory.getLogger(NFLeagueLoader.class);
  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private int numThreads;
  private final AtomicLong recordsWritten = new AtomicLong(0);
  private ThreadPoolTaskExecutor poolTaskExecutor;

  private String MERGE_LEAGUE_NODES =
      "unwind {json} as league "
          + "unwind league.regions as region "
          + "    merge (l:League {id: league.id})"
          + "     on create set l.name = league.name ";

  private String MERGE_LEAGUE_RELATIONSHIPS =
      "unwind {json} as league "
          + "unwind league.regions as region "
          + "   match (r:Region {id: region.id})"
          + "    match (l:League {id: league.id})"
          + "    merge (r)-[:SANCTION]-(l)";

  @Autowired
  public NFLeagueLoader(ThreadPoolTaskExecutor leagueProcessorThreadPool,
                        SessionFactory sessionFactory,
                        SportsBallRepository repo,
                        Environment env) {
    this.sessionFactory = sessionFactory;
    this.repo = repo;
    this.numThreads = Integer.valueOf(env.getProperty("leagues.loading.threads", "1"));
    this.poolTaskExecutor = leagueProcessorThreadPool;
  }

  public void loadLeagueNodes() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());

    List<League> leagueList = repo.getLeagues();
    logger.info("About to load {} Leagues using {} threads", leagueList.size(), numThreads);
    recordsWritten.set(0);

    int subListSize = (int) Math.floor(leagueList.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(leagueList, subListSize).stream().parallel()
        .forEach((leagues) -> {
          doSubmitableWork(aeLogger, leagues, MERGE_LEAGUE_NODES);

        });
    monitorThreadPool();
    logger.info("Processing of {} Leagues using {} threads complete: {}ms", recordsWritten.get(),
        numThreads,
        System.currentTimeMillis() - start);
  }

  public void loadLeagueRelationships() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<Region> regions = repo.getRegions();
    logger.info("About to load League relationships using {} threads",numThreads);
    recordsWritten.set(0);

    int subListSize = (int) Math.floor(regions.size() / numThreads);
    long start = System.currentTimeMillis();

    Lists.partition(regions, subListSize).stream().parallel()
        .forEach((regionsSubList) -> {
          List<League> leagues = regionsSubList.stream()
              .map(region -> repo.getLeagues(region))
              .flatMap(l -> l.orElse(Collections.emptyList()).stream()).
                  collect(Collectors.toList());

          doSubmitableWork(aeLogger, leagues, MERGE_LEAGUE_RELATIONSHIPS);

        });
    monitorThreadPool();
    logger
        .info("Processing of {} League relationships using {} threads complete: {}ms",
            recordsWritten.get(), numThreads,
            System.currentTimeMillis() - start);
  }

  private void monitorThreadPool() {
    while (poolTaskExecutor.getPoolSize() > 0) {
      logger.info("Currently running threads: {}, jobs still in pool {}",
          poolTaskExecutor.getActiveCount(),
          poolTaskExecutor.getPoolSize());
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Async("leagueProcessorThreadPool")
  private void doSubmitableWork(AggregateExceptionLogger aeLogger,
                                List<League> leagues,
                                String cypher) {
    Neo4jTemplate
        neo4jTemplate = getNeo4jTemplate();

    logger.debug("About to process {} leagues ", leagues.size());
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("json", leagues);
    try {
      new RetriableTask().retries(3).delay(50, TimeUnit.MILLISECONDS).execute(() -> {
        neo4jTemplate.execute(cypher, map);
        recordsWritten.addAndGet(leagues.size());
      });
    } catch (Exception e) {
      aeLogger
          .error("Unable to update graph, leagueCount={}", leagues.size(),
              e);
    }
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession();
    return new Neo4jTemplate(session);
  }
}
