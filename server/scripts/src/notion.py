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
    """
    Format a Notion ID to the dashed format. If the ID is already in
    the dashed format, it will be returned as is.

    Parameters:
    - `id`: The Notion ID to format

    Returns:
    The formatted Notion ID
    """

    if len(id) == 36:
        return id # already formatted

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

    print(f'{BOLD}{AQUA}{method}{ENDC} {YELLOW}{endpoint}{ENDC}', end='')

    headers = {
        'Authorization': f'Bearer {secret}',
        'Notion-Version': version
    }

    if data is not None:
        headers['Content-Type'] = 'application/json'

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

def pages_get(version, secret, page_id):
    return make_notion_request(f'pages/{fromat_id(page_id)}', version, secret)

def pages_new(version, secret, page_id, name):
    properties = {
        'parent': {
            'page_id': page_id
        },
        'properties': {
            'title': [
                {
                    'text': {
                        'content': name
                    }
                }
            ]
        }
    }

    return make_notion_request('pages', version, secret, method='POST', data=json.dumps(properties))

def blocks_get(version, secret, block_id):
    return make_notion_request(f'blocks/{fromat_id(block_id)}', version, secret)

def blocks_append(version, secret, block_id, string):
    children = {
        'children': [
            {
                'object': 'block',
                'type': 'paragraph',
                'paragraph': {
                    'rich_text': [
                        {
                            'type': 'text',
                            'text': {
                                'content': string
                            }
                        }
                    ]
                }
            }
        ]
    }

    return make_notion_request(f'blocks/{fromat_id(block_id)}/children', version, secret, method='PATCH', data=json.dumps(children))

def usage():
    print('Usage: ./notion.py [options...] <endpoint> [args...]')
    print('Endpoints:')
    print('  pages')
    print('  blocks')
    print('Options:')
    print('  -h  --help               Show this help message and exit')
    print('  -s --secret <token>      Notion secret token')
    print('  -f --file <file>         Notion secret file')
    print('  -n --notion <version>    Notion API version')
    print('  -v --verbose             Verbose output')

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
        def pages_usage():
            print('Usage: pages <command> <page_id> [args...]')
            print('Commands:')
            print('  get <page_id>            Get a page by its ID')
            print('  new <page_id> <name>     Create a new page under a parent by its ID')
            return 0

        if len(args) < 3:
            pages_usage()
            return 1
        command = args[1].lower()
        page_id = args[2]
        if command == 'get':
            json_result = pages_get(version, secret, page_id)
        elif command == 'new':
            if len(args) < 4:
                pages_usage()
                return 1
            json_result = pages_new(version, secret, page_id, args[3])
    elif command == 'blocks':
        def blocks_usage():
            print('Usage: blocks <command> <block_id> [args...]')
            print('Commands:')
            print('  get <block_id>            Get a block by its ID')
            print('  append <block_id> <string>')
            print('                            Append plain text to a block')
            return 0
        
        if len(args) < 3:
            blocks_usage()
            return 1
        
        command = args[1].lower()
        block_id = args[2]

        if command == 'get':
            json_result = blocks_get(version, secret, block_id)
        elif command == 'append':
            if len(args) < 4:
                blocks_usage()
                return 1
            json_result = blocks_append(version, secret, block_id, args[3])
        else:
            pages_usage()
            return 1
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
