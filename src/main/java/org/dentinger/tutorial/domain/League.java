package org.dentinger.tutorial.domain;

import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class League {

  @GraphId
  private Long id;

  private String name;

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

  public Set<Team> getMembers() {
    return members;
  }

  public void setMembers(Set<Team> members) {
    this.members = members;
  }

  public Set<Gym> getVenues() {
    return venues;
  }

  public void setVenues(Set<Gym> venues) {
    this.venues = venues;
  }

  public Set<Region> getRegions() {
    return regions;
  }

  public void setRegions(Set<Region> regions) {
    this.regions = regions;
  }

  @Relationship(type = "MEMBER_OF", direction = Relationship.UNDIRECTED)
  private Set<Team> members;

  @Relationship(type = "USES")
  private Set<Gym> venues;

  @Relationship(type = "UNDER_A")
  private Set<Region> regions;

  public League() {
    super();

    regions = new HashSet<>();
    venues = new HashSet<>();
    members = new HashSet<>();
  }
  public League(Long id){
    this();
    this.id = id;
  }
  public League(Long id, Long region, String name) {
    this();
    this.id = id;
    this.name = name;
    regions.add(new Region(region) );
  }
  public League(Long id, Region region, String name) {
    this();
    this.id = id;
    this.name = name;
    regions.add(region);
  }
}
