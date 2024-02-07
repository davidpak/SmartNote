import { twMerge } from 'tailwind-merge';

interface H3Type extends React.HTMLAttributes<HTMLHeadingElement> {
  children: React.ReactNode;
}

const H3 = ({ children, className, ...rest }: H3Type) => {
  return (
    <h3 className={twMerge('font-bold text-xl', className)} {...rest}>
      {children}
    </h3>
  );
};

export default H3;
