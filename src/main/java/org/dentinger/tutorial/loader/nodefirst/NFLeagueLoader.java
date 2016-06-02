package org.dentinger.tutorial.loader.nodefirst;

import com.google.common.collect.Lists;
import java.util.List;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class NFLeagueLoader {
  private static Logger logger = LoggerFactory.getLogger(NFLeagueLoader.class);
  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private int numThreads;
  private TaskExecutor poolTaskExecutor;
  private LeagueWorker leagueWorker;

  private String MERGE_LEAGUE_NODES =
      "unwind {json} as league "
          + "merge (l:League {leagueId: league.leagueId})"
          + " on create set l.name = league.name"
          + " on match set l.name = league.name";


  private String MERGE_LEAGUE_RELATIONSHIPS =
      "unwind {json} as league "
          + "unwind league.regions as region "
          + "   match (r:Region {regionId: region.regionId})"
          + "   match (l:League {leagueId: league.leagueId})"
          + "   merge (r)-[:SANCTION]-(l)";

  private String CLEAN_UP =
      "match (l:League) detach delete l";

  @Autowired
  public NFLeagueLoader(TaskExecutor leagueProcessorThreadPool,
                        SessionFactory sessionFactory,
                        SportsBallRepository repo,
                        Environment env, LeagueWorker leagueWorker) {
    this.sessionFactory = sessionFactory;
    this.repo = repo;
    this.leagueWorker = leagueWorker;
    this.numThreads = Integer.valueOf(env.getProperty("leagues.loading.threads", "1"));
    this.poolTaskExecutor = leagueProcessorThreadPool;
  }

  public void cleanup(){
    logger.info("About to cleanup leagues");
    getNeo4jTemplate().execute(CLEAN_UP);
    logger.info("Cleanup of leagues complete");
  }

  public void loadLeagueNodes() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());

    List<League> leagueList = repo.getLeagues();
    logger.info("About to load {} Leagues using {} threads", leagueList.size(), numThreads);
    leagueWorker.getRecordsWritten().set(0);

    int subListSize = (int) Math.floor(leagueList.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(leagueList, subListSize).stream().parallel()
        .forEach((leagues) -> {
          leagueWorker.doSubmitableWork(aeLogger, leagues, MERGE_LEAGUE_NODES);
        });

    monitorThreadPool();

    logger.info("Processing of {} Leagues using {} threads complete: {}ms", leagueWorker.getRecordsWritten().get(),
        numThreads,
        System.currentTimeMillis() - start);
  }

  public void loadLeagueRelationships() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    //List<Region> regions = repo.getRegions();
    List<League> leagues = repo.getLeagues();
    logger.info("About to load League relationships using {} threads", numThreads);
    leagueWorker.getRecordsWritten().set(0);

    int subListSize = (int) Math.floor(leagues.size() / numThreads);
    long start = System.currentTimeMillis();

    Lists.partition(leagues, subListSize).stream().parallel()
        .forEach((leagueSubList) -> {
          leagueWorker.doSubmitableWork(aeLogger, leagues, MERGE_LEAGUE_RELATIONSHIPS);
        });

    monitorThreadPool();

    logger
        .info("Processing of {} League relationships using {} threads complete: {}ms",
            leagueWorker.getRecordsWritten().get(), numThreads,
            System.currentTimeMillis() - start);
  }

  private void monitorThreadPool() {
    while (( (ThreadPoolTaskExecutor)poolTaskExecutor).getActiveCount() > 0) {
//      logger.info("{} threads: {}, jobs still in pool {}",
//          ( (ThreadPoolTaskExecutor)poolTaskExecutor).getThreadNamePrefix(),
//          ( (ThreadPoolTaskExecutor)poolTaskExecutor).getActiveCount(),
//          ( (ThreadPoolTaskExecutor)poolTaskExecutor).getPoolSize());
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession();
    return new Neo4jTemplate(session);
  }
}
