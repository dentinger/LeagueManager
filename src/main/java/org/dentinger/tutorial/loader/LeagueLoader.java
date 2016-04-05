package org.dentinger.tutorial.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.client.LeagueClient;
import org.dentinger.tutorial.client.RegionClient;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class LeagueLoader {
  private static Logger logger = LoggerFactory.getLogger(LeagueLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private RegionClient regionClient;
  private LeagueClient leagueClient;

  private String MERGE_LEAGUES =
      "unwind {json} as league "
          + "unwind league.regions as region "
          + "   merge (r:Region {id: region.id})"
          + "    merge (l:League {id: league.id})"
          + "     on create set l.name = league.name "
          + "     on match set l.name = league.name "
          + "    merge (r)-[:SANCTIONS]-(l)";

  @Autowired
  public LeagueLoader(Neo4jProperties neo4jProperties,
                      SessionFactory sessionFactory,
                      RegionClient regionClient,
                      LeagueClient leagueClient) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.regionClient = regionClient;
    this.leagueClient = leagueClient;
  }

  public void loadLeagues() {
    Neo4jTemplate neo4jTemplate = getNeo4jTemplate();
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<Region> regions = regionClient.getRegions();

    long start = System.currentTimeMillis();
    regions.stream()
        .forEach(region -> {
          List<League> leagues = leagueClient.getLeagues(region);

          logger.info("About to load {} leagues for region({})",leagues.size(),region.getId());
          Map<String, Object> map = new HashMap<String, Object>();
          map.put("json", leagues);
          try {
            neo4jTemplate.execute(MERGE_LEAGUES, map);
          } catch (Exception e) {
            aeLogger.error("Unable to update graph, regionId={}, leagueCount={}", region.getId(), leagues.size(), e);
          }
        });
    logger.info("Load Leagues complete: {}ms",System.currentTimeMillis()-start);
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }
}
