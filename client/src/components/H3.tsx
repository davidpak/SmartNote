import { twMerge } from 'tailwind-merge';

const H3 = ({
  children,
  className,
  ...rest
}: React.HTMLAttributes<HTMLHeadingElement>) => {
  return (
    <h3 className={twMerge('font-bold text-xl', className)} {...rest}>
      {children}
    </h3>
  );
};

export default H3;
