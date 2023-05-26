#!/bin/bash

javac -d bin/ -cp src ./src/Automata/*.java
javac -d bin/ -cp src ./src/dk/brics/automaton/*.java
javac -d bin/ -cp src ./src/Token/*.java
javac -d bin/ -classpath ".:src:src/lib/jline3.jar" ./src/Main/*.java

cd bin
java -cp "../src:../src/lib/jline3.jar:." Main.Prover
cd ..