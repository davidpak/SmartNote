import { IoMdArrowBack as Arrow } from "react-icons/io";
import { Link } from "react-router-dom";
import { twMerge } from 'tailwind-merge';

import H2 from "./H2";
import Body from "./Body";
import Button from "./Button";

interface ExportSuccessType extends React.HTMLAttributes<HTMLDivElement> {
  prev: () => void;
}

const ExportSuccess = ({
  prev,
  className,
  ...rest
}: ExportSuccessType) => {
  return (
    <div
      className={twMerge('flex flex-col items-center gap-5', className)}
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
      <H2>Export Successful!</H2>
      <Body>
        Congrats! Check out your new notes page :&#41;
      </Body>
      <div className='flex gap-5'>
        <Link to='/' tabIndex={-1}>
          <Button variant='secondary'>Home</Button>
        </Link>
        <a href='https://www.notion.so/' target='_blank' tabIndex={-1}>
          <Button>Go to Notion</Button>
        </a>
      </div>
    </div>
  );
};

export default ExportSuccess;