import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

import FileUpload from '../components/FileUpload';
import Customization from '../components/Customization';
import TopicSelection, { JsonType } from '../components/TopicSelection';
import ConnectToNotion from '../components/ConnectToNotion';
import ExportSuccess from '../components/ExportSuccess';
import Loading from '../components/Loading';
import { usePageContext } from '../contexts/PageContext';

const Home = () => {
  const { pageIndex, next, prev, home } = usePageContext();
  const [searchParams, setSearchParams] = useSearchParams();
  const [fileList, setFileList] = useState<string[]>([]);
  const [md, setMd] = useState<string>('');
  const [json, setJson] = useState<JsonType>();

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
        return (
          <Customization
            files={fileList}
            prev={prev}
            next={next}
            setMd={(md: string) => setMd(md)}
            setJson={(json: JsonType) => setJson(json)}
          />
        );
      case 2:
        return md && json ? (
          <TopicSelection prev={prev} next={next} md={md} json={json} />
        ) : (
          <Loading prev={prev} />
        );
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
