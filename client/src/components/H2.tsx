import { twMerge } from 'tailwind-merge';

interface H2Type extends React.HTMLAttributes<HTMLHeadingElement> {
  children: React.ReactNode;
}

const H2 = ({ children, className, ...rest }: H2Type) => {
  return (
    <h2 className={twMerge('font-bold text-[2rem]', className)} {...rest}>
      {children}
    </h2>
  );
};

export default H2;
