import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';

import H2 from './H2';
import H3 from './H3';
import Body from './Body';
import Button from './Button';
import Toggle from './Toggle';
import Slider from './Slider';
import Carousel from './Carousel';

interface CustomizationType extends React.HTMLAttributes<HTMLDivElement> {
  files: string[];
  prev: () => void;
  next: () => void;
}

const Customization = ({
  files,
  prev,
  next,
  className,
  ...rest
}: CustomizationType) => {
  async function generateNotes() {
    const options = {
      general: files,
      llm: null
    };

    try {
      const res = await fetch('http://localhost:4567/api/v1/generate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(options),
      });

      // status check
      if (!res.ok) {
        throw new Error('HTTP error ' + res.status);
      }

      const data = await res.json();
      console.log(data);
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <div
      className={twMerge('flex flex-col items-center gap-16', className)}
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
      <section className='flex flex-col items-center gap-8'>
        <div className='flex flex-col text-center gap-5 max-w-xl'>
          <H2>Customize Your Notes</H2>
          <Body>
            Toggle the different settings to select which content you want to include
            in your notes and adjust the verbosity level to control its level of detail!
          </Body>
        </div>
        <div className='flex gap-52'>
          <div className='flex flex-col gap-3'>
            <H3>Content to Include:</H3>
            <Toggle label='General Overview'/>
            <Toggle label='Key Concepts'/>
            <Toggle label='Sections Breakdown'/>
            <Toggle label='Vocab Words'/>
            <Toggle label='5th Grader Explanation'/>
            <Toggle label='Conclusion'/>
          </div>
          <div className='flex flex-col gap-3'>
            <H3>Additional Customization:</H3>
            <Slider
              label='Verbosity'
              levels={['Low', 'Medium', 'High']}
            />
          </div>
        </div>
        <Button
          onClick={() => {
            generateNotes();
            next();
          }}
        >
          Generate!
        </Button>
      </section>
      <section className='flex flex-col items-center gap-5'>
        <H2>Need Examples?</H2>
        <Carousel
          imagePaths={[
            '/general-overview-ex.png',
            '/key-concepts-ex.png',
            '/section-breakdown-ex.png',
            '/vocab-ex.png',
            '/5th-grader-ex.png',
            '/conclusion-ex.png'
          ]}
          titles={[
            'General Overview:',
            'Key Concepts:',
            'Sections Breakdown:',
            'Vocab words:',
            '5th Grader Explanation:',
            'Conclusion:'
          ]}
          descriptions={[
            'A top-level breakdown that captures a succinct summary of the uploaded file contents.',
            'Highlight the top concepts present with a brief summary.',
            'The core content breakdown, the bread and butter of your notes.',
            'Capture vocabulary words and definitions in a convenient and concise manner.',
            'An intuitive and easy-to-comprehend summary.',
            'Finish off your notes with a comprehensive, succinct conclusion.'
          ]}
        />
      </section>
    </div>
  );
};

export default Customization;