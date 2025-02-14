#!/bin/sh

# for referencing the directory of this script (not the directory from where it may have been called!)
SCRIPT_DIR=$(dirname "$0")

# config
# local config: deploy_docker.conf
# USER=
# HOST=
# HOST_DATA_PATH=
# HOST_PORT=
. ./deploy_docker.conf

. $SCRIPT_DIR/build_docker_image.sh

# Allow the "removing old instance" command to fail without stopping the script
set +e

echo "------------------------"
echo "Removing old instance of $CONTAINER_NAME on $HOST"
echo "------------------------"
ssh $USER@$HOST "docker stop $CONTAINER_NAME && docker rm $CONTAINER_NAME"

# From now on, stop the script if any command return non-zero (i.e. fails)
set -e
echo "------------------------"
echo "Copying image to $HOST"
echo "------------------------"
# copy image to host
docker save $IMAGE_NAME:$TAG | gzip | pv | ssh $USER@$HOST docker load

echo "------------------------"
echo "Starting $CONTAINER_NAME on $HOST"
echo "------------------------"
# create container
ssh $USER@$HOST \
	docker run -d \
	-p $HOST_PORT:8080 \
	-v $HOST_DATA_PATH:/app/data \
	--restart on-failure:3 \
	--name $CONTAINER_NAME \
	$IMAGE_NAME:$TAG