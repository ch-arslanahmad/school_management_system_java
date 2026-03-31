#!/bin/bash


cd "$(dirname "$0")" || exit

help() {
    echo "Usage: $0 [compile|run|clean|all]"
    echo "Help: $0 --help/-h"
    exit 0
}

if [ -z "$ACTION" ]; then
    echo "Compiling and Running..."
    mvn compile exec:java -Dexec.mainClass="Main"
    exit 1
fi

# Check for help first
if [ "$ACTION" = "--help" ] || [ "$ACTION" = "-h" ]; then
    help;
elif [ ! -f "Main.java" ]; then
    echo "Main.java does not exist."
    exit 1
elif [ ! -f "pom.xml" ]; then
    echo "pom.xml does not exist."
    exit 1
fi

case "$ACTION" in
  compile)
    echo "Compiling..."
    mvn compile
    ;;
  run)
    echo "Running..."
    mvn exec:java -Dexec.mainClass="Main"
    ;;
  clean)
    echo "Cleaning..."
    mvn clean
    ;;
  all)
    echo "Compiling and Running..."
    mvn compile exec:java -Dexec.mainClass="Main"
    ;;
  *)
    echo "Invalid option: $ACTION"
    help
    ;;
esac
