package org.dentinger.tutorial.domain;

import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Region {

  @GraphId
  private Long id;
  private Long regionId;
  private String name;
  @Relationship(type = "SANCTION", direction = Relationship.UNDIRECTED)
  private Set<League> leagues;

  ///

  public Region() {
    this.leagues = new HashSet<>();
  }

  public Region(Long regionId, String name) {
    this();
    this.regionId = regionId;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Region{" +
        "id=" + id +
        ", regionId=" + regionId +
        ", name='" + name + '\'' +
        '}';
  }
}
