package org.dentinger.tutorial.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.dentinger.tutorial.domain.Gym;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class VenueClient {

  List<Gym> venues;
  private int gymsPerLeague;
  private int totalVenues;

  @Autowired
  public VenueClient(Environment environment) {
    gymsPerLeague = Integer.valueOf(environment.getRequiredProperty("venues.count.maxPerLeague"));
    totalVenues = Integer.valueOf(environment.getRequiredProperty("venues.count"));
  }

  public List<Gym> getRegions() {
    venues = new ArrayList<>();
    LongStream.range(0, gymsPerLeague)
        .forEach(i -> {
          Long regionId = i+1;
          venues.add(new Gym(regionId, "Gym" + regionId));
        });
    return venues;
  }


}
