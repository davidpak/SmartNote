const Warning = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  return (
    <div className="flex w-96 h-10 pl-4 bg-warning rounded-md items-center">
      {children}
    </div>
  );
};

export default Warning