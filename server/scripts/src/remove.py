#!/usr/bin/env python3

from typing import *

import sys
import urllib.parse
import cmdline as cl
import serverutil as su

def upload_file(base_url: str, resc: str) -> su.Response:    
    print(f'Remove {resc}... ', end='')

    resc_safe = urllib.parse.quote(resc)
    endpoint = f'api/v1/remove?name={resc_safe}'

    r = su.post(base_url, endpoint)

    if r.status_code == 200:
        print(f'Removed')
    else:
        print(f'Failed: {r.status_code} {r.reason}')

    return r

def usage():
    print('Usage: ./remove.py [options...] <host> [resource...]')
    print('Remove one or more files from the server using the remove RPC.')
    print('Options:')
    print('  -h  --help           Show this help message and exit')
    print('  -a, --auth <token>   Authentication token')

    return 0

def main() -> int:
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
    resources = args[1:]
    
    if host is None:
        print('Missing host')
        return 1
    assert isinstance(host, str)

    if len(resources) == 0:
        print('No resources')
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
    for resc in resources:
        upload_file(url, resc)

    return 0

if __name__ == '__main__':
    sys.exit(main())
