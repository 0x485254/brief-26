---
title: Guide de Démarrage
description: Guide pour démarrer avec l'API EasyGroup
---

# Guide de Démarrage avec EasyGroup

Bienvenue dans le guide de démarrage d'EasyGroup. Cette section vous aidera à comprendre comment installer, configurer et commencer à utiliser l'API EasyGroup pour la création intelligente de groupes d'apprenants.

## Vue d'ensemble

EasyGroup est une application qui permet de créer des groupes équilibrés à partir de listes de personnes, en tenant compte de divers critères comme l'âge, l'expérience, les profils, etc. L'API EasyGroup fournit toutes les fonctionnalités nécessaires pour gérer les utilisateurs, les listes, les personnes et les groupes.

## Contenu de cette section

Dans cette section, vous trouverez les guides suivants :

- [**Installation**](/getting-started/installation) - Comment installer et configurer l'API EasyGroup
- [**Premiers pas**](/getting-started/first-steps) - Comment commencer à utiliser l'API EasyGroup
- [**Authentification**](/getting-started/authentication) - Comment s'authentifier auprès de l'API EasyGroup

## Prérequis

Avant de commencer, assurez-vous d'avoir les éléments suivants :

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- PostgreSQL 14 ou supérieur
- Docker et Docker Compose (recommandé pour le déploiement simplifié)

## Aperçu des fonctionnalités

L'API EasyGroup offre les fonctionnalités suivantes :

- **Gestion des utilisateurs** : Inscription, connexion, gestion des profils
- **Gestion des listes** : Création, modification, suppression et partage de listes de personnes
- **Gestion des personnes** : Ajout, modification et suppression de personnes avec leurs caractéristiques
- **Création de groupes** : Génération automatique de groupes équilibrés selon divers critères
- **Historique des tirages** : Conservation de l'historique des tirages et des groupes créés

## Points de terminaison principaux

Voici un aperçu des principaux points de terminaison de l'API :

### Authentification
- `POST /api/auth/register` - Enregistrement d'un nouvel utilisateur
- `POST /api/auth/login` - Authentification d'un utilisateur

### Authentification par Cookie (HTTP-Only)
- `POST /api/auth/cookie/register` - Enregistrement d'un nouvel utilisateur avec cookie
- `POST /api/auth/cookie/login` - Authentification d'un utilisateur avec cookie
- `POST /api/auth/cookie/logout` - Déconnexion d'un utilisateur (suppression du cookie)

Pour plus de détails sur les points de terminaison disponibles, consultez la [Référence de l'API](/api-reference/).

## Étapes suivantes

Pour commencer à utiliser l'API EasyGroup, suivez le guide d'[Installation](/getting-started/installation) pour configurer votre environnement.