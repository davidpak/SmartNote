import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

import FileUpload from '../components/FileUpload';
import Customization from '../components/Customization';
import TopicSelection from '../components/TopicSelection';
import ConnectToNotion from '../components/ConnectToNotion';
import ExportSuccess from '../components/ExportSuccess';
import { usePageContext } from '../contexts/PageContext';

const Home = () => {
  const [fileList, setFileList] = useState<string[]>([]);
  const { pageIndex, next, prev, home } = usePageContext();
  const [searchParams, setSearchParams] = useSearchParams();

  useEffect(() => {
    if (searchParams.has('code')) {
      searchParams.delete('code');
    }

    if (searchParams.has('state')) {
      searchParams.delete('state');
      setSearchParams(searchParams);
    }
  }, []);

  const renderPage = () => {
    switch (pageIndex) {
      case 0:
        return (
          <FileUpload
            next={next}
            updateFiles={(files: string[]) => setFileList(files)}
          />
        );
      case 1:
        return <Customization files={fileList} prev={prev} next={next} />;
      case 2:
        return <TopicSelection files={[]} prev={prev} next={next} />;
      case 3:
        return <ConnectToNotion prev={prev} next={next} />;
      case 4:
        return <ExportSuccess prev={prev} goHome={home} />;
      default:
        home();
        return null;
    }
  };

  return renderPage();
};

export default Home;
