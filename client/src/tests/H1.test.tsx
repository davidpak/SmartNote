import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';

import H1 from '../components/H1';
import componentSmokeTest from './componentSmokeTest';

componentSmokeTest({ Component: H1, children: 'test' });

it('should render heading with text', () => {
  render(<H1>test</H1>);
  expect(screen.getByRole('heading')).toHaveTextContent('test');
});
