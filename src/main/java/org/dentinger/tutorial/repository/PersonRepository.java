package org.dentinger.tutorial.repository;

import java.util.List;
import org.dentinger.tutorial.domain.depricated.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends GraphRepository<Person> {

  Person findByName(String name);

  @Query(value = "MATCH (n:Person)-[:TEAMMATE]-(m0:Person) WHERE m0.name = { teammates_name } "
      + "MATCH (n)-[:TEAMMATE]-(m0) WITH n MATCH p=(n)-[*0..1]-(m) RETURN p, ID(n)")
  List<Person> findByTeammates_Name(String name);

}