#!/usr/bin/env python3

import requests
import sys
import urllib.parse

# Upload file
def upload_file(base_url, filename, auth=None):
    print(f'Upload {filename}... ', end='')

    with open(filename, 'rb') as f:
        data = f.read()
        f.close()

    filename_safe = urllib.parse.quote(filename)
    url = f'{base_url}/upload?filename={filename_safe}'

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

# Print usage
def usage():
    print('Usage: ./upload.py [options...] <host> [files...]')
    print('Options:')
    print('  -h  --help           Show this help message and exit')
    print('  -a, --auth <token>   Authentication token')

def main():
    if len(sys.argv) < 2:
        usage()
        return 1

    # options
    host = None
    url = None
    auth = None
    files = []

    # parse command line
    i = 1
    while i < len(sys.argv):
        if sys.argv[i] in ('-h', '--help'):
            usage()
            return 0
        elif sys.argv[i] in ('-a', '--auth'):
            i = i + 1
            if i >= len(sys.argv):
                print('Missing auth token')
                return 1
            auth = sys.argv[i]
        elif sys.argv[i].startswith('-'):
            print(f'Unknown option: {sys.argv[i]}')
            return 1
        elif host is None:
            host = sys.argv[i]
        else:
            files.append(sys.argv[i])

        i = i + 1

    if host is None:
        print('Missing host')
        return 1

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
