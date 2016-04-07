package org.dentinger.tutorial.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.dentinger.tutorial.domain.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class RegionClient {
  private List<Region> regions;
  private int numRegions;

  @Autowired
  public RegionClient(Environment environment) {
    numRegions = Integer.valueOf(environment.getRequiredProperty("regions.count"));
  }

  public List<Region> getRegions() {
    regions = new ArrayList<>();
    LongStream.range(0, numRegions)
        .forEach(i -> {
          Long regionId = i+1;
          regions.add(new Region(regionId, "Region-" + regionId));
        });
    return regions;
  }
}
