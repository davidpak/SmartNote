import { twMerge } from 'tailwind-merge';

const H1 = ({
  children,
  className,
  ...rest
}: React.HTMLAttributes<HTMLHeadingElement>) => {
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
