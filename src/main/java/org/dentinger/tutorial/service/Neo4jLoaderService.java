package org.dentinger.tutorial.service;

import java.util.List;
import org.dentinger.tutorial.loader.combinedunwind.LeagueLoader;
import org.dentinger.tutorial.loader.combinedunwind.RegionLoader;
import org.dentinger.tutorial.loader.combinedunwind.TeamLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Neo4jLoaderService {

  private RegionLoader regionLoader;
  private LeagueLoader leagueLoader;
  private TeamLoader teamLoader;

  @Autowired
  public Neo4jLoaderService(RegionLoader regionLoader,
                            LeagueLoader leagueLoader,
                            TeamLoader teamLoader) {

    this.leagueLoader = leagueLoader;
    this.teamLoader = teamLoader;
    this.regionLoader = regionLoader;

  }

  public void runCombinedUnwindLoader(List<String> list) {
    if (list.contains("loadRegions")) {
      regionLoader.loadRegions();
    }
    if (list.contains("loadLeagues")) {
      leagueLoader.loadLeagues();
    }
    if (list.contains("loadTeams")) {
      teamLoader.loadTeams();
    }
    if (list.contains("loadAll")) {
      regionLoader.loadRegions();
      leagueLoader.loadLeagues();
      teamLoader.loadTeams();
    }
  }

  public void cleanup() {
    regionLoader.cleanup();
    leagueLoader.cleanup();
    teamLoader.cleanup();
  }
}
