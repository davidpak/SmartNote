import { IoMdArrowBack as Arrow } from "react-icons/io";
import { useNavigate, Link } from "react-router-dom";
import { twMerge } from 'tailwind-merge';

import H2 from "./H2";
import Body from "./Body";
import Button from "./Button";

const ExportSuccess = ({
  className,
  ...rest
}: React.HTMLAttributes<HTMLDivElement>) => {
  const navigate = useNavigate();

  return (
    <div
      className={twMerge('flex flex-col items-center gap-5 mx-36', className)}
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
      <H2>Export Successful!</H2>
      <Body>
        Congrats! Check out your new notes page :&#41;
      </Body>
      <div className='flex gap-5'>
        <Link to='/' tabIndex={-1}>
          <Button variant='secondary'>Home</Button>
        </Link>
        <a href='https://www.notion.so/' target='_blank'>
          <Button>Go to Notion</Button>
        </a>
      </div>
    </div>
  );
};

export default ExportSuccess;