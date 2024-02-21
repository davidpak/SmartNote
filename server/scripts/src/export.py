#!/usr/bin/env python3

from typing import *
from datetime import datetime

import sys
import urllib.parse
import cmdline as cl
import json
import serverutil as su

redirect_uri = 'http://localhost'

base_url = 'http://localhost:4567'

file_name = 'output.md'
input_file = f'../../private/{file_name}'

#with open(input_file, 'rb') as f:
#    data = f.read()
 #   f.close()

#with open('../../private/notion_token', 'r') as f:
#    token = f.read().strip()
#    f.close()

with open('../../private/notion_parent', 'r') as f:
    parent = f.read().strip()
    f.close()

with open('../../private/notion_code', 'r') as f:
    code = f.read().strip()
    f.close()

if len(sys.argv) > 1:
    code = sys.argv[1]
    print('code manually set to', code)

session = None

r = su.login(base_url)
print('login returned', r.status_code)
if r.status_code != 200:
    print(r.text)
    sys.exit(1)

session = r.session

print('session token:', session.token)

endpoint = f'api/v1/upload?name={file_name}'

#r = su.post(base_url, f'api/v1/upload?name={file_name}', data=data, session=session)
#print('upload returned', r.status_code)
#if r.status_code != 200:
#    print(r.text)
#    sys.exit(1)

#result = json.loads(r.text)
#resource = result['name']
#print('resource:', resource)

#integration_information = {
#    'token': token
#}

remote_information = {
    'mode': 'new',
    #'page': parent,
    'code': code,
    'redirectUri': redirect_uri,
    #'integration': integration_information
}

export_options = {
    'source': 'public:output.md',
    #'output': 'I specified the name in JSON',
    'exporter': 'notion',
    'remote': remote_information,
}

data = json.dumps(export_options).encode('utf-8')

r = su.post(base_url, 'api/v1/export', data=data, session=session)
print('export returned', r.status_code)
if r.status_code != 200:
    print(r.text)
    sys.exit(1)

print(r.text)
