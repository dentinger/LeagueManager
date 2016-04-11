package org.dentinger.tutorial.loader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.client.LeagueClient;
import org.dentinger.tutorial.client.RegionClient;
import org.dentinger.tutorial.client.VenueClient;
import org.dentinger.tutorial.domain.Gym;
import org.dentinger.tutorial.domain.Region;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class VenueLoader {

  private static Logger logger = LoggerFactory.getLogger(VenueLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private VenueClient venueClient;
  private LeagueClient leagueClient;
  private RegionClient regionClient;
  private final AtomicLong recordsWritten = new AtomicLong(0);

  private String MERGE_VENUES =
      "unwind {json} as league "
          + "unwind league.venues as venue "
          + "   merge (v:Venue {id: venue.id})"
          + "     on create set v.name = venue.name "
          + "    merge (l:League {id: league.id})"
          + "     on match set l.name = league.name "
          + "    merge (l)-[:PLAYS_GAMES_AT]-(v)";

  @Autowired
  public VenueLoader(Neo4jProperties neo4jProperties,
                     SessionFactory sessionFactory,
                     VenueClient venueClient,
                     LeagueClient leagueClient,
                     RegionClient regionClient
  ) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.venueClient = venueClient;
    this.leagueClient = leagueClient;
    this.regionClient = regionClient;
  }

  public void loadVenues() {
    Neo4jTemplate neo4jTemplate = getNeo4jTemplate();
    venueClient.getVenues();

    List<Region> regions = regionClient.getRegions();
    recordsWritten.set(0);
    long start = System.currentTimeMillis();
    regions.stream().forEach(
        region -> {
          leagueClient.getLeagues(region).stream().forEach(league -> {
            List<Gym> venuesForLeague = venueClient.getVenuesForLeague(league);
            logger.info("Going to load {} venues for league {}", venuesForLeague.size(),
                league.getId());
            //TODO insert into NEO
            recordsWritten.incrementAndGet();
            Map<String, Object> map = new HashMap<String, Object>();
            league.setVenues(new HashSet<>(venuesForLeague));
            map.put("json", league);
            logger.info("About to load {} venues for league {}", venuesForLeague.size(),
                league.getId());
            try {
              neo4jTemplate.execute(MERGE_VENUES, map);
            } catch (Exception e) {
              logger.error("Unable to update graph, regionId={}, leagueCount={}", region.getId(),
                  league.getId(), e);
            }

          });
        }

    );
    logger.info("Loading of {} Venues complete: {}ms", recordsWritten.get(),
        System.currentTimeMillis() - start);
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }
}
