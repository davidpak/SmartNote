import { IoMdWarning as Alert } from 'react-icons/io';

const Warning = ({ children }: { children: React.ReactNode }) => {
  return (
    <div className='flex gap-4 px-4 py-3 w-full bg-warning-light rounded-md items-center'>
      <Alert className='text-warning' size={28} />
      {children}
    </div>
  );
};

export default Warning;
