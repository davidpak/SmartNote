import { render, screen, cleanup, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';

import Toggle from '../components/Toggle';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({
  Component: Toggle,
  props: {
    label: 'Key Concepts',
  },
});

const toggle = (
  <Toggle
    label='Key Concepts'
    updateToggle={() => {}}
    data-testid='toggle-test'
  />
);

it('should render with correct label', () => {
  render(toggle);
  expect(screen.getByLabelText('Key Concepts')).toHaveTextContent('Key Concepts');
});

it('should render toggle - default off', () => {
  render(toggle);
  expect(screen.getByRole('switch')).toHaveAttribute('aria-checked', 'false');
});

it('should update value when toggling', () => {
  render(toggle);
  const elt = screen.getByRole('switch');
  fireEvent.click(elt);
  expect(elt).toHaveAttribute('aria-checked', 'true');
});
