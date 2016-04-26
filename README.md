# LeagueManager
Small project to test different graph databases and different strategies of graph database bulk loading.

## Prerequisites

This project requires the following:

1. Java 8
2. Gradle
3. Local install of Neo4J

## Overview

Ingesting large amounts of data in any database is difficult and there are few good
resources for testing loading techniques in NOSQL databases. To help investigate
different loading strategies in different Graph database technologies, we have decided
to model the relationships of the ever popular SportsBall teams.  The data model for
SportsBall teams and their Leagues will be:

 * People (Types: Player, Coach, Fan) are associated with one or more Teams
 * Teams play in one or more Leagues
 * Regions contain one or more Leagues

 ![Leagure Manager Model](./docs/LeagueManagerModel.png)

SportsBall is the worlds most popular, full contact, high speed sporting extravaganza that delivers competitive advantage through continuous excitement delivery at a speed that matters.

## How to use

There are a handful of properties that drive the loading of data in LeagueManager.  All of these can be set/updated in the application.yml file.
For a full set of properties currently set look in ![application.yml](./src/main/resources/application.yml). There are a few properties that have a more impact
on the behavior of the app.  These include:

* regions.loading.threads - how many threads will load regions into database.
* leagues.loading.threads - how many threads will load leagues into database.
* teams.loading.threads - how many threads will load teams into database.
* neo4j.url - url to the neo4j install
* neo4j.username - neo4j user
* neo4j.password - password for neo4j.user

To run the test project either run the Spring Boot application from the command line or from your IDE of choice.

|Functionality |JVM parameter |Description|
|---|---|---|
| Remove all LeagueManager nodes | cleanup | Delete all nodes and relationships from the DB |
| Load All data | loadAll  |Run all loaders in the application.   |
| Load League data  | loadLeagues  | Only load league data to the application.  |
| Load Region data  | loadRegions  | Only load region data to the application. |
| Load Team data | loadTeams | Only load team data to the application. |
| Run Node First insertion | nodeFirst | Run using the node first approach for loaders. |

Sample usage: *java -jar leagueManager-0.0.1-RELEASE.jar loadLeagues loadRegions*

Sample usage of Node First approach: *java -jar leagueManager-0.0.1-RELEASE.jar nodeFirst loadLeagues loadRegions*

Sample Cleanup usage: *java -jar leagueManager-0.0.1-RELEASE.jar cleanup*

This will load the leagues and regions into the application.

[Outstanding Concerns](./docs/outstanding_concerns.md)

[Project work that is still needed](./docs/TODOs.md)
