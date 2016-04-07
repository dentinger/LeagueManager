package org.dentinger.tutorial.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class LeagueClient {
  private Map<Long,List<League>> leagueMap;
  private RegionClient regionClient;
  private int count;
  private int minRegionAffiliations;
  private int maxRegionAffiliations;


  @Autowired
  public LeagueClient(Environment environment, RegionClient regionClient) {
    count = Integer.valueOf(environment.getRequiredProperty("leagues.count"));
    minRegionAffiliations = Integer.valueOf(environment.getRequiredProperty("leagues.minRegionAffiliations"));
    maxRegionAffiliations = Integer.valueOf(environment.getRequiredProperty("leagues.maxRegionAffiliations"));
    List<Region> regions = regionClient.getRegions();
    Random r = new Random(System.currentTimeMillis());
    leagueMap = new HashMap<>();
    LongStream.range(0,count).forEach(i -> {
      long leagueId = i+1;
      League league = new League(leagueId, "League-"+leagueId);
      r.ints(0,regions.size()).limit(minRegionAffiliations+r.nextInt(maxRegionAffiliations))
          .forEach(x -> {
            Region region = regions.get(x);
            league.addRegion(region);
            List<League> leagues = leagueMap.get(region.getId());
            if( leagues == null ){
              leagues = new ArrayList<League>();
              leagueMap.put(region.getId(),leagues);
            }
            leagues.add(league);
          });
    });
  }

  public List<League> getLeagues(Region region) {
    return leagueMap.get(region.getId());
  }
}
