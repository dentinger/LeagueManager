package org.dentinger.tutorial.client.dto;

public class LeagueDTO {
  private Long id;
  private Long regionId;
  private String name;

  public LeagueDTO(Long id, Long regionId, String name) {
    this.id = id;
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

  @Override public String toString() {
    return "LeagueDTO{" +
        "id=" + id +
        ", regionId=" + regionId +
        ", name='" + name + '\'' +
        '}';
  }
}
