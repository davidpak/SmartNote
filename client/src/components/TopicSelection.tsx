import { useState } from 'react';
import { CheckTree } from 'rsuite';
import 'rsuite/dist/rsuite.min.css';
import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';

import H2 from './H2';
import H3 from './H3';
import Button from './Button';
import Sidebar from './Sidebar';
import { File } from './Sidebar';

interface TopicSelectionType extends React.HTMLAttributes<HTMLDivElement> {
  files: File[];
}

const TopicSelection = ({
  files,
  className,
  ...rest
}: TopicSelectionType) => {
  const [activeIndex, setActiveIndex] = useState<number>(0);

  const data = [{
    value: 'mars',
    label: 'Mars',
    children: [
        { value: 'phobos', label: 'Phobos' },
        { value: 'deimos', label: 'Deimos' },
    ],
  }];

  return (
    <div
      className={twMerge('flex flex-col items-center gap-10', className)}
      {...rest}
    >
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-16'
        onClick={() => {}}
      >
        Back
      </Button>
      <H2 className='text-center'>Select Topics to Include</H2>
      <section className='flex bg-neutral-100 max-w-4xl'>
        <Sidebar
          files={files}
          activeIndex={activeIndex}
          selectFile={(index) => setActiveIndex(index)}
          className='w-1/6 border-neutral-400 border-r-2 pr-1 pt-5'
        />
        <section className='w-1/4 border-neutral-400 border-r-2 overflow-auto pt-5 pr-2'>
          <H3 className='text-base mb-2 pl-5'>Breakdown</H3>
          <CheckTree data={data} defaultExpandAll showIndentLine />
        </section>
        <section className='w-3/5 pt-5 px-5'>
          <H3 className='text-base mb-2'>Output Preview</H3>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor
            incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
            exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure
            dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
            Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt...
          </p>
        </section>
      </section>
      <Button onClick={() => {}}>Continue</Button>
    </div>
  );
};

export default TopicSelection;
