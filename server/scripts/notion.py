#!/usr/bin/env python3

import requests
import json
import sys
import cmdline as cl
from typing import *

GREEN = '\033[92m'
RED = '\033[91m'
BLUE = '\033[94m'
YELLOW = '\033[93m'
AQUA = '\033[96m'
BOLD = '\033[1m'
ENDC = '\033[0m'

VERBOSE: bool = False
    
def fromat_id(id: str) -> str:
    first = id[:8]
    second = id[8:12]
    third = id[12:16]
    fourth = id[16:20]
    fifth = id[20:]
    return f'{first}-{second}-{third}-{fourth}-{fifth}'

def make_notion_request(endpoint: str, version: str, secret: str, method: str='GET', data: Any=None) -> Union[dict, None]:
    """
    Make a request to the Notion API

    Parameters:
    - `endpoint`: The API endpoint to call
    - `version`: The Notion API version to use
    - `secret`: The Notion secret token
    - `method`: The HTTP method to use
    - `data`: The data to send with the request

    Returns:
    The JSON from the request, or `None` if the request failed
    """

    global VERBOSE

    print(f'{BOLD}Calling Notion API{ENDC}')
    print(f'{BOLD}{AQUA}{method}{ENDC} {YELLOW}{endpoint}{ENDC}', end='')

    headers = {
        'Authorization': f'Bearer {secret}',
        'Notion-Version': version,
        'Content-Type': 'application/json'
    }

    url = f'https://api.notion.com/v1/{endpoint}'

    sys.stdout.flush()

    if method == 'GET':
        response = requests.get(url, headers=headers)
    elif method == 'POST':
        response = requests.post(url, headers=headers, data=data)
    elif method == 'PATCH':
        response = requests.patch(url, headers=headers, data=data)
    elif method == 'DELETE':
        response = requests.delete(url, headers=headers)
    else:
        print(' -> {RED}Failed{ENDC}')
        print(f'{RED}Invalid method {method}{ENDC}')
        return None
    
    print(' -> ', end='')

    if response.status_code == 200: 
        print(f'{BOLD}{GREEN}{response.status_code} {response.reason}{ENDC}')
    else:
        print(f'{BOLD}{RED}{response.status_code} {response.reason}{ENDC}')
        
    return response.json()

def pages(version, secret, page_id):
    return make_notion_request(f'pages/{fromat_id(page_id)}', version, secret)

def usage():
    print('Usage: ./notion.py [options...] endpoint [args...]')
    print('Where endpoint is one of:')
    print('  pages')
    print('Options:')
    print('  -h  --help             Show this help message and exit')
    print('  -s --secret <token>    Notion secret token')
    print('  -f --file <file>       Notion secret file')
    print('  -n --notion <version>  Notion API version')
    print('  -v --verbose           Verbose output')

    return 0

def main() -> int:
    switches = [
        cl.Switch('help', value=usage),
        cl.Switch('secret', type=str),
        cl.Switch('file', type=str),
        cl.Switch('notion', type=str),
        cl.Switch('verbose', type=bool, value=True)
    ]

    res = cl.parse(sys.argv, switches=switches)
    if isinstance(res, int):
        return res
    args, options = res

    if len(args) == 0:
        return usage()
    
    global VERBOSE
    VERBOSE = options.get('verbose', False)
    
    version = options.get('notion', '2022-06-28')
    secret = options.get('secret', None)

    if secret is None:
        secret_file = options.get('file', 'private/notion_secret.txt')

        with open(secret_file, 'r') as f:
            secret = f.read().strip()
        print(f'Read secret from file {GREEN}{secret_file}{ENDC}')
    
    json_result = None
    command = args[0].lower()
    if command == 'pages':
        if len(args) < 2:
            print('Usage: ./notion.py pages <page_id>')
            return 1
        page_id = args[1]
        json_result = pages(version, secret, page_id)
    else:
        print(f'Unknown command {command}, use --help for usage')
        return 1
    
    if json_result is None:
        return 1
    
    print(f'{BOLD}Response body{ENDC}')
    print(json.dumps(json_result, indent=2))

    return 0

if __name__ == '__main__':
    sys.exit(main())
