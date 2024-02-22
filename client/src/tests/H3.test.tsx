import { render, screen, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

import H3 from '../components/H3';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({ Component: H3, children: 'test' });

it('should render heading with text', () => {
  render(<H3>test</H3>);
  expect(screen.getByRole('heading')).toHaveTextContent('test');
});
