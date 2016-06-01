package org.dentinger.tutorial.repository;

import org.dentinger.tutorial.domain.Team;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TeamRepository extends GraphRepository<Team> {
  Team findByTeamId(@Param("teamId") Long teamId);
}