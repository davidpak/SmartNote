import { IoMdArrowBack as Arrow } from "react-icons/io";
import { useNavigate } from "react-router-dom";
import { twMerge } from 'tailwind-merge';

import H2 from "./H2";
import H3 from "./H3";
import Body from "./Body";
import Button from "./Button";
import Toggle from "./Toggle";
import Slider from "./Slider";

interface CustomizationType extends React.HTMLAttributes<HTMLDivElement> {
  files: string[];
}

const Customization = ({
  files,
  className,
  ...rest
}: CustomizationType) => {
  const navigate = useNavigate();

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
      className={twMerge('flex flex-col items-center gap-10 mx-36', className)}
      {...rest}
    >
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-0'
        onClick={() => navigate(-1)}
      >
        Back
      </Button>
      <div className='flex flex-col text-center gap-5'>
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
        onClick={() => generateNotes()}
      >
        Generate!
      </Button>
    </div>
  );
};

export default Customization;
