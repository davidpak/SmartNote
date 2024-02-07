import { useRouteError } from 'react-router-dom';

interface Error {
  statusText?: string;
  message?: string;
}

const ErrorPage = () => {
  const error = useRouteError() as Error;
  console.error(error);

  return <></>;
};

export default ErrorPage;
