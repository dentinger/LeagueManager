package org.dentinger.tutorial.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class TeamLoader {
  private static Logger logger = LoggerFactory.getLogger(TeamLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private final AtomicLong recordsWritten = new AtomicLong(0);

  private String MERGE_TEAMS =
      "unwind {json} as team "
          + "unwind team.leagues as league "
          + "   merge (l:League {id: league.id})"
          + "    merge (t:Team {id: team.id})"
          + "     on create set t.name = team.name "
          + "    merge (t)-[:PLAYS_IN]-(l)";

  @Autowired
  public TeamLoader(Neo4jProperties neo4jProperties,
                    SessionFactory sessionFactory,
                      SportsBallRepository repo) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.repo = repo;
  }

  public void loadTeams() {
    Neo4jTemplate neo4jTemplate = getNeo4jTemplate();
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<League> leagues = repo.getLeagues();

    recordsWritten.set(0);
    long start = System.currentTimeMillis();
    leagues.stream()
        .forEach(league -> {
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
    logger.info("Processing of {} Team relationships complete: {}ms", recordsWritten.get(), System.currentTimeMillis() - start);
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }
}
