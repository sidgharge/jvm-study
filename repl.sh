#!/bin/sh

set -e

mvn -projects parser -am package -DskipTests

clear

java -cp parser/target/parser-1.0-SNAPSHOT.jar come.homeproects.jvmstudy.parser.Repl

