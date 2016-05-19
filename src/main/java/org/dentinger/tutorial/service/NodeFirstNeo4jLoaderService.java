package org.dentinger.tutorial.service;

import java.util.List;
import org.dentinger.tutorial.loader.nodefirst.NFLeagueLoader;
import org.dentinger.tutorial.loader.nodefirst.NFPersonLoader;
import org.dentinger.tutorial.loader.nodefirst.NFRegionLoader;
import org.dentinger.tutorial.loader.nodefirst.NFTeamLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NodeFirstNeo4jLoaderService {

  private NFRegionLoader regionLoader;
  private NFLeagueLoader leagueLoader;
  private NFPersonLoader personLoader;
  private NFTeamLoader teamLoader;

  @Autowired
  public NodeFirstNeo4jLoaderService(NFRegionLoader regionLoader,
                                     NFLeagueLoader leagueLoader,
                                     NFTeamLoader teamLoader,
                                     NFPersonLoader personLoader) {

    this.leagueLoader = leagueLoader;
    this.teamLoader = teamLoader;
    this.regionLoader = regionLoader;
    this.personLoader = personLoader;

  }

  public void runLoader(List<String> list) {

    if (list.contains("loadRegions")) {
      regionLoader.loadRegions();
    }
    if (list.contains("loadLeagues")) {
      leagueLoader.loadLeagueNodes();
    }
    if (list.contains("loadTeams")) {
      teamLoader.loadTeamNodes();
    }
    if (list.contains("loadPersons")) {
      personLoader.loadPersonNodes();
    }

    if (list.contains("loadLeagues")) {
      leagueLoader.loadLeagueRelationships();
    }
    if (list.contains("loadTeams")) {
      teamLoader.loadTeamRelationships();
    }
    if (list.contains("loadPersons")) {
      personLoader.loadPersonRelationships();
    }

    if (list.contains("loadAll")) {
      regionLoader.loadRegions();
      leagueLoader.loadLeagueNodes();
      teamLoader.loadTeamNodes();
      personLoader.loadPersonNodes();
      leagueLoader.loadLeagueRelationships();
      teamLoader.loadTeamRelationships();
      personLoader.loadPersonRelationships();
    }
  }
}
