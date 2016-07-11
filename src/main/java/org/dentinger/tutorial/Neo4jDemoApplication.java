package org.dentinger.tutorial;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.util.Arrays;
import java.util.List;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.loader.NodeIndexes;
import org.dentinger.tutorial.service.Neo4jLoaderService;
import org.dentinger.tutorial.service.NodeFirstNeo4jLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
public class Neo4jDemoApplication {

  private final static Logger logger = LoggerFactory.getLogger(Neo4jDemoApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(Neo4jDemoApplication.class, args);
  }

  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.filters(new SimpleFilterProvider().setFailOnUnknownId(false));
    return builder;
  }

  @Bean
  CommandLineRunner runLoader(Neo4jLoaderService service,
                                    NodeFirstNeo4jLoaderService nodeFirstNeo4jLoaderService,
                                    SportsBallRepository sportsBallRepository,
                                    NodeIndexes nodeIndexes) {
    return args -> {
      List<String> list = Arrays.asList(args);
      sportsBallRepository.init(list);
      logger.debug("Running LeagueManager loading process");
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
