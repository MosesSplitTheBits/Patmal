# Workshop Registration – EX2 Maven Evolution

## One-time setup: install the course jars into the local Maven repository
Run from the folder that contains the 3 jars:

```
mvn install:install-file -Dfile=model-2.0.jar -DgroupId=patmal.workshops -DartifactId=model -Dversion=2.0 -Dpackaging=jar
mvn install:install-file -Dfile=logic-2.0.jar -DgroupId=patmal.workshops -DartifactId=logic -Dversion=2.0 -Dpackaging=jar
mvn install:install-file -Dfile=workshop-tester-2.0.jar -DgroupId=patmal.workshops -DartifactId=workshop-tester -Dversion=2.0 -Dpackaging=jar
```

## Build
```
mvn clean install
```
Produces `app/target/workshop-cli.jar`.

## Run
```
java -jar app/target/workshop-cli.jar
```
Writes instructors.json, students.json and teams.json to the current folder.

## Test
```
java -cp "<path-to>/workshop-tester-2.0.jar:app/target/workshop-cli.jar" patmal.workshops.main.Main
```

## Modules
- `core/model-impl` – model implementation
- `core/logic-impl` – WorkshopSystem implementation (logging, e-mail validation, JSON export)
- `app` – runnable CLI, builds the uber jar