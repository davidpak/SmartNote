#!/usr/bin/env python3

from typing import *
from datetime import datetime

import sys
import urllib.parse
import cmdline as cl
import json
import serverutil as su

def upload_file(base_url: str, filename: str) -> su.Response:    
    print(f'Upload {filename}... ', end='')

    with open(filename, 'rb') as f:
        data = f.read()
        f.close()

    filename_safe = urllib.parse.quote(filename)
    endpoint = f'api/v1/upload?name={filename_safe}'

    r = su.post(base_url, endpoint, data=data)
    if r.status_code == 200:
        response = json.loads(r.text)
        print(f'Done ({len(data)} bytes transfered to {response["name"]})')
        return True
    else:
        print(f'Failed: {r.status_code} {r.reason}')
        print(r.text)
        return False
    
def usage():
    print('Usage: ./upload.py [options...] <host> [files...]')
    print('Upload one or more files to the server using the upload RPC.')
    print('Options:')
    print('  -h  --help           Show this help message and exit')
    print('  -a, --auth <token>   Authentication token')

    return 0

def main(try_again=True) -> int:
    switches = [
        cl.Switch('help', value=usage),
        cl.Switch('auth', type=str)
    ]

    res = cl.parse(sys.argv, switches=switches)

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
    for filename in files:
        upload_file(url, filename)
    
    return 0

if __name__ == '__main__':
    sys.exit(main())
