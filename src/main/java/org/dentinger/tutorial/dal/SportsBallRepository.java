package org.dentinger.tutorial.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Person;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.domain.Team;
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
  private static final AtomicLong personIdGenerator = new AtomicLong(0);

  @Autowired
  public SportsBallRepository(Environment environment) {
    this.environment = environment;
    init();
  }

  public List<Region> getRegions() {
    return regionList;
  }

  public List<League> getLeagues() {
    return leagueMap.values().stream().flatMap(l -> l.stream()).distinct()
        .collect(Collectors.toList());
  }

  public Optional<List<League>> getLeagues(Region region) {

    return Optional.ofNullable(leagueMap.get(region.getId()));
  }

  public List<Team> getTeams() {
    return teamMap.values().stream().flatMap(l -> l.stream()).distinct()
        .collect(Collectors.toList());
  }

  public Optional<List<Team>> getTeams(League league) {

    return Optional.ofNullable(teamMap.get(league.getId()));
  }

  public List<Person> getPersons() {
    return personMap.values().stream().flatMap(l -> l.stream()).distinct()
        .collect(Collectors.toList());
  }

  public Optional<List<Person>> getPersons(Team team) {

    return Optional.ofNullable(personMap.get(team.getId()));
  }

  private void init() {
    long start = System.currentTimeMillis();
    logger.info("Initializing SportsBall Repo ...");
    generateRegions();
    generateLeagues();
    generateTeams(); // Also generates persons
    logger.info("Initialization complete: {}ms", System.currentTimeMillis() - start);
  }

  private void generateRegions() {
    int regionCount = Integer.valueOf(environment.getRequiredProperty("regions.count"));
    regionList = new ArrayList<>();
    LongStream.range(1, regionCount + 1)
        .forEach(id -> regionList.add(new Region(id, "Region-" + id)));
  }

  private void generateLeagues() {
    int leagueCount = Integer.valueOf(environment.getRequiredProperty("leagues.count"));
    int minRegionAffiliations = Integer
        .valueOf(environment.getRequiredProperty("leagues.minRegionAffiliations"));
    int maxRegionAffiliations = Integer
        .valueOf(environment.getRequiredProperty("leagues.maxRegionAffiliations"));
    leagueMap = new HashMap<>();
    LongStream.range(1, leagueCount + 1)
        .forEach(id -> {
          League league = new League(id, "League-" + id);
          getParentOffsets(regionList.size(), minRegionAffiliations, maxRegionAffiliations)
              .forEach(x -> {
                // logger.info("Adding league({}) to region({})",league.getId(),x);
                Long regionId = regionList.get(x.intValue()).getId();
                List<League> leagues = leagueMap.get(regionId);
                if (leagues == null) {
                  leagues = new ArrayList<>();
                  leagueMap.put(regionId, leagues);
                }
                leagues.add(league);
                league.addRegion(regionList.get(x.intValue()));
              });
        });
    // logger.info("leagueCount={}",leagueMap.values().stream().flatMap(l -> l.stream()).count());
  }

  private void generateTeams() {
    int teamCount = Integer.valueOf(environment.getRequiredProperty("teams.count"));
    int minLeagueMemberships = Integer
        .valueOf(environment.getRequiredProperty("teams.minLeagueMemberships"));
    int maxLeagueMemberships = Integer
        .valueOf(environment.getRequiredProperty("teams.maxLeagueMemberships"));
    int minPlayersPerTeam = Integer
        .valueOf(environment.getRequiredProperty("teams.minPlayersPerTeam"));
    int maxPlayersPerTeam = Integer
        .valueOf(environment.getRequiredProperty("teams.maxPlayersPerTeam"));
    teamMap = new HashMap<>();
    personMap = new HashMap<>();
    List<League> leagueList = getLeagues();
    Random rand = new Random();
    LongStream.range(1, teamCount + 1)
        .forEach(id -> {
          Team team = new Team(id, generateName(rand, id));
          getParentOffsets(leagueList.size(), minLeagueMemberships, maxLeagueMemberships)
              .forEach(x -> {
                Long leagueId = leagueList.get(x.intValue()).getId();
                List<Team> teams = teamMap.get(leagueId);
                if (teams == null) {
                  teams = new ArrayList<Team>();
                  teamMap.put(leagueId, teams);
                }
                teams.add(team);
                team.addLeague(leagueList.get(x.intValue()));
              });
          generatePlayers(team, minPlayersPerTeam, maxPlayersPerTeam);
        });
  }

  private void generatePlayers(Team team, int minPlayers, int maxPlayers) {
    Random rand = new Random();
    int playerCount = (minPlayers == maxPlayers) ? maxPlayers :
        rand.ints(minPlayers, maxPlayers).limit(1).findFirst()
            .getAsInt();
    LongStream.range(1, playerCount + 1)
        .forEach(id -> {
          Person person = new Person(personIdGenerator.incrementAndGet(), UUID.randomUUID(), generatePlayerName(rand));
          List<Person> persons = personMap.get(team.getId());
          if (persons == null) {
            persons = new ArrayList<>();
            personMap.put(team.getId(), persons);
          }
          persons.add(person);
          person.addTeam(team);
        });
  }

  private Stream<Long> getParentOffsets(long range, int minReturned, int maxReturned) {
    Random r = new Random(System.currentTimeMillis());
    int returnCount = (minReturned == maxReturned) ? maxReturned :
        r.ints(minReturned, maxReturned).limit(1).findFirst().getAsInt();
    return r.longs(0, range).limit(returnCount).distinct().boxed();
  }

  private String[] adjs = new String[]{
      "Fast", "Big", "Mighty", "Super", "Shining", "Cold Hearted", "Free Range", "Thundering ",
      "Purple", "Great Green", "Lovely", "Yellow", "Stuffed", "Flat", "Dirty",
      "Tiny", "Alpha", "Omega", "Vegetarian", "Pirate"
  };
  private String[] nouns = new String[]{
      "Dogs", "Pumas", "Jack Fruit Farmers", "Strikers", "Bashers", "Guavateers", "Herd", "Badgers",
      "Spiders", "Pulled Pork Pirates", "Apes", "Hobos", "Pictures", "Games", "Bed", "Basket",
      "Flower", "Apples", "Chips", "Nuubs", "Pros"
  };

  private String[] firstNames = new String[]{
      "Tomi  ", "Vinnie  ", "Alicia  ", "Brandie  ", "Madlyn  ", "Sandi  ", "Ariana  ", "Otilia  ",
      "Jeanice  ", "Alonso  ", "Chere  ", "Beverlee  ", "Johana  ", "Korey  ", "Mabel  ",
      "Earnest  ", "Lelia  ", "Freeda  ", "Cherri  ", "Bud  ", "Stasia  ", "Jerald  ", "Alona  ",
      "Joann  ", "Damian  ", "Nancee  ", "Bari  ", "Ludie  ", "Honey  ", "Marnie  ", "Maryln  ",
      "Stephine  ", "Teena  ", "Sherrill  ", "Cleta  ", "Darin  ", "Leisha  ", "Shemeka  ",
      "Katina  ", "Coy  ", "Jackeline  ", "Aja  ", "Kathline  ", "Arminda  ", "Demarcus  ",
      "Edmund  ", "Kurt  ", "Christoper  ", "Ami  ", "Tandy", "Candie  ", "Lawerence  ",
      "Terrance  ", "Martha  ", "Marian  ", "Kennith  ", "Mariano  ", "Randee  ", "Slyvia  ",
      "Felipa  ", "Christeen  ", "Lacy  ", "Bertie  ", "Versie  ", "Jamar  ", "Florene  ",
      "Awilda  ", "Donna  ", "Tarah  ", "Kelsey  ", "Hillary  ", "Huong  ", "Yanira  ", "Scarlet  ",
      "Chanelle  ", "Maryrose  ", "Shea  ", "Seymour  ", "Sudie  ", "Courtney  ", "Tamiko  ",
      "Gwyn  ", "Dolores  ", "Roosevelt  ", "Maxine  ", "Robena  ", "Aisha  ", "Enriqueta  ",
      "Hassie  ", "Devon  ", "Catherin  ", "Lucien  ", "Augustus  ", "Deidra  ", "Parthenia  ",
      "Anibal  ", "Mariela  ", "Sabrina  ", "Eugena ", "Retha  ", "Wilhemina  ", "Matt  ",
      "Kayleen  ", "Novella  ", "Antonette  ", "Melba  ", "Ranee  ", "Mariel  ", "Vincenzo  ",
      "Denis  ", "Katheryn  ", "Amparo  ", "Shannon  ", "Grazyna  ", "Mercedes  ", "Carman  ",
      "Celestina  ", "Claretta  ", "Colene  ", "Eli  ", "Brittani  ", "Chi  ", "Kellie  ",
      "Chong  ", "Mara  ", "Lyda  ", "Trinh  ", "Marisol  ", "Darcie  ", "Barb  ", "Mendy  ",
      "Roland  ", "Ermelinda  ", "Kandice  ", "Benton  ", "Charline  ", "Katie  ", "Sheilah  ",
      "Verlene  ", "Calista  ", "Cordia  ", "Chery  ", "Lawanda  ", "Eloise  ", "Monica  ",
      "Jody  ", "Janita  ", "Lavonne  ", "Dorthey  ", "Rex  ", "Nydia  ", "Vanda  ", "Long  ",
      "Enoch  ", "Sherron  ", "Gary  ", "Jarred  ", "Herma  ", "Consuela  ", "Janett  ", "Ned  ",
      "Ahmed  ", "Virginia  ", "Sheri  ", "Caron  ", "Juliana  ", "Vernie  ", "Fredricka  ",
      "Dudley  ", "Ethelyn  ", "Garry  ", "Hellen  ", "Wilton  ", "Clarinda  ", "Synthia  ",
      "Catherina  ", "Eddie  ", "Carmina  ", "Theresa  ", "Sheena  ", "Akilah  ", "Lewis  ",
      "Jacob  ", "Barbara  ", "Nolan  ", "Ashanti  ", "Thanh  ", "Kaylee  ", "Alexis  ", "Lyman  ",
      "Hae  ", "Christi  ", "Marlena  ", "Colin  ", "Enola  ", "Enid  ", "Leticia  ", "Ron ",
      "Dan ", "Shawn ", "Erik", "Tony ", "Gerald ", "Jane ", "Michael ", "Mike ", "Joseph ",
      "Wesley ", "Haley ", "Maynard "
  };
  private String[] lastNames = new String[]{
      "Barker", "Morales", "Garrison", "Ayala", "Sutton", "Delacruz", "Olsen", "Stephenson",
      "Adkins", "Tran", "Hicks", "Lara", "Barrett", "Warren", "Harper", "Reyes", "Liu", "Best",
      "Robinson", "Davies", "Wood", "Mcconnell", "Galloway", "Whitehead", "Christensen", "Moon",
      "Good", "Michael", "Peterson", "Gibbs", "Clayton", "Mccoy", "Chandler", "French", "Reynolds",
      "Carrillo", "Duncan", "Faulkner", "Harrell", "Williams", "Sweeney", "Daniels", "Abbott",
      "Castaneda", "Brandt", "Hendricks", "Rodriguez", "Whitaker", "Strong", "Dillon", "Manning",
      "Sanchez", "Armstrong", "Clay", "Richmond", "Pittman", "Hooper", "Strickland", "Wiggins",
      "Santana", "Shaw", "Bryan", "Davila", "Mullins", "Archer", "Avila", "Turner", "Curtis",
      "Mckee", "Hopkins", "Moody", "Herrera", "Wagner", "Wiley", "Brennan", "Black", "Brown",
      "Schroeder", "Yates", "Downs", "Schmitt", "Preston", "Berg", "Dalton", "Sims", "Crane",
      "Buchanan", "Leonard", "Bender", "Carpenter", "Fischer", "Berger", "Greene", "Rice", "Hebert",
      "Shelton", "Espinoza", "Jennings", "Watson", "Carroll", "Mcdaniel", "Compton", "Molina",
      "Kaufman", "Lopez", "Collier", "Garrett", "Conrad", "Stokes", "Baker", "Ayers", "Mejia",
      "Holland", "Love", "Nielsen", "Mckenzie", "Powers", "Roy", "Whitney", "Smith", "Garner",
      "Padilla", "Frost", "Singh", "Bradshaw", "Flowers", "Sheppard", "Foster", "Henderson",
      "Glass", "Sparks", "Clark", "Parks", "Bradford", "Giles", "Webster", "Hull", "Walker",
      "Logan", "Mcmahon", "Mata", "Watkins", "Huber", "Patterson", "Case", "Jordan", "Fitzpatrick",
      "Howell", "Monroe", "Velasquez", "Ware", "Porter", "Potts", "Swanson", "Estes", "Steele",
      "Conner", "Kidd", "Dunn", "Yoder", "Shepherd", "Benitez", "Mclaughlin", "English", "Li",
      "Rollins", "Bray", "Malone", "Matthews", "Ellis", "Campbell", "Roach", "Perkins", "Bryant",
      "Campos", "Klein", "Lozano", "Rowe", "Brock", "Jordan", "Mercer", "Robinson", "Ewing",
      "Duncan"
  };

  private String generateName(Random rand, Long teamId) {
    return adjs[rand.ints(1, 0, adjs.length).findFirst().getAsInt()] + " " +
        nouns[rand.ints(1, 0, nouns.length).findFirst().getAsInt()];
  }

  private String generatePlayerName(Random rand) {
    return firstNames[rand.ints(1, 0, firstNames.length).findFirst().getAsInt()] + " " +
        lastNames[rand.ints(1, 0, lastNames.length).findFirst().getAsInt()];
  }

}
