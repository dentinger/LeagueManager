package org.dentinger.tutorial.domain;

import java.util.Date;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Gym {
  @GraphId
  private Long id;

  private String name;

  private Date openTime;

  private Date closeTime;

  private boolean badPartOfTown;

  private int numberOfCourts;

  public Gym() { super();}
  public Gym(Long id, String name) {
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

  public Date getOpenTime() {
    return openTime;
  }

  public void setOpenTime(Date openTime) {
    this.openTime = openTime;
  }

  public Date getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(Date closeTime) {
    this.closeTime = closeTime;
  }

  public boolean isBadPartOfTown() {
    return badPartOfTown;
  }

  public void setBadPartOfTown(boolean badPartOfTown) {
    this.badPartOfTown = badPartOfTown;
  }

  public int getNumberOfCourts() {
    return numberOfCourts;
  }

  public void setNumberOfCourts(int numberOfCourts) {
    this.numberOfCourts = numberOfCourts;
  }
}
