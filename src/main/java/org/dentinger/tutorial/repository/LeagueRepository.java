package org.dentinger.tutorial.repository;

import org.dentinger.tutorial.domain.League;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LeagueRepository extends GraphRepository<League> {
  League findByLeagueId(@Param("leagueId") Long leagueId);
}