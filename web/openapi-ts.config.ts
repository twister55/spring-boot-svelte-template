import { defineConfig } from '@hey-api/openapi-ts';

// Generates the typed API client from the same schema.yaml the backend
// controller interfaces are generated from (see build.gradle). Output is
// not committed — `pnpm run openapi` (wired into dev/build/check/lint/test/knip/prepare) rebuilds it.
export default defineConfig({
	input: '../schema.yaml',
	output: 'src/lib/api/generated',
	plugins: [
		{
			name: '@hey-api/client-fetch',
			// Don't bake schema.yaml's `servers:` URL into the client: without a baseUrl,
			// requests stay same-origin (vite proxy in dev, Spring Boot static serving in prod).
			baseUrl: false,
			// SDK functions throw on failure instead of returning { data, error };
			// the error interceptor in src/lib/api/index.ts shapes what is thrown.
			throwOnError: true,
		},
		{
			name: '@hey-api/sdk',
			// SDK functions return response data directly, not { data, request, response }.
			responseStyle: 'data',
		},
	],
});
