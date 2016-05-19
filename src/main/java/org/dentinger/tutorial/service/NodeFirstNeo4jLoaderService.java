package org.dentinger.tutorial.service;

import java.util.List;
import org.dentinger.tutorial.loader.nodefirst.NFLeagueLoader;
import org.dentinger.tutorial.loader.nodefirst.NFPersonLoader;
import org.dentinger.tutorial.loader.nodefirst.NFRegionLoader;
import org.dentinger.tutorial.loader.nodefirst.NFTeamLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NodeFirstNeo4jLoaderService {
  private Logger logger = LoggerFactory.getLogger(NodeFirstNeo4jLoaderService.class);
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

  public void cleanup(){
    logger.info("About to cleanup");
    regionLoader.cleanup();
    leagueLoader.cleanup();
    teamLoader.cleanup();
    personLoader.cleanup();
    logger.info("Cleanup complete");
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
      leagueLoader.loadLeagueRelationships();
      teamLoader.loadTeamNodes();
      teamLoader.loadTeamRelationships();
      personLoader.loadPersonNodes();
      personLoader.loadPersonRelationships();
    }
  }
}
