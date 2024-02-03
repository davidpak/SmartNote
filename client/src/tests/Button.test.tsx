import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';

import Button from '../components/Button';

it('should render button with text', () => {
  render(<Button>test</Button>);

  expect(screen.getByRole('button')).toHaveTextContent('test');
});
