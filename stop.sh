#!/bin/bash

# Function to check if Docker is running
is_docker_running() {
  docker info &>/dev/null
}

# Ensure Docker is running before stopping containers
if is_docker_running; then
  echo "Removing Docker containers..."
  docker compose down || echo "Error: Failed to stop Docker containers."
else
  echo "Docker is not running. Skipping container removal."
fi

# Stop Docker Desktop only if it is running
#if is_docker_running; then
#  echo "Stopping Docker daemon..."
#  docker desktop stop || echo "Error: Failed to stop Docker daemon."
#else
#  echo "Docker is already stopped."
#fi