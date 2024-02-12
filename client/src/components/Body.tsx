import { twMerge } from 'tailwind-merge';

const Body = ({
  children,
  className,
  ...rest
}: React.HTMLAttributes<HTMLParagraphElement>) => {
  return (
    <p className={twMerge('text-neutral-475', className)} {...rest}>
      {children}
    </p>
  );
};

export default Body;
