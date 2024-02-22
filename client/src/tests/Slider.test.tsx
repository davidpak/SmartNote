import { render, screen, cleanup, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';

import Slider from '../components/Slider';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({
  Component: Slider,
  props: {
    label: 'Verbosity',
    levels: ['Low', 'Medium', 'High'],
  },
});

const slider = <Slider label='Verbosity' levels={['Low', 'Medium', 'High']} data-testid='slider-test'/>;

it('should have correct label', () => {
  render(slider);
  expect(screen.getByTestId('slider-test')).toHaveTextContent('Verbosity');
});

it('should have correct default value', () => {
  render(slider);
  const elt = screen.getByRole('slider');
  expect(elt).toHaveAttribute('type', 'range');
  expect(elt).toHaveValue('0');
  expect(screen.getByRole('status')).toHaveTextContent('Low');
});

it('should update value when moving slider', () => {
  render(slider);
  const elt = screen.getByRole('slider');
  fireEvent.change(elt, { target: { value: 1 } });
  expect(elt).toHaveValue('1');
  expect(screen.getByRole('status')).toHaveTextContent('Medium');
});
