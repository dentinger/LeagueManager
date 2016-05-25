package org.dentinger.tutorial.loader.nodefirst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.dentinger.tutorial.domain.League;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.dentinger.tutorial.util.RetriableTask;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class LeagueWorker {

  private static Logger logger = LoggerFactory.getLogger(LeagueWorker.class);
  private SessionFactory sessionFactory;
  private final AtomicLong recordsWritten = new AtomicLong(0);

  @Autowired
  public LeagueWorker(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession();
    return new Neo4jTemplate(session);
  }

  @Async("leagueProcessorThreadPool")
  public void doSubmitableWork(AggregateExceptionLogger aeLogger,
                                List<League> leagues,
                                String cypher) {
    Neo4jTemplate
        neo4jTemplate = getNeo4jTemplate();

    logger.debug("About to process {} leagues ", leagues.size());
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("json", leagues);
    try {
      new RetriableTask().retries(3).delay(50, MILLISECONDS).execute(() -> {
        neo4jTemplate.execute(cypher, map);
        recordsWritten.addAndGet(leagues.size());
      });
    } catch (Exception e) {
      aeLogger
          .error("Unable to update graph, leagueCount={}", leagues.size(),
              e);
    }
  }

  public AtomicLong getRecordsWritten() {
    return recordsWritten;
  }
}
