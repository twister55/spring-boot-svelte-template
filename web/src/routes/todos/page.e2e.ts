import { expect, test } from '@playwright/test';

// The Playwright preview server has no backend behind it, so this test mocks the API
// instead of hitting a real one (see playwright.config.ts).
test('todos page lists and adds a todo', async ({ page }) => {
	const todos: Array<{ completed: boolean; createdAt: string; id: number; title: string }> = [];
	let nextId = 1;

	await page.route('**/api/v1/todos', async (route) => {
		if (route.request().method() === 'GET') {
			await route.fulfill({ json: todos });
			return;
		}
		if (route.request().method() === 'POST') {
			const body = route.request().postDataJSON();
			const todo = { id: nextId, createdAt: new Date().toISOString(), ...body };
			nextId += 1;
			todos.push(todo);
			await route.fulfill({ json: todo, status: 201 });
			return;
		}
		await route.continue();
	});

	await page.goto('/todos');
	await expect(page.locator('h1')).toContainText('Todos');

	await page.fill('#new-todo', 'Write e2e test');
	await page.click('button:has-text("Add")');
	await expect(page.getByText('Write e2e test')).toBeVisible();
});
