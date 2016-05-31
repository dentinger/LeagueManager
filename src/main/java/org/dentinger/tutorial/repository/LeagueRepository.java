package org.dentinger.tutorial.repository;

import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Team;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LeagueRepository extends GraphRepository<League> {
}