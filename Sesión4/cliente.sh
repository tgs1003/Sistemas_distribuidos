#!/bin/bash
export  CLASSPATH=$CLASSPATH:./target/classes
java -Djava.security.policy=resterit.policy es.ubu.lsi.Cliente $1
