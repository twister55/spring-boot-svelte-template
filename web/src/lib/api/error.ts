export class ApiError extends Error {
	readonly status: number;
	readonly body: unknown;

	constructor(message: string, status: number, body?: unknown) {
		super(message);
		this.name = 'ApiError';
		this.status = status;
		this.body = body;
	}
}

// controller.abort() rejects fetch with an 'AbortError' DOMException,
// AbortSignal.timeout() with a 'TimeoutError' one; polyfills and custom fetch
// implementations may throw plain Errors with the same names, so match by name
// rather than requiring DOMException. Custom abort reasons are handled by the
// signal check in the interceptor (see index.ts).
export function isAbortError(e: unknown): boolean {
	return e instanceof Error && (e.name === 'AbortError' || e.name === 'TimeoutError');
}

/**
 * Maps an error returned by the generated client to an ApiError. Error bodies
 * from the backend ExceptionHandlers advice ({ status, message, timestamp })
 * keep their typed message; other HTTP errors fall back to the status line;
 * failures without an HTTP error status — network failures, exceptions thrown
 * while reading a 2xx response — get status 0.
 */
export function toApiError(error: unknown, response: Response | undefined): ApiError {
	// a 2xx response means the failure happened after the HTTP exchange (e.g.
	// JSON.parse on a malformed body) — status 0, like other non-HTTP failures
	const status = response && !response.ok ? response.status : 0;
	if (error && typeof error === 'object' && 'message' in error && typeof error.message === 'string') {
		return new ApiError(error.message, status, error);
	}
	if (response && !response.ok) {
		return new ApiError(`${response.status} ${response.statusText}`, response.status, error);
	}
	return new ApiError(fallbackMessage(error), 0, error);
}

function fallbackMessage(error: unknown): string {
	if (typeof error === 'string') {
		return error;
	}
	try {
		return JSON.stringify(error) ?? String(error);
	} catch {
		return String(error);
	}
}
