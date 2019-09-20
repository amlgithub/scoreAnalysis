#!/usr/bin/env bash

rm target -rf
nohup  mvn spring-boot:run -Drun.project=score_analysis >> logs/catalina.out  2>&1 &
