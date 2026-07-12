import { expect, it } from 'vitest';
import { render } from 'vitest-browser-svelte';

import Counter from './counter.svelte';

it('increments and decrements the count', async () => {
	const screen = render(Counter);

	const increment = screen.getByLabelText('Increase the counter by one');
	const decrement = screen.getByLabelText('Decrease the counter by one');

	await expect.element(screen.getByText('0')).toBeInTheDocument();

	await increment.click();
	await expect.element(screen.getByText('1')).toBeInTheDocument();

	await decrement.click();
	await decrement.click();
	await expect.element(screen.getByText('-1')).toBeInTheDocument();
});
