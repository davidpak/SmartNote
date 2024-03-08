import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';
import { useState } from 'react';

import H2 from '../components/H2';
import H3 from '../components/H3';
import Body from '../components/Body';
import Button from '../components/Button';
import Toggle from '../components/Toggle';
import Slider from '../components/Slider';
import Carousel from '../components/Carousel';
import { useNavigate } from 'react-router-dom';
import { useFilesContext } from '../contexts/FilesContext';
import { useOutputContext } from '../contexts/OutputContext';

const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

const Customization = ({
  className,
  ...rest
}: React.HTMLAttributes<HTMLDivElement>) => {
  const navigate = useNavigate();
  const { files } = useFilesContext();
  const { setMarkdown, setJson } = useOutputContext();

  const [verbosity, setVerbosity] = useState(0.0);
  const [hasOverview, setHasOverview] = useState(true);
  const [hasKeyConcepts, setHasKeyConcepts] = useState(true);
  const [hasSections, setHasSections] = useState(true);
  const [hasAdditionalInfo, setHasAdditionalInfo] = useState(true);
  const [hasVocab, setHasVocab] = useState(true);
  const [hasSimpleExplanation, setHasSimpleExplanation] = useState(true);
  const [hasConclusion, setHasConclusion] = useState(true);
  const isSelected =
    hasOverview ||
    hasKeyConcepts ||
    hasSections ||
    hasAdditionalInfo ||
    hasVocab ||
    hasSimpleExplanation ||
    hasConclusion;

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
      const res = await fetch(`${BASE_URL}/generate`, {
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
      setMarkdown(data.markdown);
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
        onClick={() => navigate('/')}
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
          {...(!isSelected && { disabled: true })}
          onClick={() => {
            generateNotes();
            navigate('/select');
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
