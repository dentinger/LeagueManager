package org.dentinger.tutorial;

import java.util.Arrays;
import java.util.List;
import org.dentinger.tutorial.loader.NodeIndexes;
import org.dentinger.tutorial.service.Neo4jLoaderService;
import org.dentinger.tutorial.service.NodeFirstNeo4jLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "org.dentinger.tutorial.repository")
public class Neo4jDemoApplication {

  private final static Logger logger = LoggerFactory.getLogger(Neo4jDemoApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(Neo4jDemoApplication.class, args).close();
  }

  @Bean CommandLineRunner runLoader(Neo4jLoaderService service,
                                    NodeFirstNeo4jLoaderService nodeFirstNeo4jLoaderService,
                                    NodeIndexes nodeIndexes) {
    return args -> {
      logger.debug("Running LeagueManager loading process");
      List<String> list = Arrays.asList(args);
      if (!list.contains("nodeFirst")) {
        if (list.contains("cleanup")) {
          nodeFirstNeo4jLoaderService.cleanup();
        }
        nodeIndexes.createIndexes();
        service.runCombinedUnwindLoader(list);
      }
      if (list.contains("nodeFirst")) {
        if (list.contains("cleanup")) {
          nodeFirstNeo4jLoaderService.cleanup();
        }
        nodeIndexes.createIndexes();
        nodeFirstNeo4jLoaderService.runLoader(list);
      }
      logger
          .debug("Compeleted LeagueManager loading process -------  Enjoy your SPORTSBALL Season ");
    };
  }

}
