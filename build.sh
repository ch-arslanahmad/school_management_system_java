#!/bin/bash

# 1. Navigate to script's directory
cd "$(dirname "$0")" || exit

# 2. Clean previous builds
echo "Cleaning old class files..."
find . -name "*.class" -delete

# 3. Compile all Java files
echo "Compiling all sources..."
javac -d bin -cp "lib/sqlite-jdbc-3.50.3.0.jar" $(find . -name "*.java") || {
    echo "Compilation failed."
    exit 1
}

# 4. Run specified class (default: database.testing)
CLASS_TO_RUN="${1:-database.testing}"
echo "Running $CLASS_TO_RUN..."
java -cp "bin:lib/sqlite-jdbc-3.50.3.0.jar" "$CLASS_TO_RUN"