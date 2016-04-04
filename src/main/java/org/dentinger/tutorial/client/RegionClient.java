package org.dentinger.tutorial.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.dentinger.tutorial.client.dto.RegionDTO;
import org.dentinger.tutorial.domain.League;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class RegionClient {
  private List<RegionDTO> regions;
  private int numRegions;

  @Autowired
  public RegionClient(Environment environment) {
    numRegions = Integer.valueOf(environment.getRequiredProperty("region.count"));
  }

  public List<RegionDTO> getRegions() {
    regions = new ArrayList<>();
    LongStream.range(0, numRegions)
        .forEach(i -> {
          Long regionId = i+1;
          regions.add(new RegionDTO(regionId, "Region" + regionId));
        });
    return regions;
  }
}
