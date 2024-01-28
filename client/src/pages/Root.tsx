import { Outlet } from 'react-router-dom';

import Navbar from '../components/Navbar';

const Root = () => {
  return (
    <>
      <Navbar />
      <main className='px-16 py-10'>
        <Outlet />
      </main>
    </>
  );
};

export default Root;
