import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
export default {
	preprocess: vitePreprocess(),

	kit: {
		adapter: adapter({
			assets: 'build',
			// the SPA shell lives inside the appDir (kit.appDir, default _app): the kit
			// server hard-404s every /_app/* request before route matching, so no route —
			// prerendered or not — can ever collide with this file
			fallback: '_app/fallback.html',
		}),
	},
};
