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


}
