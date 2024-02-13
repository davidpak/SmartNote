import { useState } from 'react';
import FileUpload from '../components/FileUpload';
import Customization from '../components/Customization';
// import ExportSuccess from '../components/ExportSuccess';

const Home = () => {
  const [index, setIndex] = useState<number>(() => {
    const i = localStorage.getItem('index');
    return i ? parseInt(i, 10) : 0;
  });

  const next = () => {
    setIndex((prevIndex) => {
      const newIndex = prevIndex + 1;
      localStorage.setItem('index', newIndex.toString());
      return newIndex;
    });
  };

  const prev = () => {
    setIndex((prevIndex) => {
      const newIndex = prevIndex - 1;
      localStorage.setItem('index', newIndex.toString());
      return newIndex;
    });
  };

  const goHome = () => {
    setIndex(0);
    localStorage.setItem('index', '0');
  };

  const renderPage = () => {
    switch (index) {
      case 0:
        return <FileUpload next={next} />;
      case 1:
        return <Customization files={[]} prev={prev} next={next} />;
      // case _:
      //   return <ExportSuccess prev={prev} goHome={goHome} />;
      default:
        goHome();
        return null;
    }
  };

  return renderPage();
};

export default Home;
