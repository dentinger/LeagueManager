package org.dentinger.tutorial.loader.nodefirst;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class NFLeagueLoader {
  private static Logger logger = LoggerFactory.getLogger(NFLeagueLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private int numThreads;
  private final AtomicLong recordsWritten = new AtomicLong(0);

  private String MERGE_LEAGUES_NODE =
      "unwind {json} as league "
          + "unwind league.regions as region "
          + "    merge (l:League {id: league.id})"
          + "     on create set l.name = league.name ";

  private String MERGE_LEAGUES_RELATIONSHIPS =
      "unwind {json} as league "
          + "unwind league.regions as region "
          + "   match (r:Region {id: region.id})"
          + "    match (l:League {id: league.id})"
          + "    merge (r)-[:SANCTION]-(l)";

  @Autowired
  public NFLeagueLoader(Neo4jProperties neo4jProperties,
                        SessionFactory sessionFactory,
                        SportsBallRepository repo,
                        Environment env) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.repo = repo;
    this.numThreads = Integer.valueOf(env.getProperty("leagues.loading.threads", "1"));
  }
  public void loadLeagueNodes() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());

    List<League> leagueList = repo.getLeagues();
    logger.info("About to load Leagues for {} in {} threads", leagueList.size(), numThreads);
    recordsWritten.set(0);
    ExecutorService executorService = getExecutorService(numThreads);
    int subListSize = (int) Math.floor(leagueList.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(leagueList, subListSize).stream().parallel()
        .forEach((leagues) -> {
          executorService.submit(() -> {
            doSubmitableWork(aeLogger, leagues, leagues.size(), MERGE_LEAGUES_NODE);

          });
        });
    executorService.shutdown();
    try {
      executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }catch(Exception e){
      logger.error("executorService exception: ",e);
    }
    logger.info("Processing of {} League relationships complete: {}ms", recordsWritten.get(), System.currentTimeMillis() - start);
  }

  public void loadLeagueRelationships() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<Region> regions = repo.getRegions();
    List<League> leagueList = repo.getLeagues();
    logger.info("About to load Leagues for {} in {} threads", leagueList.size(), numThreads);
    recordsWritten.set(0);
    ExecutorService executorService = getExecutorService(numThreads);
    int subListSize = (int) Math.floor(regions.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(leagueList, subListSize).stream().parallel()
        .forEach((leagues) -> {
          executorService.submit(() -> {
            doSubmitableWork(aeLogger, leagues, leagues.size(), MERGE_LEAGUES_RELATIONSHIPS);

          });
        });
    executorService.shutdown();
    try {
      executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }catch(Exception e){
      logger.error("executorService exception: ",e);
    }
    logger.info("Processing of {} League relationships complete: {}ms", recordsWritten.get(), System.currentTimeMillis() - start);
  }

  private void doSubmitableWork(AggregateExceptionLogger aeLogger,
                                List<League> leagues,
                                int size,
                                String cypher) {
    Neo4jTemplate
        neo4jTemplate = getNeo4jTemplate();

    logger.info("About to load {} leagues ", size);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("json", leagues);
    try {
      neo4jTemplate.execute(cypher, map);
      recordsWritten.addAndGet(size);
    } catch (Exception e) {
      aeLogger
          .error("Unable to update graph, leagueCount={}", size,
              e);
    }
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }

  private ExecutorService getExecutorService(int numThreads) {
    final ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("leagueLoader-nodefirst-%d")
        .setDaemon(true)
        .build();
    return Executors.newFixedThreadPool(numThreads, threadFactory);
  }


}
