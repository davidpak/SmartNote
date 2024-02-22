#!/usr/bin/env python3

from typing import *
from datetime import datetime

import sys
import urllib.parse
import cmdline as cl
import json
import serverutil as su

base_url = 'http://localhost:4567'

input_files = ['public:test.md']

session = None

r = su.login(base_url)
print('login returned', r.status_code)
if r.status_code != 200:
    print(r.text)
    sys.exit(1)

session = r.session

print('session token:', session.token)

generate_options = {
    'general': {
        'files': input_files
    },
    'llm': {
        'velocity': 0.5
    }
}

data = json.dumps(generate_options).encode('utf-8')

r = su.post(base_url, 'api/v1/generate', data=data, session=session)
print('generate returned', r.status_code)
if r.status_code != 200:
    print(r.text)
    sys.exit(1)

print(r.text)
