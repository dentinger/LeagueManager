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

  @Relationship(type = "SANCTION")
  private Set<Region> regions;

  public League() {
    super();
    regions = new HashSet<>();
  }

  public League(Long id) {
    this();
    this.id = id;
  }

  public League(Long id, String name) {
    this();
    this.id = id;
    this.name = name;
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

  public Set<Region> getRegions() {
    return regions;
  }

  public void setRegions(Set<Region> regions) {
    this.regions = regions;
  }

  public void addRegion(Region region) {
    regions.add(region);
  }

  @Override public String toString() {
    return "League{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", region count=" + regions.size() +
        '}';
  }
}
