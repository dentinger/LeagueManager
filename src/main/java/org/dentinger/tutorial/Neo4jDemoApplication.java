package org.dentinger.tutorial;

import java.util.Arrays;
import java.util.List;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.domain.depricated.Person;
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


}
