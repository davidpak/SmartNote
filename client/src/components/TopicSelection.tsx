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
  prev: () => void;
  next: () => void;
}

const TopicSelection = ({
  files,
  prev,
  next,
  className,
  ...rest
}: TopicSelectionType) => {
  const [activeIndex, setActiveIndex] = useState<number>(0);

  const jsonData = {
    value: 'title',
    label: 'Exploring Quaternions for 3D Orientation',
    children: [
        { value: 'overview', label: 'General Overview' },
        {
          value: 'concept',
          label: 'Key Concepts',
          children: [
              { value: 'c1', label: 'Concept 1: Quaternions' },
              { value: 'c2', label: 'Concept 2: 3D Orientation' },
              { value: 'c3', label: 'Concept 3: Applications' }
          ],
        },
        {
          value: 'section',
          label: 'Section by Section Breakdown',
          children: [
              { value: 's1', label: '1. Introduction and Context' },
              { value: 's2', label: '2. Importance of Quaternions' },
              { value: 's3', label: '3. Quaternion Multiplication' }
          ],
        }
    ],
  };

  const data = [jsonData];

  return (
    <div
      className={twMerge('flex flex-col items-center gap-10', className)}
      {...rest}
    >
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-16'
        onClick={() => prev()}
      >
        Back
      </Button>
      <H2 className='text-center'>Select Topics to Include</H2>
      <section className='flex bg-neutral-100 max-w-5xl'>
        <Sidebar
          files={files}
          activeIndex={activeIndex}
          selectFile={(index) => setActiveIndex(index)}
          className='border-neutral-400 border-r-2 pr-1 pt-5'
        />
        <section className='border-neutral-400 border-r-2 pt-5 px-2'>
          <H3 className='text-base mb-2 pl-5'>Breakdown</H3>
          <CheckTree data={data} defaultExpandAll showIndentLine />
        </section>
        <section className='p-6'>
          <H3 className='text-base mb-2'>Output Preview</H3>
          <article className='flex flex-col bg-white p-6 gap-5'>
            <H3>Exploring Quaternions for 3D Orientation</H3>
            <section>
              <H3 className='text-base mb-2'>General Overview</H3>
              <p>This video explores the concept of quaternions and their application in describing 3D orientation. Quaternions are a 4-dimensional number system that provide a robust and bug-free method for representing 3D rotations. The video highlights the importance of quaternions in computer graphics, robotics, virtual reality, and other fields involving 3D orientation.</p>
            </section>
            <section>
              <H3 className='text-base mb-2'>Key Concepts</H3>
              <ul className='list-disc pl-6 flex flex-col gap-3'>
                <li>
                  <p className='font-bold'>Concept 1: Quaternions</p>
                  <p>Quaternions are a 4-dimensional number system used to represent 3D rotations.</p>
                </li>
                <li>
                  <p className='font-bold'>Concept 2: 3D Orientation</p>
                  <p>Quaternions provide a reliable way to describe and manipulate 3D orientation without encountering bugs or edge cases.</p>
                </li>
                <li>
                  <p className='font-bold'>Concept 3: Applications</p>
                  <p>Quaternions are widely used in computer graphics, robotics, virtual reality, and other fields involving 3D orientation.</p>
                </li>
              </ul>
            </section>
            <section>
              <H3 className='text-base mb-2'>Section by Section Breakdown</H3>
              <ol className='list-decimal pl-6'>
                <li>
                  <p className='font-bold'>Introduction and Context</p>
                  <ul className='list-disc pl-6'>
                    <li>The video introduces the collaboration with Ben Eater and the explorable videos created.</li>
                    <li>Quaternions are briefly mentioned as a 4-dimensional number system for describing 3D orientation.</li>
                  </ul>
                </li>
                <li>
                  <p className='font-bold'>Importance of Quaternions</p>
                  <ul className='list-disc pl-6'>
                    <li>Quaternions are highlighted as a preferred method for describing 3D orientation due to their bug-free nature.</li>
                    <li>The example of using quaternions to track a phone's orientation in software is mentioned.</li>
                  </ul>
                </li>
                <li>
                  <p className='font-bold'>Quaternion Multiplication</p>
                  <ul className='list-disc pl-6'>
                    <li>The method for quaternion multiplication is reviewed, including the use of half the angle and multiplying from the right by the inverse.</li>
                    <li>The goal is to break down and visualize the computation of quaternion multiplication.</li>
                  </ul>
                </li>
              </ol>
            </section>
          </article>
        </section>
      </section>
      <Button onClick={() => next()}>Continue</Button>
    </div>
  );
};

export default TopicSelection;
