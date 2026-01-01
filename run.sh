#!/bin/bash
set -e

export HYWATCH_BOT_IDENTITY="hywatch-backend-prod"
export HYWATCH_BOT_PASSWORD="FbYd5BL5NgmqiF!n"

# Create lib directory
mkdir -p lib

# Download Gson if not exists
if [ ! -f "lib/gson-2.10.1.jar" ]; then
    echo "Downloading Gson..."
    curl -L -o lib/gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
fi

# Compile
echo "Compiling..."
javac -cp "lib/gson-2.10.1.jar" -d bin src/main/java/com/hywatch/client/*.java

# Run
echo "Running..."
# Use -cp to include bin and lib, and src/main/resources for the logo (resources need to be on classpath)
# Actually, standard way is to copy resources to bin or just include source folder is hacky but might work for lookup.
# Better to copy resources to bin.
cp -r src/main/resources/* bin/

java -cp "bin:lib/gson-2.10.1.jar" com.hywatch.client.Main
