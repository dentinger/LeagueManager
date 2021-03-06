package org.dentinger.tutorial.repository;

import java.util.List;
import java.util.UUID;
import org.dentinger.tutorial.domain.Person;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends GraphRepository<Person> {
  Person findByPersonId(@Param("personId") Long personId);
  Person findByUuid(@Param("uuid") UUID uuid);
  List<Person> findByName(@Param("name") String name);
  List<Person> findByNameLike(@Param("name") String name);
}