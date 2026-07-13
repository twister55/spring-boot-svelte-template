import { expect, it, vi } from 'vitest';
import { render } from 'vitest-browser-svelte';

import TodoList from './todo-list.svelte';

vi.mock('$lib/api', () => {
	class ApiError extends Error {}

	const now = new Date().toISOString();
	let nextId = 2;

	return {
		ApiError,
		createTodo: vi.fn(async ({ body }: { body: { completed: boolean; title: string } }) => ({
			id: nextId++,
			createdAt: now,
			...body,
		})),
		deleteTodo: vi.fn(async () => undefined),
		getTodos: vi.fn(async () => [{ id: 1, title: 'Existing todo', completed: false, createdAt: now }]),
		updateTodo: vi.fn(
			async ({ body, path }: { body: { completed: boolean; title: string }; path: { id: number } }) => ({
				id: path.id,
				createdAt: now,
				...body,
			}),
		),
	};
});

it('lists, adds, toggles and deletes todos', async () => {
	const screen = render(TodoList);

	await expect.element(screen.getByText('Existing todo')).toBeInTheDocument();

	await screen.getByLabelText('New todo title').fill('Write docs');
	await screen.getByRole('button', { name: 'Add' }).click();
	await expect.element(screen.getByText('Write docs')).toBeInTheDocument();

	const checkbox = screen.getByRole('checkbox', { name: 'Write docs' });
	await expect.element(checkbox).not.toBeChecked();
	await checkbox.click();
	await expect.element(checkbox).toBeChecked();

	await screen.getByRole('button', { name: 'Delete Write docs' }).click();
	await expect.element(screen.getByText('Write docs')).not.toBeInTheDocument();
});
