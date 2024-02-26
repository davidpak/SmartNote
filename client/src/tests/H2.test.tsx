import { render, screen, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

import H2 from '../components/H2';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({ Component: H2, children: 'test' });

it('should render heading with text', () => {
  render(<H2>test</H2>);
  expect(screen.getByRole('heading')).toHaveTextContent('test');
});
