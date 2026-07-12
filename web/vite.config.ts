import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import { playwright } from '@vitest/browser-playwright';
import { loadEnv } from 'vite';
import { defineConfig } from 'vitest/config';

export default defineConfig(({ mode }) => {
	const { APP_API_SERVER } = loadEnv(mode, process.cwd(), '');

	return {
		plugins: [tailwindcss(), sveltekit()],
		server: {
			proxy: {
				'^/api.*': {
					target: APP_API_SERVER || 'http://localhost:8080',
					secure: false,
				},
			},
		},
		test: {
			// Fail any test that runs without making an assertion.
			expect: { requireAssertions: true },
			// Two projects: component tests run in a real browser, everything else in Node.
			projects: [
				{
					extends: true,
					test: {
						name: 'client',
						// *.svelte.test.ts / *.svelte.spec.ts files render Svelte components in a real browser.
						include: ['src/**/*.svelte.{test,spec}.{js,ts}'],
						browser: {
							enabled: true,
							headless: true,
							provider: playwright(),
							instances: [{ browser: 'chromium' }],
						},
					},
				},
				{
					extends: true,
					test: {
						name: 'server',
						// Plain unit tests (utils, stores, pure logic) run in Node.
						environment: 'node',
						include: ['src/**/*.{test,spec}.{js,ts}'],
						exclude: ['src/**/*.svelte.{test,spec}.{js,ts}'],
					},
				},
			],
		},
	};
});
