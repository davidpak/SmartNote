import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import Dropzone from '../components/Dropzone';
import Button from '../components/Button';
import H1 from '../components/H1';
import H2 from '../components/H2';
import Body from '../components/Body';
import YouTubeUpload from './YouTubeUpload';

const FileUpload = ({
  next,
  updateFiles,
}: {
  next: () => void;
  updateFiles: (files: string[]) => void;
}) => {
  const [files, setFiles] = useState<File[]>([]);
  const [links, setLinks] = useState<string[]>([]);
  const [errors, setErrors] = useState<(string | null)[]>([]);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const hasErrors = () => errors.filter((error) => error !== null).length > 0;

  useEffect(() => {
    const login = async () => {
      try {
        const res = await fetch('http://localhost:4567/api/v1/login', {
          method: 'POST',
          credentials: 'include',
        });
        if (!res.ok) {
          throw new Error('HTTP error ' + res.status);
        }
        setIsLoggedIn(true);
      } catch (e) {
        console.error(e);
      }
    };
    if (!isLoggedIn) {
      login();
    }
  }, []);

  const uploadFiles = () => {
    if (!isLoggedIn) {
      throw new Error('not authenticated');
    }

    files.forEach(async (file) => {
      const reader = new FileReader();
      reader.readAsArrayBuffer(file);
      reader.onload = async () => {
        const res = await fetch(
          `http://localhost:4567/api/v1/upload?name=${file.name}`,
          {
            method: 'POST',
            credentials: 'include',
            body: file,
          }
        );
        if (!res.ok) {
          throw new Error('HTTP error ' + res.status);
        }
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
        <Link to='/about' tabIndex={-1}>
          <Button variant='secondary'>Learn More</Button>
        </Link>
        <img src={'/splash.png'} alt='' className='max-w-xl' />
      </section>
      <section className='flex flex-col gap-6 items-center w-full'>
        <div className='flex flex-col gap-4 items-center'>
          <H2>Upload Files</H2>
          <Body>
            Learning takes place in all kinds of formats. Upload your files
            here:
          </Body>
        </div>
        <div className='flex gap-4 w-full max-w-4xl justify-center'>
          <Dropzone
            files={files}
            setFiles={(files) => {
              setFiles(files);
              updateFiles(files.map((file) => `session:uploads/${file.name}`));
            }}
            errors={errors}
            setErrors={(errors) => setErrors(errors)}
            hasErrors={hasErrors}
            className='basis-1/2'
          />
          <YouTubeUpload
            links={links}
            setLinks={(links) => {
              setLinks(links);
              // updateFiles(links.map((file) => `${file.name}`));
            }}
            className='basis-1/2'
          />
        </div>
      </section>
      <Button
        {...((files.length === 0 || hasErrors()) && { disabled: true })}
        onClick={() => {
          uploadFiles();
          next();
        }}
      >
        Continue
      </Button>
    </div>
  );
};

export default FileUpload;
