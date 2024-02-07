import { useState } from 'react';
import Dropzone from '../components/Dropzone';

const Home = () => {
  const [files, setFiles] = useState<File[]>([]);
  const [errors, setErrors] = useState<(string | null)[]>([]);

  return (
    <div className='flex flex-col gap-4'>
      <Dropzone
        files={files}
        setFiles={(files) => setFiles(files)}
        errors={errors}
        setErrors={(errors) => setErrors(errors)}
      />
    </div>
  );
};

export default Home;
