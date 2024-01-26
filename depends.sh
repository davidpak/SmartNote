#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
NC='\033[0m' # No Color

MISSING_TOOLS=0
HAS_JAVA=false
HAS_JAVAC=false
HAS_ANT=false
HAS_IVY=false
HAS_NPM=false
HAS_NODE=false
HAS_PYTHON=false
HAS_PIP=false

MISSING_DEPS=0
HAS_REQUESTS=false
HAS_LANGCHAIN=false
HAS_DOTENV=false
HAS_NODE_MODULES=false

DO_INSTALL=false
DO_SILENT=false

INSTALL_REQUIRED=false

IS_WINDOWS=false
IS_DARWIN=false
IS_LINUX=false

DO_INSTALL=true

SYS_OS_NAME="$(uname -s)"
SYS_ARCH="$(uname -m)"

#####################################################################
# OS detection

if [[ "$SYS_OS_NAME" == "Windows_NT" ]]; then
    IS_WINDOWS=true
elif [[ "$SYS_OS_NAME" == "Darwin" ]]; then
    IS_DARWIN=true
elif [[ "$SYS_OS_NAME" == "Linux" ]]; then
    IS_LINUX=true
fi

extract_major_version() {
    echo "$1" | awk -F '.' '{print $1}'
}

extract_minor_version() {
    echo "$1" | awk -F '.' '{print $2}'
}

clear_line() {
    printf "\r%*s\r" "$(tput cols)" ""
}

print_line() {
    printf "%*s\n" "$(tput cols)" | tr ' ' -
}

succeed() {
    clear_line
    printf "Has ${BOLD}$1${NC} - ${GREEN}OK${NC}\n"
}

fail() {
    clear_line
    printf "Has ${BOLD}$1${NC} - ${RED}FAIL${NC}\n"
}

# hash_file <file>
hash_file() {
    if [[ "$IS_DARWIN"=true ]]; then
        echo $($md5 -q "$1")
    else
        echo $($md5sum "$1" | awk '{print $1}')
    fi
}

get_os_version() {
    if [[ "$IS_WINDOWS"=true ]]; then
        echo $(wmic os get Caption,Version | sed -n '2p')
    elif [[ "$IS_LINUX"=true ]]; then
        echo $(lsb_release -d | awk -F ' ' '{print $2}')
    elif [[ "$IS_DARWIN"=true ]]; then
        echo $(sw_vers -productVersion)
    fi
}

#####################################################################
# Command line arguments

for arg in "$@"; do
    if [[ "$arg" == "--no-install" ]]; then
        DO_INSTALL=false
    elif [[ "$arg" == "--clear-cache" ]]; then
        printf "${YELLOW}Clearing dependency cache${NC}\n"
        rm -f "client/package.json.md5"
        exit 0
    elif [[ "$arg" == "--silent" ]]; then
        DO_SILENT=true
    elif [[ "$arg" == "--help" ]]; then
        printf "Usage: ./depends.sh [options...]\n"
        printf "Check tools and dependencies for the project and install missing ones\n"
        printf "\n"
        printf "  --help           Display this help and exit\n"
        printf "  --no-install     Do not install missing dependencies\n"
        printf "  --clear-cache    Clear the cache of installed dependencies\n"
        printf "  --silent         Do not print anything to the console\n"
        exit 0
    else
        printf "${RED}Unknown argument: $arg${NC}\n"
        exit 1
    fi
done

if [[ $DO_SILENT = true ]]; then
    exec 1>/dev/null
    exec 2>/dev/null
fi

#####################################################################

printf "${BOLD}System${NC}\n"
print_line

printf "Operating System  ${BOLD}$SYS_OS_NAME${NC}\n"
printf "Architecture      ${BOLD}$SYS_ARCH${NC}\n"
printf "OS Version        ${BOLD}$(get_os_version)${NC}\n"

#####################################################################
# Check tools

printf "\n${BOLD}Check tools${NC}\n"
print_line

#####################################################################
# Check Java installation

