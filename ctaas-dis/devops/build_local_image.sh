#!/bin/bash
export WORKSPACE=$(pwd)
export BRANCH_NAME=main
export RELEASE_VERSION="${BRANCH_NAME}-latest"

bash ./devops/build_docker_image.sh local
