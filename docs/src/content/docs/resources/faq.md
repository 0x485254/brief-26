---
title: FAQ
description: Questions fréquemment posées sur l'API EasyGroup
---

# Questions Fréquemment Posées

Cette page regroupe les questions les plus fréquemment posées sur l'API EasyGroup.

## Questions Générales

### Qu'est-ce que l'API EasyGroup ?

L'API EasyGroup est une application qui permet de créer des groupes équilibrés à partir de listes de personnes, en tenant compte de divers critères comme l'âge, le niveau de français, le niveau technique, le profil, etc.

### Quelles sont les principales fonctionnalités de l'API EasyGroup ?

Les principales fonctionnalités sont :
- Gestion des utilisateurs (inscription, connexion, gestion des profils)
- Gestion des listes de personnes (création, modification, suppression, partage)
- Gestion des personnes (ajout, modification, suppression)
- Création de groupes équilibrés selon divers critères
- Historique des tirages et des groupes créés

### L'API EasyGroup est-elle gratuite ?

Oui, l'API EasyGroup est actuellement disponible gratuitement pour tous les utilisateurs.

## Questions Techniques

### Quelles technologies sont utilisées par l'API EasyGroup ?

L'API EasyGroup est construite avec :
- Backend : Spring Boot 3.2.0, Java 17
- Base de données : PostgreSQL 14
- Sécurité : Spring Security avec Argon2id pour le hachage des mots de passe
- Conteneurisation : Docker et Docker Compose
- CI/CD : GitHub Actions

### Comment l'API gère-t-elle l'authentification ?

L'API utilise l'authentification par JWT (JSON Web Token) stocké dans un Cookie HTTP-Only, qui offre une meilleure sécurité contre les attaques XSS (Cross-Site Scripting).

### Puis-je utiliser l'API avec n'importe quel langage de programmation ?

Oui, l'API EasyGroup est une API REST standard qui peut être utilisée avec n'importe quel langage de programmation capable d'effectuer des requêtes HTTP.

## Questions sur l'Utilisation

### Comment créer un groupe équilibré ?

Pour créer un groupe équilibré, vous devez :
1. Créer une liste de personnes
2. Ajouter des personnes à cette liste avec leurs caractéristiques
3. Utiliser l'endpoint `/api/lists/{listId}/draws/preview` pour générer une prévisualisation des groupes
4. Ajuster les groupes si nécessaire
5. Utiliser l'endpoint `/api/lists/{listId}/draws` pour sauvegarder les groupes

### Comment partager une liste avec un autre utilisateur ?

Vous pouvez partager une liste en utilisant l'endpoint `/api/lists/{listId}/share` avec l'email de l'utilisateur avec qui vous souhaitez partager la liste.

### Comment modifier manuellement les groupes générés ?

Après avoir généré des groupes, vous pouvez les modifier manuellement en utilisant les endpoints suivants :
- `/api/groups/{groupId}/persons/{personId}` (POST) pour ajouter une personne à un groupe
- `/api/groups/{groupId}/persons/{personId}` (DELETE) pour retirer une personne d'un groupe
- `/api/draws/{drawId}/groups` (POST) pour créer un nouveau groupe
- `/api/groups/{groupId}` (PUT) pour modifier un groupe existant
- `/api/groups/{groupId}` (DELETE) pour supprimer un groupe

## Résolution de Problèmes

### J'ai oublié mon mot de passe, que faire ?

Actuellement, l'API ne dispose pas d'une fonctionnalité de récupération de mot de passe. Veuillez contacter l'administrateur système pour réinitialiser votre mot de passe.

### Je n'arrive pas à me connecter après l'inscription

Assurez-vous d'avoir vérifié votre adresse email en cliquant sur le lien envoyé lors de l'inscription. L'activation du compte est nécessaire avant de pouvoir se connecter.

### Les groupes générés ne sont pas équilibrés comme je le souhaite

L'algorithme de génération de groupes tente de créer des groupes aussi équilibrés que possible en fonction des critères spécifiés, mais il peut ne pas être parfait dans tous les cas. Vous pouvez ajuster manuellement les groupes après leur génération.

Pour plus d'informations sur des problèmes spécifiques, consultez notre [guide de résolution de problèmes](/resources/troubleshooting).