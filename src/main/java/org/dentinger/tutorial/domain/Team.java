package org.dentinger.tutorial.domain;

import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Team {
  @GraphId
  private Long id;

  private String name;

  @Relationship(type = "PLAYSON", direction = Relationship.UNDIRECTED)
  private Set<Person> roster;

  @Relationship(type = "MEMBER_OF")
  private Set<League> leagues;

  public Team(){
    this.roster = new HashSet<>();
    this.leagues = new HashSet<>();
  }

  public Team(Long teamId, String teamName, Long leagueId){
    this();
    this.id = teamId;
    this.name = teamName;
    this.leagues.add(new League(leagueId));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Person> getRoster() {
    return roster;
  }

  public void setRoster(Set<Person> roster) {
    this.roster = roster;
  }

  public Set<League> getLeagues() {
    return leagues;
  }

  public void setLeagues(Set<League> leagues) {
    this.leagues = leagues;
  }

  @Override public String toString() {
    return "Team{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
