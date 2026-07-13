// The only entry point of $lib/api: components import from '$lib/api', never
// from the internal files (enforced by eslint no-restricted-imports).
// The whole generated SDK and its model types are re-exported below, so new
// schema.yaml endpoints appear here without manual wiring.
import { isAbortError, toApiError } from './error';
import { client } from './generated/client.gen';

// The generated client returns response data directly and throws on failure
// (responseStyle + throwOnError in openapi-ts.config.ts); this interceptor
// shapes what gets thrown: every failure becomes an ApiError — HTTP error
// bodies, network failures — except cancelled requests, which pass through
// unchanged so callers can tell cancellation from a real error.
client.interceptors.error.use((error, response, _request, options) => {
	// already shaped: under Vite HMR this module can be re-evaluated against the
	// cached client, stacking interceptors — keep the wrapping idempotent
	if (error instanceof Error && error.name === 'ApiError') {
		return error;
	}
	// the signal check catches cancellations that don't look like abort errors,
	// e.g. controller.abort(customReason)
	if (options.signal?.aborted || isAbortError(error)) {
		return error;
	}
	return toApiError(error, response);
});

export { ApiError } from './error';

export * from './generated';
