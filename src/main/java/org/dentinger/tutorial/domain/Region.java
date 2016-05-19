package org.dentinger.tutorial.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Region {
  @GraphId
  private Long id;

  private String name;

  public Region() { super(); }

  public Region(Long region_id) {
    id = region_id;
  }

  public Region(Long rid, String rname) {
    id = rid;
    name=rname;
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

  @Override public String toString() {
    return "Region{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