printf "${YELLOW}Check ${BOLD}java${NC}"

if ! command -v java &> /dev/null; then
    fail "java"
else
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

    if [[ -z "$java_version" ]]; then
        fail "java"
    else
        java_version_major=$(extract_major_version "$java_version")

        if [[ -z "$java_version_major" ]]; then
            fail "java"
        else
            if [[ "$java_version_major" -ge 8 ]]; then
                succeed "java"
                HAS_JAVA=true
            else
                fail "java"
            fi
        fi
    fi
fi

if [[ $HAS_JAVA = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

#####################################################################
# Check Java Compiler installation

if [[ $HAS_JAVA = true ]]; then
    printf "${YELLOW}Check ${BOLD}javac${NC}"

    if ! command -v javac &> /dev/null; then
        fail "javac"
    else
        javac_version=$(javac -version 2>&1 | awk -F ' ' '/javac/ {print $2}')

        if [[ -z "$javac_version" ]]; then
            fail "javac"
        else
            javac_version_major=$(extract_major_version "$javac_version")

            if [[ -z "$javac_version_major" ]]; then
                fail "javac"
            else
                clear_line
                if [[ "$javac_version_major" -ge 8 ]]; then
                    succeed "javac"
                    HAS_JAVAC=true
                else
                    fail "javac"
                fi
            fi
        fi
    fi
else
    printf "${YELLOW}Skipping javac installation check${NC}\n"
fi

if [[ $HAS_JAVAC = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

#####################################################################
# Check ant installation

printf "${YELLOW}Check ${BOLD}ant${NC}"

if ! command -v ant &> /dev/null; then
    fail "ant"
else
    ant_version=$(ant -version | awk -F ' ' '/version/ {print $4}')
    if [[ -z "$ant_version" ]]; then
        fail "ant"
    else
        ant_version_major=$(extract_major_version "$ant_version")
        ant_version_minor=$(extract_minor_version "$ant_version")

        if [[ -z "$ant_version_major" || -z "$ant_version_minor" ]]; then
            fail "ant"
        else
            if [[ "$ant_version_major" -eq 1 && "$ant_version_minor" -ge 10 ]]; then
                succeed "ant"
                HAS_ANT=true
            else
                fail "ant"
            fi
        fi
    fi
fi

if [[ $HAS_ANT = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

#####################################################################
# Check ivy installation

if [[ $HAS_ANT = true ]]; then
    printf "${YELLOW}Checking ${BOLD}ivy${NC}"

    ivy_jar_matches=$(ant -diagnostics | grep ivy-*.*.*.jar | awk -F ' ' '{print $1}')
    
    found_installation=false
    while IFS= read -r line; do
        if [[ $line == *ivy-*.*.*.jar ]]; then
            found_installation=true

            ivy_jar=$line
            ivy_version=$(echo "$ivy_jar" | sed -n 's/ivy-\([0-9]*\.[0-9]*\.[0-9]*\).jar/\1/p')

            ivy_version_major=$(extract_major_version "$ivy_version")
            ivy_version_minor=$(extract_minor_version "$ivy_version")

            if [[ -z "$ivy_version_major" || -z "$ivy_version_minor" ]]; then
                fail "ivy"
            else
                if [[ "$ivy_version_major" -ge 2 && "$ivy_version_minor" -ge 5 ]]; then
                    succeed "ivy"
                    HAS_IVY=true
                    break
                else
                    fail "ivy"
                fi
            fi
        fi
    done < <(echo "$ivy_jar_matches")

    if [[ $found_installation = false ]]; then
        fail "ivy"
    fi
else
    printf "${YELLOW}Skipping Ivy installation check${NC}\n"
fi

if [[ $HAS_IVY = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

#####################################################################
# Check npm installation

printf "${YELLOW}Checking ${BOLD}npm${NC}"

if ! command -v npm &> /dev/null; then
    fail "npm"
else
    npm_version=$(npm -v)
    if [[ -z "$npm_version" ]]; then
        fail "npm"
    else
        npm_version_major=$(extract_major_version "$npm_version")

        if [[ -z "$npm_version_major" ]]; then
            fail "npm"
        else
            clear_line
            if [[ "$npm_version_major" -ge 9 ]]; then
                succeed "npm"
                HAS_NPM=true
            else
                fail "npm"
            fi
        fi
    fi
fi

if [[ $HAS_NPM = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

#####################################################################
# Check node installation

printf "${YELLOW}Checking ${BOLD}node${NC}"

if ! command -v node &> /dev/null; then
    fail "node"
else
    node_version=$(node -v)
    node_version=${node_version:1}
    if [[ -z "$node_version" ]]; then
        fail "node"
    else
        node_version_major=$(extract_major_version "$node_version")

        if [[ -z "$node_version_major" ]]; then
            fail "node"
        else
            if [[ "$node_version_major" -ge 18 ]]; then
                succeed "node"
                HAS_NODE=true
            else
                fail "node"
            fi
        fi
    fi
fi

if [[ $HAS_NODE = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

#####################################################################
# Check python installation

printf "${YELLOW}Checking ${BOLD}python${NC}"

if ! command -v python &> /dev/null; then
    fail "python"
else
    python_version=$(python -V 2>&1 | awk -F ' ' '/Python/ {print $2}')
    if [[ -z "$python_version" ]]; then
        fail "python"
    else
        python_version_major=$(extract_major_version "$python_version")
        python_version_minor=$(extract_minor_version "$python_version")

        if [[ -z "$python_version_major" || -z "$python_version_minor" ]]; then
            fail "python"
        else
            if [[ "$python_version_major" -ge 3 && "$python_version_minor" -ge 3 ]]; then
                succeed "python"
                HAS_PYTHON=true
            else
                fail "python"
            fi
        fi
    fi
fi

if [[ $HAS_PYTHON = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

#####################################################################
# Check pip installation

printf "${YELLOW}Checking ${BOLD}pip${NC}"

if ! command -v pip &> /dev/null; then
    fail "pip"
else
    pip_version=$(pip -V 2>&1 | awk -F ' ' '/pip/ {print $2}')
    if [[ -z "$pip_version" ]]; then
        fail "pip"
    else
        pip_version_major=$(extract_major_version "$pip_version")

        if [[ -z "$pip_version_major" ]]; then
            fail "pip"
        else
            if [[ "$pip_version_major" -ge 20 ]]; then
                succeed "pip"
                HAS_PIP=true
            else
                fail "pip"
            fi
        fi
    fi
fi

if [[ $HAS_PIP = false ]]; then
    MISSING_TOOLS=$((MISSING_TOOLS+1))
fi

if [[ $MISSING_TOOLS -gt 0 ]]; then
    printf "\n${RED}One or more tools are missing or incompatible${NC}\n"
fi

#####################################################################
# Check dependencies

printf "\n${BOLD}Check dependencies${NC}\n"
print_line

# requests
printf "${YELLOW}Checking ${BOLD}requests${NC}"

if ! python -c "import requests" &> /dev/null; then
    fail "requests"
    MISSING_DEPS=$((MISSING_DEPS+1))
else
    succeed "requests"
    HAS_REQUESTS=true
fi

# langchain
printf "${YELLOW}Checking ${BOLD}langchain${NC}"

if ! python -c "import langchain" &> /dev/null; then
    fail "langchain"
    MISSING_DEPS=$((MISSING_DEPS+1))
else
    succeed "langchain"
    HAS_LANGCHAIN=true
fi

# dotenv
printf "${YELLOW}Checking ${BOLD}dotenv${NC}"

if ! python -c "import dotenv" &> /dev/null; then
    fail "dotenv"
    MISSING_DEPS=$((MISSING_DEPS+1))
else
    succeed "dotenv"
    HAS_DOTENV=true
fi

# node_modules
printf "${YELLOW}Checking ${BOLD}node_modules${NC}"

current_package_json_hash=$(hash_file "client/package.json")
if [[ ! -d "client/node_modules" ]]; then
    # npm install has not been run
    fail "node_modules"
    MISSING_DEPS=$((MISSING_DEPS+1))
else
    prev_hash=""
    if [[ -f "client/package.json.md5" ]]; then
        prev_hash=$(cat "client/package.json.md5")
    fi
    
    if [[ "$current_package_json_hash" != "$prev_hash" ]]; then
        # package.json has changed since last install
        fail "node_modules"
        MISSING_DEPS=$((MISSING_DEPS+1))
    else
        succeed "node_modules"
        HAS_NODE_MODULES=true
    fi
fi

if [[ $MISSING_DEPS -gt 0 ]]; then
    printf "\n${RED}One or more dependencies are missing or incompatible${NC}\n"
    INSTALL_REQUIRED=true
fi

if [[ $INSTALL_REQUIRED = true ]]; then

#####################################################################
# Install missing dependencies

printf "\n${BOLD}Install dependencies${NC}\n"
print_line

if [[ $DO_INSTALL = true ]]; then 
    printf "Starting installs, this may take a while...\n\n"

    if [[ $HAS_PIP = false ]]; then
        printf "${RED}${BOLD}pip${NC} ${YELLOW}is missing - Python dependencies will not be installed${NC}\n"
    fi

    if [[ $HAS_REQUESTS = false && $HAS_PIP = true ]]; then
        printf "${YELLOW}Install ${BOLD}requests${NC}${YELLOW}...${NC}\n"
        pip install requests
        success=$?
        if [[ $success -eq 0 ]]; then
            printf "${GREEN}Installed ${BOLD}requests${NC}\n"
            HAS_REQUESTS=true
            MISSING_DEPS=$((MISSING_DEPS-1))
        else
            printf "${RED}Failed to install ${BOLD}requests${NC}\n"
        fi
        printf "\n"
    fi

    if [[ $HAS_LANGCHAIN = false && $HAS_PIP = true ]]; then
        printf "${YELLOW}Install ${BOLD}langchain${NC}${YELLOW}...${NC}\n"
        pip install langchain
        success=$?
        if [[ $success -eq 0 ]]; then
            printf "${GREEN}Installed ${BOLD}langchain${NC}\n"
            HAS_LANGCHAIN=true
            MISSING_DEPS=$((MISSING_DEPS-1))
        else
            printf "${RED}Failed to install ${BOLD}langchain${NC}\n"
        fi
        printf "\n"
    fi

    if [[ $HAS_DOTENV = false && $HAS_PIP = true ]]; then
        printf "${YELLOW}Install ${BOLD}dotenv${NC}${YELLOW}...${NC}\n"
        pip install python-dotenv
        success=$?
        if [[ $success -eq 0 ]]; then
            printf "${GREEN}Installed ${BOLD}dotenv${NC}\n"
            HAS_DOTENV=true
            MISSING_DEPS=$((MISSING_DEPS-1))
        else
            printf "${RED}Failed to install ${BOLD}dotenv${NC}\n"
        fi
        printf "\n"
    fi

    if [[ $HAS_NODE_MODULES = false && $HAS_NPM = true ]]; then
        printf "${YELLOW}Install ${BOLD}node_modules${NC}${YELLOW}...${NC}\n"

        cd client
        npm install
        success=$?
        cd ..

        if [[ $success -eq 0 ]]; then
            printf "${GREEN}Installed ${BOLD}node_modules${NC}\n"
            echo "$current_package_json_hash" > "client/package.json.md5"
            HAS_NODE_MODULES=true
            MISSING_DEPS=$((MISSING_DEPS-1))
        else
            printf "${RED}Failed to install ${BOLD}node_modules${NC}\n"
        fi
        printf "\n"
    fi
else
    printf "\n${YELLOW}Run with ${BOLD}--no-install${NC} ${YELLOW}to install missing dependencies${NC}\n"
fi

fi

#####################################################################
# Summary

printf "\n${BOLD}Summary${NC}\n"

term_width=$(tput cols)
printf "%*s\n" "${term_width}" | tr ' ' -

printf "\n${BOLD}Tools${NC}\n"

# Java
printf "  - ${BOLD}java $java_version${NC}\n"
if [[ $HAS_JAVA = false ]]; then
    if [[ -z "$java_version" ]]; then
        printf "      ${RED}No installation${NC}\n"
    fi
    printf "      ${RED}Requires JDK 8 or higher${NC}\n"
fi

# Javac
printf "  - ${BOLD}javac $javac_version${NC}\n"
if [[ $HAS_JAVAC = false ]]; then
    if [[ -z "$javac_version" ]]; then
        printf "      ${RED}No installation${NC}\n"
    fi
    printf "      ${RED}Requires JDK 8 or higher${NC}\n"
fi

# Ant
printf "  - ${BOLD}ant $ant_version${NC}\n"
if [[ $HAS_ANT = false ]]; then
    if [[ -z "$ant_version" ]]; then
        printf "      ${RED}No installation${NC}\n"
    fi
    printf "      ${RED}Requires Ant 1.10 or higher${NC}\n"
fi

# Ivy
printf "  - ${BOLD}ivy $ivy_version${NC}\n"
if [[ $HAS_IVY = false ]]; then
    if [[ -z "$ivy_version" ]]; then
        printf "      ${RED}No installation${NC}\n"
    fi
    printf "      ${RED}Requires Ivy 2.5 or higher${NC}\n"
fi

# NPM
printf "  - ${BOLD}npm $npm_version${NC}\n"
if [[ $HAS_NPM = false ]]; then
    if [[ -z "$npm_version" ]]; then
        printf "      ${RED}No installation${NC}\n"
    fi
    printf "      ${RED}Requires NPM 9 or higher${NC}\n"
fi

# Node
printf "  - ${BOLD}node $node_version${NC}\n"
if [[ $HAS_NODE = false ]]; then
    if [[ -z "$node_version" ]]; then
        printf "      ${RED}No installation${NC}\n"
    fi
    printf "      ${RED}Requires Node 18 or higher${NC}\n"
fi

# Python
printf "  - ${BOLD}python $python_version${NC}\n"
if [[ $HAS_PYTHON = false ]]; then
    if [[ -z "$python_version" ]]; then
        printf "      ${RED}No installation"
    fi
    printf "      ${RED}Requires Python 3.5 or higher${NC}\n"
fi

# Pip
printf "  - ${BOLD}pip $pip_version${NC}\n"
if [[ $HAS_PIP = false ]]; then
    if [[ -z "$pip_version" ]]; then
        printf "      ${RED}No installation${NC}\n"
    fi
    printf "      ${RED}Requires Pip 20 or higher${NC}\n"
fi

printf "\n${BOLD}Dependencies${NC}\n"

# Requests
printf "  - ${BOLD}requests${NC} (Python)\n"
if [[ $HAS_REQUESTS = false ]]; then
    printf "      ${RED}Missing${NC}\n"
fi

# Langchain
printf "  - ${BOLD}langchain${NC} (Python)\n"
if [[ $HAS_LANGCHAIN = false ]]; then
    printf "      ${RED}Missing${NC}\n"
fi

# Dotenv
printf "  - ${BOLD}dotenv${NC} (Python)\n"
if [[ $HAS_DOTENV = false ]]; then
    printf "      ${RED}Missing${NC}\n"
fi

# Node Modules
printf "  - ${BOLD}node_modules${NC} (Node)\n"
if [[ $HAS_NODE_MODULES = false ]]; then
    printf "      ${RED}Missing${NC}\n"
fi

# If there are still issues, print a message
if [[ $MISSING_TOOLS -gt 0 || $MISSING_DEPS -gt 0 ]]; then
    printf "\n${RED}There are still issues${NC}\n"
    printf "${YELLOW}Please resolve them and try again${NC}\n"
    printf "See ${BOLD}README.md${NC} for more information\n"
    exit 1
fi

printf "\n${GREEN}All dependencies are installed and compatible${NC}\n"
exit 0
