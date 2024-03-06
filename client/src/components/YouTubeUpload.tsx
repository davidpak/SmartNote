import { IoLinkOutline as Link } from 'react-icons/io5';

import FileList from './FileList';
import Button from './Button';
import { useState } from 'react';

export type VideoType = {
  name: string;
  url: string;
};

interface YoutubeUploadType extends React.HTMLAttributes<HTMLDivElement> {
  videos: VideoType[];
  setVideos: (videos: VideoType[]) => void;
}

const YouTubeUpload = ({
  videos,
  setVideos,
  className,
  ...rest
}: YoutubeUploadType) => {
  const [input, setInput] = useState<string>('');

  const addLink = () => {
    if (isValidLink(input)) {
      // TODO: get video name
      const video = {
        name: 'name',
        url: input,
      };
      setVideos([...videos, video]);
    } else {
    }
  };

  const isValidLink = (url: string) => {
    // Source: https://stackoverflow.com/questions/19377262/regex-for-youtube-url
    const pattern =
      /^((?:https?:)?\/\/)?((?:www|m)\.)?((?:youtube(-nocookie)?\.com|youtu\.be))(\/(?:[\w\-]+\?v=|embed\/|live\/|v\/)?)([\w\-]+)(\S+)?$/;
    return pattern.test(url);
  };

  const removeLink = (idx: number) => {
    setVideos(videos.filter((_, index) => idx !== index));
  };

  return (
    <section className={className} {...rest}>
      <div
        className='p-8 border border-dashed border-neutral-300 rounded-3xl text-center h-56'
        {...rest}
      >
        <form
          onSubmit={(e) => {
            e.preventDefault();
            setInput('');
            addLink();
          }}
          className='flex flex-col items-center justify-center gap-4 h-full'
        >
          <Link size={40} className='text-neutral-500' aria-hidden />
          <label
            className='font-medium text-lg text-neutral-500'
            htmlFor='youtubeInput'
          >
            Insert a valid YouTube URL
          </label>
          <div className='flex gap-3'>
            <input
              type='text'
              id='youtubeInput'
              className='border border-neutral-400 rounded-lg px-3 w-56'
              placeholder='youtube.com/watch...'
              value={input}
              onChange={(e) => setInput(e.target.value)}
            />
            <Button type='submit' variant='secondary' className='text-sm'>
              Upload
            </Button>
          </div>
        </form>
      </div>
      <FileList
        files={videos}
        errors={[]}
        removeFile={removeLink}
        className='px-4'
      />
    </section>
  );
};

export default YouTubeUpload;
