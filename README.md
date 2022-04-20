# steam-mining

[![Build](https://github.com/Ancocodet/steam-mining/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/Ancocodet/steam-mining/actions/workflows/gradle.yml)
[![Publish](https://github.com/Ancocodet/steam-mining/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/Ancocodet/steam-mining/actions/workflows/gradle-publish.yml)

Data Mining of Steam Data Sources with two running options.
The fetch is done with multiple threads (config).

```shell
java -jar <jarname> --details # Fetching game details (prices)
java -jar <jarname> # Fetching online counters
java -jar <jarname> --full # Running both
```

## Requirements:
- min. Java 11
- Cron (recommended)

## Configurations

There are two configuration-files. One for the database(mysql) settings and one for the fetch settings.

**Fetch-Config**
```json
{
  "threads": 1000,
  "timeouts": 2500,
  "app_per_parse": 500
}
```
The thread amount and parse amount should be handled with care.

**Database-Config**
```json
{
  "hostName": "localhost",
  "port": 3306,
  "database": "database",
  "username": "username",
  "password": "password"
}
```

## Warnings

There is a Rate-Limit of **100.000** requests per day for details (and there are more than 100.000 games) because of that the detail fetch shouldn't be used more than once per day.

The amount of data is huge, because of that the online fetch shouldn't be used very often (recommended every 2 hours).