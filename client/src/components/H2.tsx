const H2 = ({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) => {
  return (
    <h2 className={`${className ?? ''} font-bold text-[2rem]`}>{children}</h2>
  );
};

export default H2;
