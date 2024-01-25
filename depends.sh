#!/bin/bash

start_time="$(date -u +%s)"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
NC='\033[0m' # No Color

printf "${GREEN}Targeting platform $OSTYPE${NC}\n"

HAS_JAVA=false
HAS_ANT=false
HAS_IVY=false

extract_major_version() {
    echo "$1" | awk -F '.' '{print $1}'
}

extract_minor_version() {
    echo "$1" | awk -F '.' '{print $2}'
}

clear_line() {
    printf "\r%*s\r" "$(tput cols)" ""
}

# Check Java installation
printf "${YELLOW}Checking Java installation${NC}"

if ! command -v java &> /dev/null; then
    printf "${RED}Error: Java not found, need Java 8 or higher${NC}\n"
else
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

    if [[ -z "$java_version" ]]; then
        clear_line
        printf "${RED}Error: Java not found, need Java 8 or higher${NC}\n"
    else
        java_version_major=$(extract_major_version "$java_version")

        if [[ -z "$java_version_major" ]]; then
            clear_line
            printf "${RED}Error: Java version not found, need Java 8 or higher${NC}\n"
        else
            clear_line
            printf "Has ${BOLD}java $java_version${NC}"
            if [[ "$java_version_major" -ge 8 ]]; then
                printf " - ${GREEN}OK${NC}\n"
                HAS_JAVA=true
            else
                printf "\n${RED}Error: Java version is too low, need Java 8 or higher${NC}\n"
            fi
        fi
    fi
fi

# Check ant installation
printf "${YELLOW}Checking Ant installation${NC}"

if ! command -v ant &> /dev/null; then
    clear_line
    printf "${RED}Error: Ant not found, need Ant 1.10 or higher${NC}\n"
else
    ant_version=$(ant -version | awk -F ' ' '/version/ {print $4}')
    if [[ -z "$ant_version" ]]; then
        printf "${RED}Error: Ant not found, need Ant 1.10 or higher${NC}\n"
    else
        ant_version_major=$(extract_major_version "$ant_version")
        ant_version_minor=$(extract_minor_version "$ant_version")

        if [[ -z "$ant_version_major" || -z "$ant_version_minor" ]]; then
            printf "\r${RED}Error: Ant version not found, need Ant 1.10 or higher${NC}\n"
        else
            printf "\rHas ${BOLD}ant $ant_version${NC}"
            if [[ "$ant_version_major" -eq 1 && "$ant_version_minor" -ge 10 ]]; then
                printf " - ${GREEN}OK${NC}\n"
                HAS_ANT=true
            else
                printf "\n${RED}Error: Ant version is too low, need Ant 1.10 or higher${NC}\n"
            fi
        fi
    fi
fi

# Check for ivy installation
if [[ $HAS_ANT = true ]]; then
    printf "${YELLOW}Checking Ivy installation${NC}"

    ivy_jar_matches=$(ant -diagnostics | grep ivy-2.*.*.jar | awk -F ' ' '{print $1}')
    
    while IFS= read -r line; do
        if [[ $line == *ivy-2.*.*.jar ]]; then
            ivy_jar=$line
            ivy_version=$(echo "$ivy_jar" | sed -n 's/ivy-\([0-9]*\.[0-9]*\.[0-9]*\).jar/\1/p')

            ivy_version_major=$(extract_major_version "$ivy_version")
            ivy_version_minor=$(extract_minor_version "$ivy_version")

            if [[ -z "$ivy_version_major" || -z "$ivy_version_minor" ]]; then
                printf "\r${YELLOW}An Ivy installation was found, but the version could not be determined${NC}\n"
            else
                printf "\rHas ${BOLD}ivy $ivy_version${NC}"
                if [[ "$ivy_version_major" -ge 2 && "$ivy_version_minor" -ge 5 ]]; then
                    printf " - ${GREEN}OK${NC}\n"
                    HAS_IVY=true
                else
                    printf "\n${RED}Error: Ivy version is too low, need Ivy 2.5 or higher${NC}\n"
                fi
            fi
            break
        fi
    done < <(echo "$ivy_jar_matches")

    if [[ $HAS_IVY = false ]]; then
        printf "\r${RED}Error: Ivy not found, need Ivy 2.5 or higher${NC}\n"
    fi
else
    printf "${YELLOW}Skipping Ivy installation check${NC}\n"
fi

# check for npm installation
printf "${YELLOW}Checking NPM installation${NC}"

if ! command -v npm &> /dev/null; then
    printf "\r${RED}Error: NPM not found, need NPM 10 or higher${NC}\n"
    exit 1
fi

npm_version=$(npm -v)
if [[ -z "$npm_version" ]]; then
    printf "\r${RED}Error: NPM not found, need NPM 10 or higher${NC}\n"
else
    npm_version_major=$(extract_major_version "$npm_version")

    if [[ -z "$npm_version_major" ]]; then
        printf "\r${RED}Error: NPM version not found, need NPM 10 or higher${NC}\n"
    else
        printf "\rHas ${BOLD}npm $npm_version${NC}"
        if [[ "$npm_version_major" -ge 10 ]]; then
            printf " - ${GREEN}OK${NC}\n"
        else
            printf "\n${RED}Error: NPM version is too low, need NPM 10 or higher${NC}\n"
        fi
    fi
fi

# check node installation
printf "${YELLOW}Checking Node installation${NC}"

if ! command -v node &> /dev/null; then
    printf "\r${RED}Error: Node not found, need Node 20 or higher${NC}\n"
    exit 1
fi

node_version=$(node -v)
node_version=${node_version:1}
if [[ -z "$node_version" ]]; then
    printf "\r${RED}Error: Node not found, need Node 20 or higher${NC}\n"
else
    node_version_major=$(extract_major_version "$node_version")

    if [[ -z "$node_version_major" ]]; then
        printf "\r${RED}Error: Node version not found, need Node 20 or higher${NC}\n"
    else
        printf "\rHas ${BOLD}node $node_version${NC}"
        if [[ "$node_version_major" -ge 20 ]]; then
            printf " - ${GREEN}OK${NC}\n"
        else
            printf "\n${RED}Error: Node version is too low, need Node 20 or higher${NC}\n"
        fi
    fi
fi
