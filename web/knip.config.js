/** @type {import('knip').KnipConfig} */
export default {
	entry: [
		'src/routes/**/+{page,layout,server,page.server,layout.server,error}{,@*}.{js,ts,svelte}',
		'src/hooks.{js,ts}',
		'src/hooks.{server,client}.{js,ts}',
		'src/app.d.ts',
		'src/routes/**/*.css',
	],
	project: ['src/**/*.{js,ts,svelte,css}'],
	paths: {
		'$lib/*': ['./src/lib/*'],
	},
	ignore: ['.svelte-kit/**', 'static/**'],
	ignoreExportsUsedInFile: true,
};
