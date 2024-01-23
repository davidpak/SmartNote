const H1 = ({
  children,
  className,
}: {
  children: React.ReactNode;
  className?: string;
}) => {
  return (
    <h1
      className={`${
        className ?? ''
      } font-bold text-[4rem] leading-[3rem] text-accent`}
    >
      {children}
    </h1>
  );
};

export default H1;
