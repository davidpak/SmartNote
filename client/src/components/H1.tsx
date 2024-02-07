import { twMerge } from 'tailwind-merge';

interface H1Type extends React.HTMLAttributes<HTMLHeadingElement> {
  children: React.ReactNode;
}

const H1 = ({ children, className, ...rest }: H1Type) => {
  return (
    <h1
      className={twMerge(
        'font-bold text-[4rem] leading-[3rem] text-accent',
        className
      )}
      {...rest}
    >
      {children}
    </h1>
  );
};

export default H1;
