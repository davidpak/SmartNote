from typing import *
from datetime import datetime

import requests
import os

class Session:
    """
    Represents a session with the server.
    """

    def __init__(self, token: str, expires: int):
        self.token = token
        self.expires = expires

    def write(self):
        """
        Write the session to the `.session` file.
        """

        with open('.session', 'w') as f:
            f.write(f'{self.token}\n{self.expires}')
            f.close()

    @staticmethod
    def read() -> Union['Session', None]:
        """
        Read the session from the `.session` file.

        Returns:
        The session, if any.
        """

        try:
            with open('.session', 'r') as f:
                lines = f.readlines()
                f.close()

                if len(lines) != 2:
                    return None

                token = lines[0].strip()
                expires = int(lines[1].strip())

                # check if token is expired
                if expires < datetime.now().timestamp():
                    os.remove('.session')
                    return None

                return Session(token, expires)
        except FileNotFoundError:
            return None
        
    def __str__(self):
        return f'Session(token={self.token}, expires={self.expires})'
    
    def __repr__(self):
        return str(self)

class Response:
    def __init__(self, response: requests.Response):
        self.status_code = response.status_code
        self.reason = response.reason
        self.text = response.text

        self.session = None
        for cookie in response.cookies:
            if cookie.name.lower() == 'session':
                self.session = Session(cookie.value, cookie.expires)
                break
    
    def __str__(self):
        return f'Response(status_code={self.status_code}, reason={self.reason}, text={self.text}, session={self.session})'
    
    def __repr__(self):
        return str(self)

def post(
        base_url: str, endpoint: str,
        data: Union[bytes, str, None] = None,
        session: Union[Session, None] = None,
        authenticate: bool = True) -> Response:
    """
    Send a post request to the server.

    Parameters:
    - `base_url`: Server base URL.
    - `data`: Data to send.
    - `session`: Session object. If `None` and `authenticate` is `True`,
                 then a session is read from the `.session` file or
                 requested from the server.
    - `authenticate`: If `True`, then the request is authenticated.

    Returns:
    The server response.
    """

    url = f'{base_url}/{endpoint}'

    if not session and authenticate:
        session = Session.read()

    cookies = {}
    if session:
        cookies['session'] = session.token

    r = Response(requests.post(url, data=data, cookies=cookies))
    if r.status_code == 401:
        try:
            os.remove('.session')
        except FileNotFoundError:
            pass

        if authenticate:
            r = login(base_url)
            return post(base_url, endpoint, data, r.session, False)
    elif r.status_code == 200 and r.session:
        r.session.write()

    return r

def login(base_url: str) -> Response:
    return post(base_url, 'api/v1/login', authenticate=False)
