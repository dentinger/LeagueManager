package org.dentinger.tutorial.repository;

import org.dentinger.tutorial.domain.Region;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface RegionRepository extends GraphRepository<Region> {
}