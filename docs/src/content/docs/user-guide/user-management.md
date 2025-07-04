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
  -b cookies.txt
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
  -b cookies.txt
```

## Modification des Informations d'un Utilisateur

Pour modifier les informations de votre compte utilisateur, utilisez l'endpoint `/api/users/me` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Content-Type: application/json" \
  -b cookies.txt \
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
  -b cookies.txt \
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
  -b cookies.txt
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Gestion des Préférences Utilisateur

### Récupération des préférences

Pour récupérer vos préférences utilisateur, utilisez l'endpoint `/api/users/me/preferences` :

```bash
curl -X GET http://localhost:8080/api/users/me/preferences \
  -b cookies.txt
```

Vous recevrez une réponse similaire à :

```json
{
  "theme": "dark",
  "language": "fr",
  "notifications": {
    "email": true,
    "push": false
  },
  "defaultListView": "grid"
}
```

### Modification des préférences

Pour modifier vos préférences utilisateur, utilisez l'endpoint `/api/users/me/preferences` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/users/me/preferences \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "theme": "light",
    "language": "en",
    "notifications": {
      "email": true,
      "push": true
    },
    "defaultListView": "list"
  }'
```

Vous recevrez une réponse contenant les préférences mises à jour.

## Gestion des Notifications

### Récupération des notifications

Pour récupérer vos notifications, utilisez l'endpoint `/api/users/me/notifications` :

```bash
curl -X GET http://localhost:8080/api/users/me/notifications \
  -b cookies.txt
```

Vous recevrez une réponse contenant la liste de vos notifications :

```json
{
  "content": [
    {
      "id": 1,
      "type": "LIST_SHARED",
      "message": "Jean Dupont a partagé une liste avec vous",
      "createdAt": "2023-07-03T12:34:56Z",
      "read": false,
      "data": {
        "listId": 5,
        "listName": "Équipe Projet X",
        "sharedBy": "Jean Dupont"
      }
    },
    {
      "id": 2,
      "type": "DRAW_COMPLETED",
      "message": "Le tirage 'Tirage du 15 juillet' est terminé",
      "createdAt": "2023-07-03T10:20:30Z",
      "read": true,
      "data": {
        "drawId": 3,
        "drawName": "Tirage du 15 juillet",
        "listId": 2
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```

### Marquer une notification comme lue

Pour marquer une notification comme lue, utilisez l'endpoint `/api/users/me/notifications/{notificationId}/read` :

```bash
curl -X PUT http://localhost:8080/api/users/me/notifications/1/read \
  -b cookies.txt
```

### Marquer toutes les notifications comme lues

Pour marquer toutes vos notifications comme lues, utilisez l'endpoint `/api/users/me/notifications/read-all` :

```bash
curl -X PUT http://localhost:8080/api/users/me/notifications/read-all \
  -b cookies.txt
```

## Bonnes Pratiques

### Sécurité du Compte

- Utilisez un mot de passe fort et unique
- Changez régulièrement votre mot de passe
- Ne partagez jamais vos identifiants de connexion

### Gestion des Données Personnelles

- Gardez vos informations de contact à jour
- Utilisez une adresse email valide pour recevoir les notifications importantes
- Supprimez votre compte uniquement si vous êtes sûr de ne plus vouloir utiliser le service