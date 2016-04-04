package org.dentinger.tutorial;

import java.util.Arrays;
import java.util.List;
import org.dentinger.tutorial.loader.LeagueLoader;
import org.dentinger.tutorial.loader.RegionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories(basePackages = "org.dentinger.tutorial.repository")
public class Neo4jDemoApplication {

  private final static Logger log = LoggerFactory.getLogger(Neo4jDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(Neo4jDemoApplication.class, args);
	}
  
	@Bean CommandLineRunner runLoader(RegionLoader regionLoader, LeagueLoader leagueLoader){
		return args -> {
			List<String> list = Arrays.asList(args);
      if( list.contains("loadRegions")){
        regionLoader.loadRegions();
      }
      if( list.contains("loadLeagues")){
        leagueLoader.loadLeagues();
      }
		};
	}


}
