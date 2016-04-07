package org.dentinger.tutorial.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.domain.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class TeamClient {
  List<Team> teams;
  private int count;
  private int maxLeagueMemberships;

  private String[] adjs = new String[] {
      "Fast", "Big", "Mighty", "Super", "Shining", "Cold Hearted"
  };
  private String[] nouns = new String[]{
      "Dogs", "Pumas", "Jack Fruit Farmers", "Strikers", "Bashers"
  };

  @Autowired
  public TeamClient(Environment environment) {
    count = Integer.valueOf(environment.getRequiredProperty("teams.count"));
    maxLeagueMemberships = Integer.valueOf(environment.getRequiredProperty("teams.maxLeagueMemberships"));
  }

  public List<Team> getTeams(League league) {
    Random rand = new Random(System.currentTimeMillis());
    List<Team> teams = new ArrayList<>();
//    Long teamIdSeed = league.getId() * perLeague;
//    LongStream.range(0, perLeague)
//        .forEach(i -> {
//          Long teamId = (league.getId() * perLeague) + i;
//          teams.add(new Team(teamId, generateName(rand, teamId), league.getId()));
//        });
    return teams;
  }

  private String generateName(Random rand, Long teamId){
    return adjs[rand.ints(1, 0, adjs.length).findFirst().getAsInt()] + " " +
        nouns[rand.ints(1, 0, nouns.length).findFirst().getAsInt()];
  }
}
