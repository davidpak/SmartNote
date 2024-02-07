import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';

import Button from '../components/Button';
import componentSmokeTest from './componentSmokeTest';

componentSmokeTest({ Component: Button, children: 'test' });

it('should render button with text', () => {
  render(<Button>test</Button>);
  expect(screen.getByRole('button')).toHaveTextContent('test');
});

it('should accept onClick handler', () => {
  const onClick = jest.fn();
  render(<Button onClick={onClick}>test</Button>);
  fireEvent.click(screen.getByRole('button'));
  expect(onClick).toHaveBeenCalled();
});
