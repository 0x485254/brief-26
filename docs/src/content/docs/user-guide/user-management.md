---
title: Gestion des Utilisateurs
description: Guide de gestion des comptes utilisateurs dans l'API EasyGroup
---

# Gestion des Utilisateurs

Ce guide explique comment gérer les comptes utilisateurs dans l'API EasyGroup, y compris la création, la modification et la suppression de comptes.

## Vue d'ensemble

L'API EasyGroup propose plusieurs endpoints pour gérer les comptes utilisateurs :

- Création d'un compte utilisateur
- Récupération des informations d'un utilisateur
- Modification des informations d'un utilisateur
- Modification du mot de passe
- Suppression d'un compte utilisateur

## Création d'un Compte Utilisateur

La création d'un compte utilisateur se fait via l'endpoint d'inscription, comme expliqué dans le [guide d'authentification](/getting-started/authentication).

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "votre_nom_utilisateur",
    "email": "votre_email@exemple.com",
    "password": "votre_mot_de_passe"
  }'
```

## Récupération des Informations d'un Utilisateur

### Récupération de son propre profil

Pour récupérer les informations de votre propre compte utilisateur, utilisez l'endpoint `/api/users/me` :

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse similaire à :

```json
{
  "id": 1,
  "username": "votre_nom_utilisateur",
  "email": "votre_email@exemple.com",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T12:34:56Z"
}
```

### Récupération du profil d'un autre utilisateur

Pour récupérer les informations d'un autre utilisateur (si vous avez les permissions nécessaires), utilisez l'endpoint `/api/users/{userId}` :

```bash
curl -X GET http://localhost:8080/api/users/2 \
  -H "Authorization: Bearer votre_token_jwt"
```

## Modification des Informations d'un Utilisateur

Pour modifier les informations de votre compte utilisateur, utilisez l'endpoint `/api/users/me` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "username": "nouveau_nom_utilisateur",
    "email": "nouvel_email@exemple.com"
  }'
```

Vous recevrez une réponse contenant les informations mises à jour :

```json
{
  "id": 1,
  "username": "nouveau_nom_utilisateur",
  "email": "nouvel_email@exemple.com",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z"
}
```

## Modification du Mot de Passe

Pour modifier votre mot de passe, utilisez l'endpoint `/api/users/me/password` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/users/me/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "currentPassword": "votre_mot_de_passe_actuel",
    "newPassword": "votre_nouveau_mot_de_passe"
  }'
```

En cas de succès, vous recevrez une réponse avec le code 200 OK.

## Suppression d'un Compte Utilisateur

Pour supprimer votre compte utilisateur, utilisez l'endpoint `/api/users/me` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/users/me \
  -H "Authorization: Bearer votre_token_jwt"
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Gestion des Rôles et Permissions

### Rôles disponibles

L'API EasyGroup définit plusieurs rôles pour les utilisateurs :

- **USER** : Rôle de base pour tous les utilisateurs
- **ADMIN** : Rôle administrateur avec des permissions étendues

### Attribution de rôles (Administrateurs uniquement)

Les administrateurs peuvent attribuer des rôles aux utilisateurs via l'endpoint `/api/users/{userId}/roles` :

```bash
curl -X PUT http://localhost:8080/api/users/2/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "roles": ["USER", "ADMIN"]
  }'
```

## Recherche d'Utilisateurs

### Recherche par nom d'utilisateur

Pour rechercher des utilisateurs par nom d'utilisateur, utilisez l'endpoint `/api/users/search` :

```bash
curl -X GET "http://localhost:8080/api/users/search?username=jean" \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant les utilisateurs correspondants :

```json
[
  {
    "id": 2,
    "username": "jean_dupont",
    "email": "jean.dupont@exemple.com",
    "createdAt": "2023-07-03T12:34:56Z",
    "updatedAt": "2023-07-03T12:34:56Z"
  },
  {
    "id": 3,
    "username": "jeanne_martin",
    "email": "jeanne.martin@exemple.com",
    "createdAt": "2023-07-03T12:34:56Z",
    "updatedAt": "2023-07-03T12:34:56Z"
  }
]
```

## Gestion des Préférences Utilisateur

### Récupération des préférences

Pour récupérer vos préférences utilisateur, utilisez l'endpoint `/api/users/me/preferences` :

```bash
curl -X GET http://localhost:8080/api/users/me/preferences \
  -H "Authorization: Bearer votre_token_jwt"
```

### Modification des préférences

Pour modifier vos préférences utilisateur, utilisez l'endpoint `/api/users/me/preferences` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/users/me/preferences \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "theme": "dark",
    "language": "fr",
    "notifications": {
      "email": true,
      "push": false
    }
  }'
```

## Gestion des Sessions

### Récupération des sessions actives

Pour récupérer la liste de vos sessions actives, utilisez l'endpoint `/api/users/me/sessions` :

```bash
curl -X GET http://localhost:8080/api/users/me/sessions \
  -H "Authorization: Bearer votre_token_jwt"
```

### Révocation d'une session

Pour révoquer une session spécifique, utilisez l'endpoint `/api/users/me/sessions/{sessionId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/users/me/sessions/abc123 \
  -H "Authorization: Bearer votre_token_jwt"
```

### Révocation de toutes les sessions

Pour révoquer toutes vos sessions (sauf la session courante), utilisez l'endpoint `/api/users/me/sessions` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/users/me/sessions \
  -H "Authorization: Bearer votre_token_jwt"
```

## Bonnes Pratiques

### Sécurité du Compte

- Utilisez un mot de passe fort et unique
- Changez régulièrement votre mot de passe
- Vérifiez régulièrement vos sessions actives et révoquez celles que vous ne reconnaissez pas

### Gestion des Informations Personnelles

- Utilisez une adresse email valide pour pouvoir récupérer votre compte si nécessaire
- Mettez à jour vos informations personnelles si elles changent

## Étapes Suivantes

Maintenant que vous savez comment gérer les comptes utilisateurs, vous pouvez passer à la [gestion des listes](/user-guide/list-management) pour apprendre à créer et gérer des listes de personnes.