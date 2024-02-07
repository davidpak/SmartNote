import { IoMdWarning as Alert } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';

interface WarningType extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
}

const Warning = ({ children, className, ...rest }: WarningType) => {
  return (
    <div
      className={twMerge(
        'flex gap-4 px-4 py-3 w-full bg-warning-light rounded-md items-center',
        className
      )}
      {...rest}
    >
      <Alert className='text-warning' size={28} />
      {children}
    </div>
  );
};

export default Warning;
