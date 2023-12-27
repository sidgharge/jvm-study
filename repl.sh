#!/bin/sh

mvn clean install -DskipTests

java -cp parser/target/parser-1.0-SNAPSHOT.jar come.homeproects.jvmstudy.parser.Repl

