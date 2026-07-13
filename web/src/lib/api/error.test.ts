import { describe, expect, it } from 'vitest';

import { isAbortError, toApiError } from './error';

describe('toApiError', () => {
	it('uses the typed message from an ExceptionHandlers body', () => {
		const body = { status: 404, message: 'chart not found', timestamp: '2026-01-01T00:00:00Z' };
		const resp = new Response(null, { status: 404, statusText: 'Not Found' });

		const err = toApiError(body, resp);

		expect(err.message).toBe('chart not found');
		expect(err.status).toBe(404);
		expect(err.body).toEqual(body);
	});

	it('falls back to the status line for a non-JSON body', () => {
		const resp = new Response(null, { status: 502, statusText: 'Bad Gateway' });

		const err = toApiError('<html>oops</html>', resp);

		expect(err.message).toBe('502 Bad Gateway');
		expect(err.status).toBe(502);
	});

	it('falls back to the status line for JSON without a message', () => {
		const resp = new Response(null, { status: 400, statusText: 'Bad Request' });

		const err = toApiError({ error: 'x' }, resp);

		expect(err.message).toBe('400 Bad Request');
		expect(err.status).toBe(400);
	});

	it('maps a network failure (no response) to status 0', () => {
		const err = toApiError(new TypeError('Failed to fetch'), undefined);

		expect(err.message).toBe('Failed to fetch');
		expect(err.status).toBe(0);
	});

	it('maps a parse failure of a 2xx response to status 0, not the 2xx status', () => {
		const resp = new Response(null, { status: 200, statusText: 'OK' });

		const err = toApiError(new SyntaxError('Unexpected end of JSON input'), resp);

		expect(err.message).toBe('Unexpected end of JSON input');
		expect(err.status).toBe(0);
	});

	it('serializes a message-less object instead of "[object Object]"', () => {
		const err = toApiError({ code: 'NAV' }, undefined);

		expect(err.message).toBe('{"code":"NAV"}');
		expect(err.status).toBe(0);
	});
});

describe('isAbortError', () => {
	it('recognizes an AbortError DOMException (controller.abort())', () => {
		expect(isAbortError(new DOMException('The user aborted a request.', 'AbortError'))).toBe(true);
	});

	it('recognizes a TimeoutError DOMException (AbortSignal.timeout())', () => {
		expect(isAbortError(new DOMException('The operation timed out.', 'TimeoutError'))).toBe(true);
	});

	it('recognizes a plain Error named AbortError (polyfilled fetch)', () => {
		const e = new Error('aborted');
		e.name = 'AbortError';

		expect(isAbortError(e)).toBe(true);
	});

	it('rejects ordinary errors', () => {
		expect(isAbortError(new TypeError('Failed to fetch'))).toBe(false);
	});
});
