#!/bin/bash

# Runs the Web Server locally, not in a container
# For the Docker version, see start_docker_local.sh
./gradlew jvmRun &
./gradlew jsRun -t
