package org.dentinger.tutorial;

import java.util.Arrays;
import java.util.List;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.domain.depricated.Person;
import org.dentinger.tutorial.loader.LeagueLoader;
import org.dentinger.tutorial.loader.RegionLoader;
import org.dentinger.tutorial.repository.PersonRepository;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.transaction.Transaction;
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

//	@Bean CommandLineRunner demo(PersonRepository personRepository,
//                               SessionFactory sessionFactory, Neo4jProperties neo4jProperties) {
//
//    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
//        neo4jProperties.getUsername(), neo4jProperties.getPassword());
//
//		return args -> {
//
//			Person greg = new Person("Greg");
//			Person roy = new Person("Roy");
//			Person craig = new Person("Craig");
//
//			List<Person> team = Arrays.asList(greg, roy, craig);
//
//			log.info("Before linking up with Neo4j...");
//
//			team.stream()
//					.forEach(person -> log.info("\t" + person.toString()));
//
//			Transaction tx = session.beginTransaction();
//			try {
//				personRepository.save(greg);
//				personRepository.save(roy);
//				personRepository.save(craig);
//
//				greg = personRepository.findByName(greg.getName());
//				greg.worksWith(roy);
//				greg.worksWith(craig);
//				personRepository.save(greg);
//
//				roy = personRepository.findByName(roy.getName());
//				roy.worksWith(craig);
//				// We already know that roy works with greg
//				personRepository.save(roy);
//
//				// We already know craig works with roy and greg
//
//
//				log.info("Lookup each person by name...");
//				team.stream()
//						.forEach(person ->
//								log.info("\t" + personRepository
//										.findByName(person.getName()).toString()));
//
//
//				log.info("Lookup each person by teammate...");
//				for (Person person : team) {
//					log.info(person.getName() + " is a teammate of...");
//					personRepository.findByTeammates_Name(person.getName()).stream()
//							.forEach(teammate -> log.info("\t" + teammate.getName()));
//				}
//
//			//	tx.commit();
//			} finally {
////				tx.close();
//			}
//		};
//	}
}
