package org.dentinger.tutorial.service;

import java.util.List;
import org.dentinger.tutorial.loader.nodefirst.NFLeagueLoader;
import org.dentinger.tutorial.loader.nodefirst.NFRegionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NodeFirstNeo4jLoaderService {

  private NFRegionLoader regionLoader;
  private NFLeagueLoader leagueLoader;

  @Autowired
  public NodeFirstNeo4jLoaderService(NFRegionLoader regionLoader,
                                     NFLeagueLoader leagueLoader) {

    this.leagueLoader = leagueLoader;

    this.regionLoader = regionLoader;

  }

  public void runLoader(List<String> list) {

    if (list.contains("loadRegions")) {
      regionLoader.loadRegions();
    }
    if (list.contains("loadLeagues")) {
      leagueLoader.loadLeagueNodes();
    }

    if (list.contains("loadLeagues")) {
      leagueLoader.loadLeagueRelationships();
    }

  }
}
