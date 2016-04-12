package org.dentinger.tutorial.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.domain.Team;
import org.dentinger.tutorial.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
public class SportsBallRepository {
  private static final Logger logger = LoggerFactory.getLogger(SportsBallRepository.class);
  private Environment environment;
  private List<Region> regionList;
  private Map<Long, List<League>> leagueMap; // key is region id
  private Map<Long, List<Team>> teamMap; // key is league id
  private Map<Long, List<Person>> personMap; // key is team id

  @Autowired
  public SportsBallRepository(Environment environment) {
    this.environment = environment;
    init();
  }

  public List<Region> getRegions() {
    return regionList;
  }

  public List<League> getLeagues() {
    return leagueMap.values().stream().flatMap(l -> l.stream()).distinct().collect(Collectors.toList());
  }

  public List<League> getLeagues(Region region) {
    return leagueMap.get(region.getId());
  }

  public List<Team> getTeams() {
    return teamMap.values().stream().flatMap(l -> l.stream()).distinct().collect(Collectors.toList());
  }

  public List<Team> getTeams(League league) {
    return teamMap.get(league.getId());
  }

  public List<Person> getPersons() {
    return personMap.values().stream().flatMap(l -> l.stream()).distinct().collect(Collectors.toList());
  }

  public List<Person> getPersons(Team team) {
    return personMap.get(team.getId());
  }

  private void init() {
    long start = System.currentTimeMillis();
    logger.info("Initializing SportsBall Repo ...");
    generateRegions();
    generateLeagues();
    generateTeams(); // Also generates persons
    logger.info("Initialization complete: {}ms",System.currentTimeMillis()-start);
  }

  private void generateRegions() {
    int regionCount = Integer.valueOf(environment.getRequiredProperty("regions.count"));
    regionList = new ArrayList<>();
    LongStream.range(1, regionCount + 1)
        .forEach(id -> {
          regionList.add(new Region(id, "Region-" + id));
        });
  }

  private void generateLeagues() {
    int leagueCount = Integer.valueOf(environment.getRequiredProperty("leagues.count"));
    int minRegionAffiliations = Integer.valueOf(environment.getRequiredProperty("leagues.minRegionAffiliations"));
    int maxRegionAffiliations = Integer.valueOf(environment.getRequiredProperty("leagues.maxRegionAffiliations"));
    leagueMap = new HashMap<>();
    LongStream.range(1, leagueCount + 1)
        .forEach(id -> {
          League league = new League(id, "League-" + id);
          getParentOffsets(regionList.size(), minRegionAffiliations, maxRegionAffiliations)
              .forEach(x -> {
                // logger.info("Adding league({}) to region({})",league.getId(),x);
                Long regionId = regionList.get(x.intValue()).getId();
                List<League> leagues = leagueMap.get(regionId);
                if( leagues == null){
                  leagues = new ArrayList<League>();
                  leagueMap.put(regionId,leagues);
                }
                leagues.add(league);
                league.addRegion(regionList.get(x.intValue()));
              });
        });
    // logger.info("leagueCount={}",leagueMap.values().stream().flatMap(l -> l.stream()).count());
  }

  private void generateTeams() {
    int teamCount = Integer.valueOf(environment.getRequiredProperty("teams.count"));
    int minLeagueMemberships = Integer.valueOf(environment.getRequiredProperty("teams.minLeagueMemberships"));
    int maxLeagueMemberships = Integer.valueOf(environment.getRequiredProperty("teams.maxLeagueMemberships"));
    int minPlayersPerTeam = Integer.valueOf(environment.getRequiredProperty("teams.minPlayersPerTeam"));
    int maxPlayersPerTeam = Integer.valueOf(environment.getRequiredProperty("teams.maxPlayersPerTeam"));
    teamMap = new HashMap<>();
    personMap = new HashMap<>();
    List<League> leagueList = getLeagues();
    Random rand = new Random(System.currentTimeMillis());
    LongStream.range(1, teamCount + 1)
        .forEach(id -> {
          Team team = new Team(id, generateName(rand, id));
          getParentOffsets(leagueList.size(), minLeagueMemberships, maxLeagueMemberships)
              .forEach(x -> {
                Long leagueId = leagueList.get(x.intValue()).getId();
                List<Team> teams = teamMap.get(leagueId);
                if( teams == null){
                  teams = new ArrayList<Team>();
                  teamMap.put(leagueId,teams);
                }
                teams.add(team);
                team.addLeague(leagueList.get(x.intValue()));
              });
          generatePlayers(team, minPlayersPerTeam, maxPlayersPerTeam);
        });
  }

  private void generatePlayers(Team team, int minPlayers, int maxPlayers) {
    int playerCount = (minPlayers == maxPlayers)?maxPlayers :
      new Random(System.currentTimeMillis()).ints(minPlayers, maxPlayers).limit(1).findFirst().getAsInt();
    LongStream.range(1, playerCount + 1)
        .forEach(id -> {
          Person person = new Person(id, "Player-"+id);
          List<Person> persons = personMap.get(team.getId());
          if( persons == null){
            persons = new ArrayList<Person>();
            personMap.put(team.getId(),persons);
          }
          persons.add(person);
          person.addTeam(team);
        });
  }

  private Stream<Long> getParentOffsets(long range, int minReturned, int maxReturned) {
    Random r = new Random(System.currentTimeMillis());
    int returnCount = (minReturned == maxReturned)? maxReturned :
        r.ints(minReturned, maxReturned).limit(1).findFirst().getAsInt();
    return r.longs(0, range).limit(returnCount).distinct().boxed();
  }

  private String[] adjs = new String[] {
      "Fast", "Big", "Mighty", "Super", "Shining", "Cold Hearted"
  };
  private String[] nouns = new String[]{
      "Dogs", "Pumas", "Jack Fruit Farmers", "Strikers", "Bashers"
  };

  private String generateName(Random rand, Long teamId){
    return adjs[rand.ints(1, 0, adjs.length).findFirst().getAsInt()] + " " +
        nouns[rand.ints(1, 0, nouns.length).findFirst().getAsInt()];
  }

}
