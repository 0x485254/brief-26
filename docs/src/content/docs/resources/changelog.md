---
title: Journal des Modifications
description: Historique des versions et des changements de l'API EasyGroup
---

# Journal des Modifications

Cette page présente l'historique des versions et des changements apportés à l'API EasyGroup.

## Version 1.0.0 (Actuelle)

### Fonctionnalités principales

- Système d'authentification complet avec JWT dans des cookies HTTP-Only
- Vérification d'email lors de l'inscription
- Gestion des utilisateurs (inscription, connexion, profil)
- Gestion des listes de personnes
- Partage de listes entre utilisateurs
- Gestion des personnes avec attributs spécifiques (âge, genre, niveau de français, niveau technique, profil)
- Génération de groupes équilibrés avec prévisualisation
- Modification manuelle des groupes générés
- Historique des tirages et des groupes

### Détails techniques

- Backend : Spring Boot 3.2.0, Java 17
- Base de données : PostgreSQL 14
- Sécurité : Spring Security avec Argon2id pour le hachage des mots de passe
- Conteneurisation : Docker et Docker Compose
- CI/CD : GitHub Actions

## Version 0.9.0 (Beta)

**Date de sortie** : 1 novembre 2023

### Fonctionnalités

- Première version beta publique
- Système d'authentification de base
- Gestion des listes et des personnes
- Génération de groupes (algorithme initial)
- Interface API REST

### Améliorations

- N/A (première version beta)

### Corrections de bugs

- N/A (première version beta)

## Prochaines Fonctionnalités Planifiées

Les fonctionnalités suivantes sont planifiées pour les prochaines versions :

### Version 1.1.0 (Planifiée)

- Récupération de mot de passe
- Recherche avancée de personnes
- Filtrage amélioré des personnes
- Ajout de plusieurs personnes en une seule requête (batch)
- Déplacement de personnes entre listes
- Amélioration de l'algorithme d'équilibrage des groupes

### Version 1.2.0 (Planifiée)

- Interface d'administration
- Statistiques sur les groupes générés
- Export des données en différents formats (CSV, Excel, PDF)
- Intégration avec des services tiers (Google Classroom, Microsoft Teams)
- API GraphQL en complément de l'API REST

## Politique de Versionnement

L'API EasyGroup suit la convention de versionnement sémantique ([SemVer](https://semver.org/)) :

- **Version majeure (X.0.0)** : Changements incompatibles avec les versions précédentes
- **Version mineure (0.X.0)** : Ajout de fonctionnalités compatibles avec les versions précédentes
- **Version de correctif (0.0.X)** : Corrections de bugs compatibles avec les versions précédentes

## Compatibilité

L'API EasyGroup s'engage à maintenir la compatibilité des API au sein d'une même version majeure. Les changements incompatibles seront clairement documentés et ne seront introduits que dans les nouvelles versions majeures.