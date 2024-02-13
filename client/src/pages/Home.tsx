import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import Dropzone from '../components/Dropzone';
import Button from '../components/Button';
import Warning from '../components/Warning';
import H1 from '../components/H1';
import H2 from '../components/H2';
import Body from '../components/Body';
import Button from '../components/Button';
import Warning from '../components/Warning';
import H1 from '../components/H1';
import H2 from '../components/H2';
import Body from '../components/Body';

const Home = () => {
  const [files, setFiles] = useState<File[]>([]);
  const [errors, setErrors] = useState<(string | null)[]>([]);
  const [jwt, setJwt] = useState<string>();

  const hasErrors = () => errors.filter((error) => error !== null).length > 0;

  // TODO: server should send JWT as an HTTP-only cookie
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
    <div className='flex flex-col items-center gap-6 text-center'>
      <H1>Extract, Customize, Export.</H1>
      <section className='flex flex-col gap-4 items-center'>
        <H2>Intelligent note-taking with AI, integrated with Notion.</H2>
        <Body>
          Streamlining the process of transforming educational content to
          readable, flexible notes.
        </Body>
        <Link to='/about'>
          <Button variant='secondary'>Learn More</Button>
        </Link>
        <img src={'/home-hero.png'} alt='' className='max-w-xl' />
      </section>
      <section className='flex flex-col gap-6 items-center w-full max-w-xl'>
        <div className='flex flex-col gap-4 items-center'>
          <H2>Upload Files</H2>
          <Body>
            Learning takes place in all kinds of formats. Upload your files
            here:
          </Body>
        </div>
        <Dropzone
          files={files}
          setFiles={(files) => setFiles(files)}
          errors={errors}
          setErrors={(errors) => setErrors(errors)}
          className='w-full'
        />
        {hasErrors() && (
          <Warning>Remove all invalid files to continue.</Warning>
        )}
      </section>
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
