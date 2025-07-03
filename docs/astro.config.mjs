// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import mermaid from 'astro-mermaid';

// https://astro.build/config
export default defineConfig({
	integrations: [
		mermaid({
			theme: 'forest'
		}),
		starlight({
			title: 'EasyGroup API Documentation',
			social: [
				{ icon: 'github', label: 'GitHub', href: 'https://github.com/withastro/starlight' }
			],
			sidebar: [
				{
					label: 'Introduction',
					items: [
						{ label: 'Accueil', link: '/' },
					],
				},
				{
					label: 'Guide de Démarrage',
					items: [
						{ label: 'Vue d\'ensemble', link: '/getting-started/' },
						{ label: 'Installation', link: '/getting-started/installation' },
						{ label: 'Premiers pas', link: '/getting-started/first-steps' },
						{ label: 'Authentification', link: '/getting-started/authentication' },
					],
				},
				{
					label: 'Guide Utilisateur',
					items: [
						{ label: 'Vue d\'ensemble', link: '/user-guide/' },
						{ label: 'Gestion des utilisateurs', link: '/user-guide/user-management' },
						{ label: 'Gestion des listes', link: '/user-guide/list-management' },
						{ label: 'Gestion des personnes', link: '/user-guide/person-management' },
						{ label: 'Création de groupes', link: '/user-guide/group-creation' },
					],
				},
				{
					label: 'Référence de l\'API',
					items: [
						{ label: 'Vue d\'ensemble', link: '/api-reference/' },
						{ label: 'Authentification', link: '/api-reference/authentication' },
						{ label: 'Utilisateurs', link: '/api-reference/users' },
						{ label: 'Listes', link: '/api-reference/lists' },
						{ label: 'Personnes', link: '/api-reference/persons' },
						{ label: 'Tirages et Groupes', link: '/api-reference/draws-groups' },
					],
				},
				{
					label: 'Documentation Technique',
					items: [
						{ label: 'Vue d\'ensemble', link: '/technical-docs/' },
						{ label: 'Architecture', link: '/technical-docs/architecture' },
						{ label: 'Modèles de données', link: '/technical-docs/data-models' },
						{ label: 'Flux métiers', link: '/technical-docs/business-flows' },
						{ label: 'Sécurité', link: '/technical-docs/security' },
						{ label: 'Déploiement', link: '/technical-docs/deployment' },
					],
				},
				{
					label: 'Récits Utilisateurs',
					items: [
						{ label: 'Vue d\'ensemble', link: '/user-stories/' },
						{ label: 'Authentification', link: '/user-stories/authentication' },
						{ label: 'Gestion des listes', link: '/user-stories/list-management' },
						{ label: 'Gestion des personnes', link: '/user-stories/person-management' },
						{ label: 'Création de groupes', link: '/user-stories/group-creation' },
						{ label: 'Administration', link: '/user-stories/administration' },
					],
				},
				{
					label: 'Ressources',
					items: [
						{ label: 'Vue d\'ensemble', link: '/resources/' },
						{ label: 'FAQ', link: '/resources/faq' },
						{ label: 'Résolution de problèmes', link: '/resources/troubleshooting' },
						{ label: 'Journal des modifications', link: '/resources/changelog' },
						{ label: 'Glossaire', link: '/resources/glossary' },
					],
				},
			],
			customCss: [
				// Path to your custom CSS file
				'./src/styles/custom.css',
			],
		}),
	],
});
