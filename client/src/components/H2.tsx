import { twMerge } from 'tailwind-merge';

const H2 = ({
  children,
  className,
  ...rest
}: React.HTMLAttributes<HTMLHeadingElement>) => {
  return (
    <h2 className={twMerge('font-bold text-[2rem]', className)} {...rest}>
      {children}
    </h2>
  );
};

export default H2;
