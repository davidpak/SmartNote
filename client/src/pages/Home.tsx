import { useState } from 'react';
import FileUpload from '../components/FileUpload';

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

  const renderPage = () => {
    switch (index) {
      case 0:
        return <FileUpload next={next} />;
      // add other pages here
      default:
        // in case something goes wrong, just go back to the initial page
        setIndex(0);
        localStorage.setItem('index', '0');
        return null;
    }
  };

  return renderPage();
};

export default Home;
