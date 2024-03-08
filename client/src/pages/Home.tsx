import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

import FileUpload from './FileUpload';
import Customization from './Customization';
import TopicSelection, { JsonType } from './TopicSelection';
import ConnectToNotion from './ConnectToNotion';
import ExportSuccess from './ExportSuccess';
import Loading from '../components/Loading';
import { usePageContext } from '../contexts/PageContext';

const Home = () => {
  const { pageIndex, home } = usePageContext();
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
          <FileUpload updateFiles={(files: string[]) => setFileList(files)} />
        );
      case 1:
        return (
          <Customization
            files={fileList}
            setMd={(md: string) => setMd(md)}
            setJson={(json: JsonType) => setJson(json)}
          />
        );
      case 2:
        return md && json ? (
          <TopicSelection md={md} json={json} />
        ) : (
          <Loading />
        );
      case 3:
        return <ConnectToNotion />;
      case 4:
        return <ExportSuccess />;
      default:
        home();
        return null;
    }
  };

  return renderPage();
};

export default Home;
