package org.dentinger.tutorial.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class LeagueClient {
  private List<League> leagues;
  private int perRegion;

  @Autowired
  public LeagueClient(Environment environment) {
    perRegion = Integer.valueOf(environment.getRequiredProperty("league.perRegion.count"));
  }

  public List<League> getLeagues(Long regionId) {
    if (perRegion >= 1000) {
      throw new IllegalStateException(
          "The number of leagues per region messes up our league id generation, duplicate nodes will result");
    }
    leagues = new ArrayList<>();
    LongStream.range(0, perRegion)
        .forEach(i -> {
          Long leagueId = (regionId * 1000) + (i + 1);
          leagues.add(new League(leagueId, regionId, "League" + leagueId));
        });
    return leagues;
  }

  public List<League> getLeagues(Region region) {
    if (perRegion >= 1000) {
      throw new IllegalStateException(
          "The number of leagues per region messes up our league id generation, duplicate nodes will result");
    }
    leagues = new ArrayList<>();
    LongStream.range(0, perRegion)
        .forEach(i -> {
          Long leagueId = (region.getId() * 1000) + (i + 1);
          leagues.add(new League(leagueId, region.getId(), "League" + leagueId));
        });
    return leagues;
  }
}
