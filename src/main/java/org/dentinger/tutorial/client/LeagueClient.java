package org.dentinger.tutorial.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.dentinger.tutorial.client.dto.LeagueDTO;
import org.dentinger.tutorial.domain.League;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class LeagueClient {
  private List<LeagueDTO> leagues;
  private int perRegion;

  @Autowired
  public LeagueClient(Environment environment) {
    perRegion = Integer.valueOf(environment.getRequiredProperty("league.perRegion.count"));
  }

  public List<LeagueDTO> getLeagues(Long regionId) {
    if( perRegion >= 1000 ){
      throw new IllegalStateException("The number of leagues per region messes up our league id generation, duplicate nodes will result");
    }
    leagues = new ArrayList<>();
    LongStream.range(0, perRegion)
        .forEach(i -> {
          Long leagueId = (regionId * 1000)+(i+1);
          leagues.add(new LeagueDTO(leagueId, regionId, "League" + leagueId));
        });
    return leagues;
  }
}
