import { Outlet } from 'react-router-dom';
import { useState } from 'react';

import Navbar from '../components/Navbar';
import { PageContextProvider } from '../contexts/PageContext';

const Root = () => {
  const [pageIndex, setPageIndex] = useState<number>(() => {
    const i = localStorage.getItem('index');
    return i ? parseInt(i, 10) : 0;
  });

  return (
    <PageContextProvider
      value={{
        pageIndex: pageIndex,
        next: () => {
          setPageIndex((prevIndex) => {
            const newIndex = prevIndex + 1;
            localStorage.setItem('index', newIndex.toString());
            return newIndex;
          });
        },
        prev: () => {
          setPageIndex((prevIndex) => {
            const newIndex = prevIndex - 1;
            localStorage.setItem('index', newIndex.toString());
            return newIndex;
          });
        },
        home: () => {
          setPageIndex(0);
          localStorage.setItem('index', '0');
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
