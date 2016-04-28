package org.dentinger.tutorial.loader.nodefirst;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.dentinger.tutorial.dal.SportsBallRepository;
import org.dentinger.tutorial.domain.Person;
import org.dentinger.tutorial.util.AggregateExceptionLogger;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class NFPersonLoader {

  private static Logger logger = LoggerFactory.getLogger(NFPersonLoader.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;
  private SportsBallRepository repo;
  private int numThreads;
  private final AtomicLong recordsWritten = new AtomicLong(0);
  private ThreadPoolTaskExecutor poolTaskExecutor;

  private String MERGE_PERSON_NODES =
      "unwind {json} as person "
          + "merge (p:Person {id: person.id}) "
          + " on create set p.name = person.name ";

  private String CLEAN_UP =
      "match (p:Person) detach delete p";

  @Autowired
  public NFPersonLoader(ThreadPoolTaskExecutor personProcessorThreadPool,
                        Neo4jProperties neo4jProperties,
                        SessionFactory sessionFactory,
                        SportsBallRepository repo,
                        Environment env) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
    this.repo = repo;
    this.numThreads = Integer.valueOf(env.getProperty("persons.loading.threads", "1"));
    this.poolTaskExecutor = personProcessorThreadPool;
  }

  public void loadPersons() {
    AggregateExceptionLogger aeLogger = AggregateExceptionLogger.getLogger(this.getClass());

    List<Person> personList = repo.getPersons();

    logger.info("About to load {} Persons using {} threads", personList.size(), numThreads);
    recordsWritten.set(0);

    int subListSize = (int) Math.floor(personList.size() / numThreads);
    long start = System.currentTimeMillis();
    Lists.partition(personList, subListSize).stream().parallel()
        .forEach((sublist) -> {
          processPersonNodeInDB(aeLogger, sublist);
          // processTeamsInDB(aeLogger, sublist);

        });
    while (poolTaskExecutor.getPoolSize() > 0) {
      logger.info("PersonLoader: Currently running threads: {}, jobs still in pool {}",
          poolTaskExecutor.getActiveCount(),
          poolTaskExecutor.getPoolSize());
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    logger.info("Processing of {} Persons nodes complete: {}ms", recordsWritten.get(),
        System.currentTimeMillis() - start);

  }

  @Async("personProcessorThreadPool")
  private void processPersonNodeInDB(AggregateExceptionLogger aeLogger, List<Person> sublist) {
    Neo4jTemplate
        neo4jTemplate = getNeo4jTemplate();
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("json", sublist);
    try {
      neo4jTemplate.execute(MERGE_PERSON_NODES, map);
      recordsWritten.addAndGet(sublist.size());
    } catch (Exception e) {
      aeLogger
          .error("Unable to update graph, personCount={}",
              sublist.size(), e);
    }
  }

  public void cleanup() {
    logger.info("Initiate Person Purge");
    getNeo4jTemplate().execute(CLEAN_UP);
    logger.info("Completed Person Purge");

  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }
}
