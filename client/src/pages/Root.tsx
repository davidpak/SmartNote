import { Outlet } from 'react-router-dom';
import { useState } from 'react';

import Navbar from '../components/Navbar';
import { PageContextProvider } from '../contexts/PageContext';

const Root = () => {
  const [pageIndex, setPageIndex] = useState<number>(0);

  return (
    <PageContextProvider
      value={{
        pageIndex: pageIndex,
        next: () => {
          setPageIndex((prevIndex) => prevIndex + 1);
        },
        prev: () => {
          setPageIndex((prevIndex) => prevIndex - 1);
        },
        home: () => {
          setPageIndex(0);
        },
      }}
    >
      <Navbar />
      <main className='px-16 py-14'>
        <Outlet />
      </main>
    </PageContextProvider>
  );
};

export default Root;
