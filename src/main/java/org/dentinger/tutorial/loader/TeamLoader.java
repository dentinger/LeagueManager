package org.dentinger.tutorial.loader;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.domain.Team;
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
public class TeamLoader {
  private static Logger logger = LoggerFactory.getLogger(TeamLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private int numThreads;
  private final AtomicLong recordsWritten = new AtomicLong(0);

  private String MERGE_TEAMS =
      "unwind {json} as team "
          + "unwind team.leagues as league "
          + "   merge (l:League {id: league.id})"
          + "    merge (t:Team {id: team.id})"
          + "     on create set t.name = team.name "
          + "    merge (t)-[:MEMBERSHIP]-(l)";

  @Autowired
  public TeamLoader(Neo4jProperties neo4jProperties,
                    SessionFactory sessionFactory,
                    SportsBallRepository repo,
                    Environment env) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.repo = repo;
    this.numThreads = Integer.valueOf(env.getProperty("teams.loading.threads","1"));
  }

  public void loadTeams() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<League> leagues = repo.getLeagues();
    logger.info("About to load Teams for {} leagues using {} threads", leagues.size(), numThreads);
    recordsWritten.set(0);
    ExecutorService executorService = getExecutorService(numThreads);
    int subListSize = (int) Math.floor(leagues.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(leagues, subListSize).stream().parallel()
        .forEach((sublist) -> {
          executorService.submit(() -> {
                Neo4jTemplate neo4jTemplate = getNeo4jTemplate();
                sublist.stream().forEach(league -> {
                  List<Team> teams = repo.getTeams(league);
                  if( teams != null ) {
                    logger.info("About to load {} teams for league({})", teams.size(), league.getId());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("json", teams);
                    try {
                      neo4jTemplate.execute(MERGE_TEAMS, map);
                      recordsWritten.addAndGet(leagues.size());
                    } catch (Exception e) {
                      aeLogger
                          .error("Unable to update graph, leagueId={}, teamCount={}", league.getId(), teams.size(), e);
                    }
                  }
                });
              });
            });
    logger.info("Processing of {} Team relationships complete: {}ms", recordsWritten.get(), System.currentTimeMillis() - start);
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }

  private ExecutorService getExecutorService(int numThreads) {
    final ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("teamLoader-%d")
        .setDaemon(true)
        .build();
    return Executors.newFixedThreadPool(numThreads, threadFactory);
  }
}
