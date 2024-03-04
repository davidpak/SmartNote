import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';
import { useState } from 'react';

import H2 from './H2';
import H3 from './H3';
import Body from './Body';
import Button from './Button';
import Toggle from './Toggle';
import Slider from './Slider';
import Carousel from './Carousel';
import { JsonType } from './TopicSelection';

interface CustomizationType extends React.HTMLAttributes<HTMLDivElement> {
  files: string[];
  prev: () => void;
  next: () => void;
  setMd: (md: string) => void;
  setJson: (json: JsonType) => void;
}

const Customization = ({
  files,
  prev,
  next,
  setMd,
  setJson,
  className,
  ...rest
}: CustomizationType) => {
  const [verbosity, setVerbosity] = useState(0.0);
  const [hasOverview, setHasOverview] = useState(false);
  const [hasKeyConcepts, setHasKeyConcepts] = useState(false);
  const [hasSections, setHasSections] = useState(false);
  const [hasAdditionalInfo, setHasAdditionalInfo] = useState(false);
  const [hasVocab, setHasVocab] = useState(false);
  const [hasSimpleExplanation, setHasSimpleExplanation] = useState(false);
  const [hasConclusion, setHasConclusion] = useState(false);

  async function generateNotes() {
    const options = {
      general: {
        files: files,
        includeJson: true,
        includeMarkdown: true,
      },
      llm: {
        verbosity: verbosity,
        generalOverview: hasOverview,
        keyConcepts: hasKeyConcepts,
        sectionBySection: hasSections,
        additionalInformation: hasAdditionalInfo,
        helpfulVocabulary: hasVocab,
        explainToFifthGrader: hasSimpleExplanation,
        conclusion: hasConclusion,
      },
    };

    try {
      const res = await fetch('http://localhost:4567/api/v1/generate', {
        method: 'POST',
        credentials: 'include',
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
      setMd(data.markdown);
      setJson(data.result.children);
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
            Toggle the different settings to select which content you want to
            include in your notes and adjust the verbosity level to control its
            level of detail!
          </Body>
        </div>
        <div className='flex gap-52'>
          <div className='flex flex-col gap-3'>
            <H3>Content to Include:</H3>
            <Toggle label='General Overview' updateToggle={setHasOverview} />
            <Toggle label='Key Concepts' updateToggle={setHasKeyConcepts} />
            <Toggle label='Sections Breakdown' updateToggle={setHasSections} />
            <Toggle
              label='Additional Information'
              updateToggle={setHasAdditionalInfo}
            />
            <Toggle label='Vocab Words' updateToggle={setHasVocab} />
            <Toggle
              label='5th Grader Explanation'
              updateToggle={setHasSimpleExplanation}
            />
            <Toggle label='Conclusion' updateToggle={setHasConclusion} />
          </div>
          <div className='flex flex-col gap-3'>
            <H3>Additional Customization:</H3>
            <Slider
              label='Verbosity'
              levels={['Low', 'Medium', 'High']}
              updateLevel={(level) => setVerbosity(level)}
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
            '/additional-info-ex.png',
            '/vocab-ex.png',
            '/5th-grader-ex.png',
            '/conclusion-ex.png',
          ]}
          titles={[
            'General Overview:',
            'Key Concepts:',
            'Sections Breakdown:',
            'Additional Information:',
            'Vocab words:',
            '5th Grader Explanation:',
            'Conclusion:',
          ]}
          descriptions={[
            'A top-level breakdown that captures a succinct summary of the uploaded file contents.',
            'Highlight the top concepts present with a brief summary.',
            'The core content breakdown, the bread and butter of your notes.',
            'Extra information that might be of interest to you.',
            'Capture vocabulary words and definitions in a convenient and concise manner.',
            'An intuitive and easy-to-comprehend summary.',
            'Finish off your notes with a comprehensive, succinct conclusion.',
          ]}
        />
      </section>
    </div>
  );
};

export default Customization;
