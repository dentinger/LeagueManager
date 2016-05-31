package org.dentinger.tutorial.repository;

import java.util.List;
import java.util.UUID;
import org.dentinger.tutorial.domain.Person;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends GraphRepository<Person> {

  List<Person> findByName(@Param("name") String name);

  List<Person> findByNameLike(@Param("name") String name);

  Person findByUuid(@Param("uuid") UUID uuid);

  /*
  Person findByName(String name);

  @Query(value = "MATCH (n:Person)-[:TEAMMATE]-(m0:Person) WHERE m0.name = { teammates_name } "
      + "MATCH (n)-[:TEAMMATE]-(m0) WITH n MATCH p=(n)-[*0..1]-(m) RETURN p, ID(n)")
  List<Person> findByTeammates_Name(String name);
  */
}