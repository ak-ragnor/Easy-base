#!/bin/bash
# EasyBase Source Formatter Script
# Usage: ./formatSource.sh or chmod +x formatSource.sh && ./formatSource.sh

echo "Running Liferay Source Formatter..."
mvn exec:java -Dexec.mainClass=com.liferay.source.formatter.SourceFormatter -Dexec.args="-Dsource.properties.file=${PWD}/source-formatter.properties" -Dexec.includePluginDependencies=true
echo "Source formatting completed!"