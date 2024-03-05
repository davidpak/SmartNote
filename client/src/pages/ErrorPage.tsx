import { Link } from 'react-router-dom';

import H1 from '../components/H1';
import H2 from '../components/H2';
import Button from '../components/Button';
import Body from '../components/Body';

const ErrorPage = () => {
  return (
    <div className='flex flex-col items-center gap-4 text-center max-w-xl m-auto'>
      <H1>404</H1>
      <H2>Oops! Page Not Found</H2>
      <Body>
        We searched high and low but sadly could not find the page you're
        looking for. Let's take you back home.
      </Body>
      <Link to='/' tabIndex={-1}>
        <Button>Home</Button>
      </Link>
      <img src='/404Error.png' alt='' className='w-64' />
    </div>
  );
};

export default ErrorPage;
