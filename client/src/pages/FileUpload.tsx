import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { twMerge } from 'tailwind-merge';

import Dropzone from '../components/Dropzone';
import Button from '../components/Button';
import H1 from '../components/H1';
import H2 from '../components/H2';
import Body from '../components/Body';
import YouTubeUpload, { VideoType } from '../components/YouTubeUpload';
import { useFilesContext } from '../contexts/FilesContext';

const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

const FileUpload = ({
  className,
  ...rest
}: React.HTMLAttributes<HTMLDivElement>) => {
  const navigate = useNavigate();
  const { setFiles: updateFiles } = useFilesContext();
  const [files, setFiles] = useState<File[]>([]);
  const [videos, setVideos] = useState<VideoType[]>([]);
  const [errors, setErrors] = useState<(string | null)[]>([]);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const hasErrors = () => errors.filter((error) => error !== null).length > 0;

  useEffect(() => {
    const login = async () => {
      try {
        const res = await fetch(`${BASE_URL}/login`, {
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
        const res = await fetch(`${BASE_URL}/upload?name=${file.name}`, {
          method: 'POST',
          credentials: 'include',
          body: file,
        });
        if (!res.ok) {
          throw new Error('HTTP error ' + res.status);
        }
      };

      reader.onerror = () => console.error(reader.error);
    });
  };

  return (
    <div
      className={twMerge(
        'flex flex-col items-center gap-6 text-center',
        className
      )}
      {...rest}
    >
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
        <div className='flex flex-col md:flex-row gap-4 w-full max-w-4xl justify-center'>
          <Dropzone
            files={files}
            setFiles={(files) => {
              setFiles(files);
            }}
            errors={errors}
            setErrors={(errors) => setErrors(errors)}
            hasErrors={hasErrors}
            className='basis-1/2'
          />
          <YouTubeUpload
            videos={videos}
            setVideos={(videos) => {
              setVideos(videos);
            }}
            className='basis-1/2'
          />
        </div>
      </section>
      <Button
        {...((files.length === 0 || hasErrors()) &&
          videos.length === 0 && { disabled: true })}
        onClick={() => {
          updateFiles([
            ...files.map((file) => `session:uploads/${file.name}`),
            ...videos.map((video) => video.url),
          ]);
          uploadFiles();
          navigate('/customize');
        }}
      >
        Continue
      </Button>
    </div>
  );
};

export default FileUpload;
