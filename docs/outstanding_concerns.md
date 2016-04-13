# Outstanding Concerns


## Deadlock concern while running loader with multiple threads.

To replicate this issue:
1. start with a clean database
2. run LeagueManager with parameter loadAll
3. See Neo4j deadlock error in log

We plan on running multiple multi-threaded  loader instances where we are not able to guarantee the
order in which data is loaded.  The current implementation has a single app instance that when
loading multiple leagues in multiple threads a deadlock is encountered because multiple Leagues share regions.

### Questions to answer:

* Is there a way to get around this issue without slowing down the data loading processing?
* Is there a limit to how much data that can be passed to a single Unwind statement? (i.e  number
of json objects passed)
* If there is a problem encountered during an unwind is everything rolled back or can data be in a
partially commited state.

## General Questions:
* Detach Delete:  It appears that a similar deadlock is encountered when trying the query below.  Is
there an appropriate way to do multiple detached deletes at a time?
>match (r:Region) match (l:League) match (t:Team) detach delete r, l, t

