import path from 'node:path';

import js from '@eslint/js';
import { defineConfig, includeIgnoreFile } from 'eslint/config';
import { createTypeScriptImportResolver } from 'eslint-import-resolver-typescript';
import importPlugin from 'eslint-plugin-import-x';
import perfectionist from 'eslint-plugin-perfectionist';
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended';
import svelte from 'eslint-plugin-svelte';
import unusedImports from 'eslint-plugin-unused-imports';
import globals from 'globals';
import ts from 'typescript-eslint';

const gitignorePath = path.resolve(import.meta.dirname, '.gitignore');

export default defineConfig(
	includeIgnoreFile(gitignorePath),
	js.configs.recommended,
	ts.configs.recommended,
	svelte.configs.recommended,
	importPlugin.flatConfigs.recommended,
	importPlugin.flatConfigs.typescript,
	{
		languageOptions: {
			globals: {
				...globals.browser,
			},
		},
		plugins: {
			perfectionist,
			'unused-imports': unusedImports,
		},
		settings: {
			// Resolve imports through tsconfig paths
			'import-x/resolver-next': [createTypeScriptImportResolver()],
		},
		rules: {
			// typescript-eslint strongly recommend that you do not use the no-undef lint rule on TypeScript projects.
			// see: https://typescript-eslint.io/troubleshooting/faqs/eslint/#i-get-errors-from-the-no-undef-rule-about-global-variables-not-being-defined-even-though-there-are-no-typescript-errors
			'no-undef': 'off',

			'object-shorthand': 'error',
			eqeqeq: ['error', 'always', { null: 'ignore' }],
			'prefer-template': 'error',
			'no-console': ['warn', { allow: ['warn', 'error'] }],
			'no-nested-ternary': 'error',
			'no-promise-executor-return': 'error',
			'no-await-in-loop': 'warn',
			'no-restricted-syntax': [
				'error',
				{ selector: 'ForInStatement', message: 'Use Object.keys/entries/values instead of for...in.' },
				{ selector: 'WithStatement', message: 'with is not allowed.' },
				{
					// Multiline inline handler/callback in Svelte markup (block body) — extract to a named function.
					selector: "SvelteAttribute > SvelteMustacheTag > ArrowFunctionExpression[body.type='BlockStatement']",
					message: 'Avoid multi-line markup handlers. Extract a named function.',
				},
			],
			'@typescript-eslint/no-inferrable-types': 'error',
			'no-throw-literal': 'error',
			'@typescript-eslint/array-type': ['error', { default: 'array-simple' }],
			'@typescript-eslint/no-non-null-assertion': 'warn',

			// unused-imports: auto-removes unused imports via --fix
			'@typescript-eslint/no-unused-vars': 'off',
			'unused-imports/no-unused-imports': 'error',
			'unused-imports/no-unused-vars': [
				'error',
				{
					args: 'all',
					argsIgnorePattern: '^_',
					caughtErrors: 'all',
					caughtErrorsIgnorePattern: '^_',
					destructuredArrayIgnorePattern: '^_',
					varsIgnorePattern: '^_',
					ignoreRestSiblings: true,
				},
			],

			// type imports: import type { A } for type-only, import { fn, type A } for mixed
			'@typescript-eslint/consistent-type-imports': [
				'error',
				{
					prefer: 'type-imports',
					fixStyle: 'inline-type-imports',
					disallowTypeAnnotations: false,
				},
			],
			'@typescript-eslint/no-import-type-side-effects': 'error',

			// import-x
			'import-x/no-extraneous-dependencies': ['error', { devDependencies: true }],
			'import-x/no-named-as-default-member': 'off',
			'import-x/no-duplicates': ['error', { considerQueryString: true }],
			'import-x/newline-after-import': 'error',
			// SvelteKit virtual modules ($app/*, $env/*, $service-worker)
			'import-x/no-unresolved': ['error', { ignore: ['^\\$app/', '^\\$env/', '^\\$service-worker$'] }],
			'import-x/order': [
				'error',
				{
					groups: ['builtin', 'external', 'internal', 'parent', 'sibling', 'index'],
					pathGroups: [
						// $app/* — SvelteKit runtime, after external packages
						{ pattern: '$app/**', group: 'external', position: 'after' },
						// $lib/* — our own code, before relative imports
						{ pattern: '$lib/**', group: 'internal', position: 'before' },
						{ pattern: '$lib', group: 'internal', position: 'before' },
					],
					pathGroupsExcludedImportTypes: ['builtin'],
					'newlines-between': 'always',
					alphabetize: { order: 'asc', caseInsensitive: true },
				},
			],

			'perfectionist/sort-named-imports': [
				'error',
				{
					type: 'natural',
					// value imports first, then type imports
					groups: ['value-import', 'type-import'],
				},
			],

			'svelte/block-lang': ['error', { script: 'ts' }],
			'svelte/no-navigation-without-resolve': 'off',
			'svelte/no-at-debug-tags': 'error',
			'svelte/no-inspect': 'error',
			'svelte/button-has-type': 'error',

			// No direct imports from barrel-module internals
			'no-restricted-imports': [
				'error',
				{
					patterns: [
						{
							group: ['$lib/api/*', '$lib/api/*/**'],
							message: "Import from '$lib/api', not from internal files.",
						},
					],
				},
			],
		},
	},
	// Root config files run in Node
	{
		files: ['*.config.ts', '*.config.js'],
		languageOptions: {
			globals: {
				...globals.node,
			},
		},
	},
	// $lib/api internals import each other — the barrel rule is for the rest of the app
	{
		files: ['**/lib/api/**'],
		rules: {
			'no-restricted-imports': 'off',
		},
	},
	// svelte/* submodules resolve to a single types/index.d.ts — false positive in no-duplicates
	{
		files: ['**/*.svelte', '**/*.svelte.ts', '**/*.svelte.js'],
		languageOptions: {
			parserOptions: {
				projectService: true,
				extraFileExtensions: ['.svelte'],
				parser: ts.parser,
			},
		},
		rules: {
			'import-x/no-duplicates': 'off',
		},
	},
	svelte.configs.prettier,

	// Keep last — enables the prettier/prettier rule
	eslintPluginPrettierRecommended,

	// eslint-config-prettier (bundled in prettier/recommended) turns off curly;
	// re-enable it after prettier so it actually runs. Line length is left to
	// prettier's printWidth — max-len is deliberately not restored, because it
	// conflicts with how prettier wraps whitespace-sensitive markup.
	{
		rules: {
			curly: ['error', 'all'],
		},
	},
);
