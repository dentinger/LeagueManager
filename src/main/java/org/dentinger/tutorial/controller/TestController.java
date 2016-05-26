package org.dentinger.tutorial.controller;

import java.util.ArrayList;
import java.util.List;
import org.dentinger.tutorial.loader.NodeIndexes;
import org.dentinger.tutorial.service.NodeFirstNeo4jLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RestController
public class TestController {
  private final static Logger logger = LoggerFactory.getLogger(TestController.class);
  private NodeFirstNeo4jLoaderService nodeFirstNeo4jLoaderService;
  private NodeIndexes nodeIndexes;

  @Autowired
  public TestController(NodeFirstNeo4jLoaderService nodeFirstNeo4jLoaderService,
                        NodeIndexes nodeIndexes) {

    this.nodeFirstNeo4jLoaderService = nodeFirstNeo4jLoaderService;
    this.nodeIndexes = nodeIndexes;
  }

  @RequestMapping(value = "/leaguemanager/load", method = RequestMethod.GET)
  public void doSomething(@RequestParam(value = "nodeFirst", required = false) String nodeFirst,
                          @RequestParam(value = "cleanup", required = false) String cleanup,
                          @RequestParam(value = "loadAll", required = false) String loadAll,
                          @RequestParam(value = "loadRegions", required = false) String loadRegions,
                          @RequestParam(value = "loadLeagues", required = false) String loadLeagues,
                          @RequestParam(value = "loadTeams", required = false) String loadTeams,
                          @RequestParam(value = "loadPersons", required = false) String loadPersons) {

      logger.debug("Running LeagueManager loading process");

      List list = new ArrayList<>();

    if( isNotBlank(loadRegions) ) { list.add(loadRegions); }
    if( isNotBlank(loadAll) ) { list.add(loadAll); }
    if( isNotBlank(loadLeagues) ) { list.add(loadLeagues); }
    if( isNotBlank(loadTeams) ) { list.add(loadTeams); }
    if( isNotBlank(loadPersons) ) { list.add(loadPersons); }

    if (isNotBlank(nodeFirst)) {
        if (isNotBlank(cleanup)) {
          nodeFirstNeo4jLoaderService.cleanup();
        }
        nodeIndexes.createIndexes();
        nodeFirstNeo4jLoaderService.runLoader(list);
      }
      logger
          .debug("Compeleted LeagueManager loading process -------  Enjoy your SPORTSBALL Season ");
  }
}
