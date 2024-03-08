import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import Root from './pages/Root';
import About from './pages/About';
import Help from './pages/Help';
import ErrorPage from './pages/ErrorPage';
import FileUpload from './pages/FileUpload';
import Customization from './pages/Customization';
import TopicSelection from './pages/TopicSelection';
import ConnectToNotion from './pages/ConnectToNotion';
import ExportSuccess from './pages/ExportSuccess';

import './index.css';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Root />,
    children: [
      {
        path: '/',
        element: <FileUpload />,
      },
      {
        path: '/customize',
        element: <Customization />,
      },
      {
        path: '/select',
        element: <TopicSelection />,
      },
      {
        path: '/connect',
        element: <ConnectToNotion />,
      },
      {
        path: '/success',
        element: <ExportSuccess />,
      },
      {
        path: '/about',
        element: <About />,
      },
      {
        path: '/help',
        element: <Help />,
      },
      {
        path: '*',
        element: <ErrorPage />,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
