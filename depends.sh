#!/bin/bash

start_time="$(date -u +%s)"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
NC='\033[0m' # No Color

printf "${GREEN}Targeting platform $OSTYPE${NC}\n"

# Check Java version
printf "\n${YELLOW}Checking Java version${NC}\n"

java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
printf "Has ${BOLD}$java_version${NC}"

# Check if Java version is 8 or higher
if [[ "$java_version" < "1.8" ]]; then
    printf "\n${RED}Fatal: Java version is too low, please install Java 8 or higher${NC}\n"
    exit 1
else
    printf " - ${GREEN}OK${NC}\n"
fi

# Check ant version
printf "\n${YELLOW}Checking Ant version${NC}\n"

ant_version=$(ant -version | awk -F ' ' '/version/ {print $4}')
printf "Has ${BOLD}$ant_version${NC}"

# Check if ant version is 1.10 or higher
if [[ "$ant_version" < "1.10" ]]; then
    printf "\n${RED}Fatal: Ant version is too low, please install Ant 1.10 or higher${NC}\n"
    exit 1
else
    printf " - ${GREEN}OK${NC}\n"
fi

# Check for ivy
printf "\n${YELLOW}Checking for Ivy${NC}\n"

# search for ivy-2.*.jar
result=$(ant -diagnostics | grep "ivy-2.*.jar")

if [[ -n "$result" ]]; then
    first_match=$(echo "$result" | head -n 1)
    ivy_jar=$(echo "$first_match" | awk -F '[-.]' '{print $2"."$3"."$4}')
    printf "Has ${BOLD}$ivy_jar${NC} - ${GREEN}OK${NC}\n"
else
    printf "${RED}Fatal: Ivy not found, please install Ivy 2 or higher${NC}\n"
    exit 1
fi
