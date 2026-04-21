#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "Compiling..."
find src -name "*.java" > sources.txt
javac -cp lib/sqlite-jdbc.jar -d out @sources.txt

echo "Packaging..."
cd out && jar xf ../lib/sqlite-jdbc.jar && cd ..
jar cfm MedicalAppointmentSystem.jar manifest.txt -C out .

echo "Launching..."
java -jar MedicalAppointmentSystem.jar
