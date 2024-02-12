import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';

import H2 from '../components/H2';
import componentSmokeTest from './componentSmokeTest';

componentSmokeTest({ Component: H2, children: 'test' });

it('should render heading with text', () => {
  render(<H2>test</H2>);
  expect(screen.getByRole('heading')).toHaveTextContent('test');
});
