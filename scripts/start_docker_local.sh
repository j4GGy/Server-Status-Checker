#!/bin/sh

# for referencing the directory of this script (not the directory from where it may have been called!)
SCRIPT_DIR=$(dirname "$0")

# Runs the Web Server in a *local* Docker container
# For the non-Docker version, see start_server.sh
# For deploying the container remotely, see deploy_docker.sh

# config
# local config: deploy_docker.conf
# Note: vars HOST and HOST_DATA_PATH are ignored here
. ./deploy_docker.conf

. $SCRIPT_DIR/build_docker_image.sh

# Allow the "removing old instance" command to fail without stopping the script
set +e

echo "------------------------"
echo "Removing old instance of $CONTAINER_NAME"
echo "------------------------"
docker stop $CONTAINER_NAME && docker rm $CONTAINER_NAME

echo "------------------------"
echo "Starting $CONTAINER_NAME"
echo "------------------------"

LOCAL_STORAGE=/tmp/docker/$CONTAINER_NAME

# create container
docker run -d \
	-p $HOST_PORT:8080 \
	-v $LOCAL_STORAGE:/app/data \
	--name $CONTAINER_NAME \
	$IMAGE_NAME:$TAG