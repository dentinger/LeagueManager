package org.dentinger.tutorial.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.GraphRepositoryImpl;
import org.springframework.data.neo4j.template.Neo4jOperations;

/**
 */
public class CustomGraphRepositoryImpl<T> extends GraphRepositoryImpl<T> {

  public CustomGraphRepositoryImpl(Class<T> clazz,
                                   Neo4jOperations neo4jOperations) {
    super(clazz, neo4jOperations);
  }

  @Override public Page<T> findAll(Pageable pageable) {
    // override default of 1
    return super.findAll(pageable, 0);
  }
}
