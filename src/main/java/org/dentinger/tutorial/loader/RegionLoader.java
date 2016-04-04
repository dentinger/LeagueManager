package org.dentinger.tutorial.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.client.RegionClient;
import org.dentinger.tutorial.client.dto.RegionDTO;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class RegionLoader {
  private static Logger logger = LoggerFactory.getLogger(RegionLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private RegionClient regionClient;

  private String MERGE_REGIONS =
      "unwind {json} as q " +
          "merge (r:Region {id: q.id})" +
          "  on create set r.name = q.name " +
          "  on match set r.name = q.name";

  @Autowired
  public RegionLoader(Neo4jProperties neo4jProperties, SessionFactory sessionFactory, RegionClient regionClient) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.regionClient = regionClient;
  }

  public void loadRegions() {
    Neo4jTemplate neo4jTemplate = getNeo4jTemplate();
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());
    List<RegionDTO> regions = regionClient.getRegions();
    logger.info("About to load {} regions", regions.size());
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("json", regions);
    long start = System.currentTimeMillis();
    try {
      neo4jTemplate.execute(MERGE_REGIONS, map);
    } catch (Exception e) {
      aeLogger.error("Unable to update graph, regionCount={}", regions.size(), e);
    }
    logger.info("Load complete: {}ms", System.currentTimeMillis() - start);
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }
}
