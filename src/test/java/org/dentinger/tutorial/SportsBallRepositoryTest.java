package org.dentinger.tutorial;

import java.util.List;
import java.util.Set;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Person;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.domain.Team;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.MockitoAnnotations.initMocks;

import org.springframework.mock.env.MockEnvironment;

import static org.junit.Assert.*;

public class SportsBallRepositoryTest {
  MockEnvironment mockEnvironment = new MockEnvironment();
  private SportsBallRepository sut;
  private String regionsCount = "20";
  private String leaguesCount = "3000";
  private String minRegionAffiliations = "1";
  private String maxRegionAffiliations = "3";
  private String teamsCount = "100000";
  private String minLeagueMemberships = "1";
  private String maxLeagueMemberships = "5";
  private String minPlayersPerTeam = "8";
  private String maxPlayersPerTeam = "12";

  @Before
  public void setup() {
    initMocks(this);
    mockEnvironment.setProperty("regions.count", regionsCount);
    mockEnvironment.setProperty("leagues.count",leaguesCount);
    mockEnvironment.setProperty("leagues.minRegionAffiliations",minRegionAffiliations);
    mockEnvironment.setProperty("leagues.maxRegionAffiliations",maxRegionAffiliations);
    mockEnvironment.setProperty("teams.count",teamsCount);
    mockEnvironment.setProperty("teams.minLeagueMemberships",minLeagueMemberships);
    mockEnvironment.setProperty("teams.maxLeagueMemberships",maxLeagueMemberships);
    mockEnvironment.setProperty("teams.minPlayersPerTeam",minPlayersPerTeam);
    mockEnvironment.setProperty("teams.maxPlayersPerTeam",maxPlayersPerTeam);

    sut = new SportsBallRepository(mockEnvironment);
  }

  @Test
  public void test_getRegions() {
    assertEquals(Integer.valueOf(regionsCount).intValue(), sut.getRegions().size());
  }

  @Test
  public void test_getAllLeagues() {
    assertEquals(Integer.valueOf(leaguesCount).intValue(), sut.getLeagues().size());
  }

  @Test
  public void test_verifyRegionsInLegaues() {
    List<League> leagueList = sut.getLeagues();
    int minRegions = Integer.valueOf(minRegionAffiliations).intValue();
    int maxRegions = Integer.valueOf(maxRegionAffiliations).intValue();
    leagueList.stream()
        .forEach(league -> {
          Set<Region> regions = league.getRegions();
          assertTrue(minRegions <= regions.size() && regions.size() <= maxRegions);
        });
  }

  @Test
  public void test_getAllTeams() {
    assertEquals(Integer.valueOf(teamsCount).intValue(), sut.getTeams().size());
  }

  @Test
  public void test_verifyLeaguesInTeams() {
    List<Team> teamList = sut.getTeams();
    int minLeagues = Integer.valueOf(minLeagueMemberships).intValue();
    int maxLeagues = Integer.valueOf(maxLeagueMemberships).intValue();
    teamList.stream()
        .forEach(team -> {
          Set<League> leagues = team.getLeagues();
          assertTrue(minLeagues <= leagues.size() && leagues.size() <= maxLeagues);
        });
  }

  @Test
  public void test_getAllPersons() {
    List<Team> teamList = sut.getTeams();
    Long min = Long.valueOf(minPlayersPerTeam).longValue();
    Long max = Long.valueOf(maxPlayersPerTeam).longValue();
    int playerCount = sut.getPersons().size();
    assertTrue((min * teamList.size()) <= playerCount && (max * teamList.size()) >= playerCount);
  }

  @Test
  public void test_verifyPersonsOnTeams() {
    List<Team> teamList = sut.getTeams();
    Long min = Long.valueOf(minPlayersPerTeam).longValue();
    Long max = Long.valueOf(maxPlayersPerTeam).longValue();
    teamList.stream()
        .forEach(team -> {
          List<Person> persons = sut.getPersons(team);
          assertTrue(min <= persons.size() && persons.size() <= max);
        });
  }
}
