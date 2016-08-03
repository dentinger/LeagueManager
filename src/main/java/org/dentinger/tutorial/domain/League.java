package org.dentinger.tutorial.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class League {

  @GraphId
  private Long id;
  private Long leagueId;
  private String name;
  @Relationship(type = "SANCTION", direction = Relationship.UNDIRECTED)
  private Set<Region> regions;
  @JsonBackReference
  @Relationship(type = "MEMBERSHIP", direction = Relationship.UNDIRECTED)
  private Set<Team> teams;

  public League() {
    this.regions = new HashSet<>();
    this.teams = new HashSet<>();
  }

  public League(Long leagueId, String name) {
    this();
    this.leagueId = leagueId;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getLeagueId() {
    return leagueId;
  }

  public void setLeagueId(Long leagueId) {
    this.leagueId = leagueId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Region> getRegions() {
    return regions;
  }

  public void setRegions(Set<Region> regions) {
    this.regions = regions;
  }

  public Set<Team> getTeams() {
    return teams;
  }

  public void setTeams(Set<Team> teams) {
    this.teams = teams;
  }

  public void addRegion(Region region) {
    regions.add(region);
  }

  @Override
  public String toString() {
    return "League{" +
        "id=" + id +
        ", leagueId=" + leagueId +
        ", name='" + name + '\'' +
        ", region count=" + regions.size() +
        '}';
  }
}
