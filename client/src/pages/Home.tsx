import { useEffect, useState } from 'react';
import Dropzone from '../components/Dropzone';
import Button from '../components/Button';
import Warning from '../components/Warning';

const Home = () => {
  const [files, setFiles] = useState<File[]>([]);
  const [errors, setErrors] = useState<(string | null)[]>([]);
  const [jwt, setJwt] = useState<string>();

  const hasErrors = () => errors.filter((error) => error !== null).length > 0;

  // TODO: save jwt in context or redux
  useEffect(() => {
    const login = async () => {
      if (!jwt) {
        const res = await fetch('http://localhost:4567/api/v1/login', {
          method: 'POST',
        });
        if (!res.ok) {
          throw new Error('HTTP error ' + res.status);
        }
        setJwt(res.headers.get('Authorization')!);
      }
    };

    login();
  }, [jwt]);

  const uploadFiles = () => {
    if (!jwt) {
      throw new Error('not authenticated');
    }
    const reader = new FileReader();

    files.forEach(async (file) => {
      reader.readAsArrayBuffer(file);
      reader.onload = async () => {
        const res = await fetch(
          `http://localhost:4567/api/v1/upload?name=${file.name}`,
          {
            method: 'POST',
            headers: {
              Authorization: jwt,
            },
            body: file,
          }
        );
        if (!res.ok) {
          throw new Error('HTTP error ' + res.status);
        }
        const json = await res.json();
        console.log(json);
      };
      reader.onerror = () => console.error(reader.error);
    });
  };

  return (
    <div className='flex flex-col gap-8 items-center w-1/2'>
      <Dropzone
        files={files}
        setFiles={(files) => setFiles(files)}
        errors={errors}
        setErrors={(errors) => setErrors(errors)}
        className='w-full'
      />
      {hasErrors() && <Warning>Remove all invalid files to continue.</Warning>}
      {/* TODO: display warning banner if there are invalid files or user
      removes all files, disable button when there are errors or no files
      uploaded yet */}
      {/* <Warning>Upload at least one file to continue.</Warning> */}
      <Button
        {...((files.length === 0 || hasErrors()) && { disabled: true })}
        onClick={() => uploadFiles()}
      >
        Continue
      </Button>
    </div>
  );
};

export default Home;
