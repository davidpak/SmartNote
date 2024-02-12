import { twMerge } from 'tailwind-merge';

const H2 = ({
  children,
  className,
  ...rest
}: React.HTMLAttributes<HTMLHeadingElement>) => {
  return (
    <h2 className={twMerge('font-bold text-3xl', className)} {...rest}>
      {children}
    </h2>
  );
};

export default H2;
