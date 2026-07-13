<script lang="ts">
	import TodoList from '$lib/components/todo-list.svelte';
</script>

<svelte:head>
	<title>Todos</title>
	<meta name="description" content="Todo list example" />
</svelte:head>

<section class="flex w-full flex-col items-center">
	<h1>Todos</h1>
	<TodoList />
</section>
