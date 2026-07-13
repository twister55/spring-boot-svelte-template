<script lang="ts">
	import { onMount } from 'svelte';

	import { ApiError, createTodo, deleteTodo, getTodos, updateTodo, type TodoDto } from '$lib/api';

	let todos = $state<TodoDto[]>([]);
	let newTitle = $state('');
	let loading = $state(true);
	let error = $state('');

	onMount(() => {
		loadTodos();
	});

	async function loadTodos() {
		loading = true;
		error = '';
		try {
			todos = await getTodos();
		} catch (err) {
			error = describeError(err, 'Failed to load todos');
		} finally {
			loading = false;
		}
	}

	async function handleAdd(event: SubmitEvent) {
		event.preventDefault();
		const title = newTitle.trim();
		if (!title) {
			return;
		}
		error = '';
		try {
			const created = await createTodo({ body: { title, completed: false } });
			todos = [...todos, created];
			newTitle = '';
		} catch (err) {
			error = describeError(err, 'Failed to add todo');
		}
	}

	async function handleToggle(todo: TodoDto) {
		error = '';
		try {
			const updated = await updateTodo({
				body: { title: todo.title, completed: !todo.completed },
				path: { id: todo.id },
			});
			todos = todos.map((existing) => (existing.id === updated.id ? updated : existing));
		} catch (err) {
			error = describeError(err, 'Failed to update todo');
		}
	}

	async function handleDelete(todo: TodoDto) {
		error = '';
		try {
			await deleteTodo({ path: { id: todo.id } });
			todos = todos.filter((existing) => existing.id !== todo.id);
		} catch (err) {
			error = describeError(err, 'Failed to delete todo');
		}
	}

	function describeError(err: unknown, fallback: string) {
		return err instanceof ApiError ? err.message : fallback;
	}
</script>

<div class="mx-auto w-full max-w-md">
	<form class="flex gap-2" onsubmit={handleAdd}>
		<label class="sr-only" for="new-todo">New todo title</label>
		<input
			id="new-todo"
			class="flex-1 rounded border border-gray-300 px-3 py-2"
			placeholder="What needs doing?"
			bind:value={newTitle}
		/>
		<button type="submit" class="rounded bg-(--color-theme-1) px-4 py-2 font-bold text-white">Add</button>
	</form>

	{#if error}
		<p class="mt-3 text-red-600" role="alert">{error}</p>
	{/if}

	{#if loading}
		<p class="mt-4">Loading…</p>
	{:else if todos.length === 0}
		<p class="mt-4 text-gray-500">No todos yet.</p>
	{:else}
		<ul class="mt-4 flex flex-col gap-2">
			{#each todos as todo (todo.id)}
				<li class="flex items-center gap-2 rounded border border-gray-200 px-3 py-2">
					<input id={`todo-${todo.id}`} type="checkbox" checked={todo.completed} onchange={() => handleToggle(todo)} />
					<label for={`todo-${todo.id}`} class="flex-1" class:line-through={todo.completed}>{todo.title}</label>
					<button
						type="button"
						class="text-red-600"
						aria-label={`Delete ${todo.title}`}
						onclick={() => handleDelete(todo)}
					>
						✕
					</button>
				</li>
			{/each}
		</ul>
	{/if}
</div>
