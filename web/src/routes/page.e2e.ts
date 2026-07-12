import { expect, test } from '@playwright/test';

test('home page has expected heading', async ({ page }) => {
	await page.goto('/');
	await expect(page.locator('h1')).toContainText('SvelteKit app');
});

test('navigating to about page shows about content', async ({ page }) => {
	await page.goto('/');
	await page.click('a[href="/about"]');
	await expect(page).toHaveURL('/about');
	await expect(page.locator('h1')).toContainText('About this app');
});
