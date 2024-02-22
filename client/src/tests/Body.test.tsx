import { render, screen, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

import Body from '../components/Body';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({ Component: Body, children: 'test' });

it('should render paragraph with text', () => {
  render(<Body>test</Body>);
  expect(screen.getByText('test')).toBeInTheDocument();
});
