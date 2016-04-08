package org.dentinger.tutorial;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.dentinger.tutorial.dal.LeagueRepository;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.domain.Region;
import org.dentinger.tutorial.domain.Team;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.Assert.*;

public class LeagueRepositoryTest {
  MockEnvironment mockEnvironment = new MockEnvironment();
  private LeagueRepository sut;
  private String regionsCount = "20";
  private String leaguesCount = "3000";
  private String minRegionAffiliations = "1";
  private String maxRegionAffiliations = "3";
  private String teamsCount = "100000";
  private String minLeagueMemberships = "1";
  private String maxLeagueMemberships = "5";

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

    sut = new LeagueRepository(mockEnvironment);
  }

  @Test
  public void test_getRegions() {
    assertEquals(sut.getRegions().size(), Integer.valueOf(regionsCount).intValue());
  }

  @Test
  public void test_getAllLeagues() {
    assertEquals(sut.getLeagues().size(), Integer.valueOf(leaguesCount).intValue());
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
    assertEquals(sut.getTeams().size(), Integer.valueOf(teamsCount).intValue());
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
}
