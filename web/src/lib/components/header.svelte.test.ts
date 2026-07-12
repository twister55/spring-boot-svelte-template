import { expect, it } from 'vitest';
import { render } from 'vitest-browser-svelte';

import Header from './header.svelte';

it('renders navigation links to Home and About', async () => {
	const screen = render(Header);

	await expect.element(screen.getByRole('link', { name: 'Home' })).toBeInTheDocument();
	await expect.element(screen.getByRole('link', { name: 'About' })).toBeInTheDocument();
});
