#!/bin/sh

# for referencing the directory of this script (not the directory from where it may have been called!)
SCRIPT_DIR=$(dirname "$0")

# docker container config
. ./build_docker_image.conf

# clean stuff created from running server locally
rm -f -R data/databases/*
rm -f -R data/static/app
rm -f data/log/*
rm -f data/*

# From now on, stop the script if any command return non-zero (i.e. fails)
set -e

echo "------------------------"
echo "Building JAR with KVision"
echo "------------------------"
./gradlew clean jar

# create image
#docker buildx build --platform linux/arm32v7 -t $IMAGE_NAME:$TAG server
docker build -t $IMAGE_NAME:$TAG .