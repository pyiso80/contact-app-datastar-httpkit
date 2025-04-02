#!/bin/bash

is_docker_running() {
  docker info &>/dev/null
}

if ! is_docker_running; then
  echo "Starting Docker daemon..."
  docker desktop start &

  # Wait until Docker is ready or timeout after 10 seconds
  timeout=10
  while ! is_docker_running; do
    echo "Waiting for Docker to start..."
    sleep 2
    ((timeout--))
    if [ $timeout -le 0 ]; then
      echo "Error: Docker did not start in time."
      exit 1
    fi
  done
else
  echo "Docker is already running."
fi

# Removing Docker Compose
echo "Removing Docker containers..."
docker compose down -v &
sleep 2
# Start Docker Compose
echo "Starting Docker containers..."
nohup docker compose up > log/docker.log 2> log/nohup.out &


# Wait a few seconds to ensure containers are ready (optional)
sleep 2

# Start the Clojure application
# echo "Starting Clojure Kit REPL..."
# clj -M:dev
