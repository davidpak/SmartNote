import { render, screen, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';
import React from 'react';

const componentSmokeTest = ({
  Component,
  props,
  children,
}: {
  Component: React.FC<any>;
  props?: Record<string, any>;
  children?: React.ReactNode;
}) => {
  afterEach(cleanup);

  it('should render without errors', () => {
    render(
      <Component {...props} data-testid='test-id'>
        {children}
      </Component>
    );
    expect(screen.getByTestId('test-id')).toBeInTheDocument();
  });

  it('should accept additional classes', () => {
    render(
      <Component {...props} className='test-class' data-testid='test-id'>
        {children}
      </Component>
    );
    expect(screen.getByTestId('test-id')).toHaveClass('test-class');
  });
};

export default componentSmokeTest;
