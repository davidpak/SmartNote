import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';

import H3 from '../components/H3';
import componentSmokeTest from './componentSmokeTest';

componentSmokeTest({ Component: H3, children: 'test' });

it('should render heading with text', () => {
  render(<H3>test</H3>);
  expect(screen.getByRole('heading')).toHaveTextContent('test');
});
