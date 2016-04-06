package org.dentinger.tutorial.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.client.LeagueClient;
import org.dentinger.tutorial.client.RegionClient;
import org.dentinger.tutorial.client.TeamClient;
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
  private TeamClient teamClient;
  private LeagueClient leagueClient;
  private RegionClient regionClient;
  private final AtomicLong recordsWritten = new AtomicLong(0);

  private String MERGE_LEAGUES =
      "unwind {json} as league "
          + "unwind league.regions as region "
          + "   merge (r:Region {id: region.id})"
          + "    merge (l:League {id: league.id})"
          + "     on create set l.name = league.name "
          + "     on match set l.name = league.name "
          + "    merge (r)-[:SANCTIONS]-(l)";

  @Autowired
  public TeamLoader(Neo4jProperties neo4jProperties,
                    SessionFactory sessionFactory,
                    TeamClient teamClient,
                    RegionClient regionClient,
                    LeagueClient leagueClient) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.regionClient = regionClient;
    this.leagueClient = leagueClient;
    this.teamClient = teamClient;
  }

  public void loadTeams() {
    Neo4jTemplate neo4jTemplate = getNeo4jTemplate();
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<Region> regions = regionClient.getRegions();

    recordsWritten.set(0);
    long start = System.currentTimeMillis();
    regions.stream()
        .forEach(region -> {
          List<League> leagues = leagueClient.getLeagues(region);
          leagues.stream()
              .forEach(league -> {
                List<Team> teams = teamClient.getTeams(league);
                logger.info("About to load {} teams for league({})", teams.size(), league.getId());
                teams.stream()
                    .forEach(team -> {
                      // TODO Add neo stuff
                      logger.info("New team: {}",team);
                      recordsWritten.incrementAndGet();
                    });
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("json", leagues);
//                try {
//                  neo4jTemplate.execute(MERGE_LEAGUES, map);
//                } catch (Exception e) {
//                  aeLogger.error("Unable to update graph, regionId={}, leagueCount={}", region.getId(), leagues.size(), e);
//                }
          });
        });
    logger.info("Loading of {} Teams complete: {}ms", recordsWritten.get(), System.currentTimeMillis() - start);
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }
}
