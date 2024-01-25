const H3 = ({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) => {
  return <h3 className={`${className ?? ''} font-bold text-xl`}>{children}</h3>;
};

export default H3;
