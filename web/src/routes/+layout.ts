// SPA mode by default: the app is served by Spring Boot as static files
// (adapter-static with fallback: _app/fallback.html), so there is no
// server-side rendering. Purely static pages (no /api calls, no browser-only
// globals at render time) can opt into prerendering by re-enabling both flags
// in their own +page.ts — see src/routes/+page.ts and src/routes/about/+page.ts.
export const ssr = false;
export const prerender = false;
