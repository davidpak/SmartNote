#!/usr/bin/env python3

from typing import *

import requests
import sys
import urllib.parse
import util

def upload_file(
        base_url: str,
        filename: str,
        auth: Union[str, None]=None) -> Union[str, None]:
    """
    Upload a file to the server. See README.md for more information.

    Parameters:
    - `base_url`: Server base URL.
    - `filename`: File name.
    - `auth`: Authentication token. If `None`, then no authentication
              token is used.

    Returns:
    The new authentication token, if any.
    """
    
    print(f'Upload {filename}... ', end='')

    with open(filename, 'rb') as f:
        data = f.read()
        f.close()

    filename_safe = urllib.parse.quote(filename)
    url = f'{base_url}/api/v1/upload?name={filename_safe}'

    headers = {
        'Content-Type': 'text/plain'
    }

    if auth:
        headers['Authorization'] = auth

    r = requests.post(url, data=data, headers=headers)
    auth = r.headers.get('Authorization', None)

    if r.status_code == 200:
        print(f'Done ({len(data)} bytes transfered)')
    else:
        print(f'Failed: {r.status_code} {r.reason}')

    return auth

def usage():
    print('Usage: ./upload.py [options...] <host> [files...]')
    print('Options:')
    print('  -h  --help           Show this help message and exit')
    print('  -a, --auth <token>   Authentication token')

    return 0

def main() -> int:
    switches = [
        util.Switch('help', 'h', 'Show this help message and exit', value=usage),
        util.Switch('auth', 'a', 'Authentication token', type=str)
    ]

    res = util.parse_command_line(
        sys.argv,
        switches=switches
    )

    if isinstance(res, int):
        return res
    args, options = res

    if len(args) + len(options) == 0:
        return usage()

    host = args[0]
    files = args[1:]
    
    if host is None:
        print('Missing host')
        return 1
    assert isinstance(host, str)

    auth = options.get('auth', None)

    if len(files) == 0:
        print('No files')
        return 1
    
    # parse port
    port_index = host.find(':')
    if port_index != -1:
        port = int(host[port_index + 1:])
        host = host[:port_index]
    else:
        port = 4567

    # build url
    if host.startswith('http://') or host.startswith('https://'):
        url = f'{host}:{port}'
    else:
        url = f'http://{host}:{port}'
        
    # upload files
    try:
        for filename in files:
            auth = upload_file(url, filename, auth=auth)
    except Exception as e:
        print(f'{e.__class__.__name__} uploading to {url}')
        return 1
    
    print(f'Auth token: {auth}')

    return 0

if __name__ == '__main__':
    sys.exit(main())
