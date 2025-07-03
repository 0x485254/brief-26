---
title: Utilisateurs
description: Documentation des endpoints de gestion des utilisateurs de l'API EasyGroup
---

# Endpoints de Gestion des Utilisateurs

Cette page documente les endpoints de gestion des utilisateurs disponibles dans l'API EasyGroup.

## Récupération du Profil de l'Utilisateur Courant

Permet à un utilisateur authentifié de récupérer son propre profil.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/users/me</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "username": "utilisateur",
  "email": "utilisateur@exemple.com",
  "firstName": "Prénom",
  "lastName": "Nom",
  "role": "USER",
  "isActivated": true,
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T12:34:56Z"
}
```

## Mise à Jour du Profil de l'Utilisateur Courant

Permet à un utilisateur authentifié de mettre à jour son propre profil.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/users/me</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "firstName": "Nouveau Prénom",
  "lastName": "Nouveau Nom",
  "email": "nouvel.email@exemple.com"
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "username": "utilisateur",
  "email": "nouvel.email@exemple.com",
  "firstName": "Nouveau Prénom",
  "lastName": "Nouveau Nom",
  "role": "USER",
  "isActivated": true,
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z"
}
```

## Modification du Mot de Passe

Permet à un utilisateur authentifié de modifier son mot de passe.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/users/me/password</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "currentPassword": "ancien_mot_de_passe",
  "newPassword": "nouveau_mot_de_passe"
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "message": "Mot de passe modifié avec succès"
}
```

## Suppression du Compte de l'Utilisateur Courant

Permet à un utilisateur authentifié de supprimer son propre compte.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/users/me</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 204 No Content

## Gestion des Préférences Utilisateur

### Récupération des Préférences

Permet à un utilisateur authentifié de récupérer ses préférences.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/users/me/preferences</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "theme": "dark",
  "language": "fr",
  "notifications": {
    "email": true,
    "push": false
  }
}
```

### Mise à Jour des Préférences

Permet à un utilisateur authentifié de mettre à jour ses préférences.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/users/me/preferences</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "theme": "light",
  "language": "en",
  "notifications": {
    "email": false,
    "push": true
  }
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "theme": "light",
  "language": "en",
  "notifications": {
    "email": false,
    "push": true
  }
}
```

## Gestion des Sessions

### Récupération des Sessions Actives

Permet à un utilisateur authentifié de récupérer la liste de ses sessions actives.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/users/me/sessions</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
[
  {
    "id": "abc123",
    "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
    "ipAddress": "192.168.1.1",
    "createdAt": "2023-07-03T12:34:56Z",
    "lastActivityAt": "2023-07-03T13:45:67Z",
    "isCurrent": true
  },
  {
    "id": "def456",
    "userAgent": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1",
    "ipAddress": "192.168.1.2",
    "createdAt": "2023-07-02T10:20:30Z",
    "lastActivityAt": "2023-07-03T09:10:11Z",
    "isCurrent": false
  }
]
```

### Révocation d'une Session Spécifique

Permet à un utilisateur authentifié de révoquer une session spécifique.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/users/me/sessions/{sessionId}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 204 No Content

### Révocation de Toutes les Sessions (Sauf la Courante)

Permet à un utilisateur authentifié de révoquer toutes ses sessions sauf la session courante.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/users/me/sessions</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 204 No Content

## Endpoints Administratifs

Les endpoints suivants sont réservés aux utilisateurs ayant le rôle ADMIN.

### Récupération de Tous les Utilisateurs

Permet à un administrateur de récupérer la liste de tous les utilisateurs.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/users</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Paramètres de Requête

- `page` : Numéro de la page (défaut: 0)
- `size` : Nombre d'éléments par page (défaut: 20)
- `sort` : Champ de tri (défaut: id,asc)
- `search` : Terme de recherche (optionnel)

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "content": [
    {
      "id": 1,
      "username": "utilisateur1",
      "email": "utilisateur1@exemple.com",
      "firstName": "Prénom1",
      "lastName": "Nom1",
      "role": "USER",
      "isActivated": true,
      "createdAt": "2023-07-03T12:34:56Z",
      "updatedAt": "2023-07-03T12:34:56Z"
    },
    {
      "id": 2,
      "username": "utilisateur2",
      "email": "utilisateur2@exemple.com",
      "firstName": "Prénom2",
      "lastName": "Nom2",
      "role": "ADMIN",
      "isActivated": true,
      "createdAt": "2023-07-02T10:20:30Z",
      "updatedAt": "2023-07-02T10:20:30Z"
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

### Récupération d'un Utilisateur par ID

Permet à un administrateur de récupérer un utilisateur spécifique par son ID.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/users/{id}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "username": "utilisateur",
  "email": "utilisateur@exemple.com",
  "firstName": "Prénom",
  "lastName": "Nom",
  "role": "USER",
  "isActivated": true,
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T12:34:56Z"
}
```

### Activation/Désactivation d'un Utilisateur

Permet à un administrateur d'activer ou de désactiver un compte utilisateur.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/users/{id}/activate</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "isActivated": true
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "username": "utilisateur",
  "email": "utilisateur@exemple.com",
  "firstName": "Prénom",
  "lastName": "Nom",
  "role": "USER",
  "isActivated": true,
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z"
}
```

### Modification du Rôle d'un Utilisateur

Permet à un administrateur de modifier le rôle d'un utilisateur.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/users/{id}/role</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "role": "ADMIN"
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "username": "utilisateur",
  "email": "utilisateur@exemple.com",
  "firstName": "Prénom",
  "lastName": "Nom",
  "role": "ADMIN",
  "isActivated": true,
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z"
}
```

### Suppression d'un Utilisateur

Permet à un administrateur de supprimer un compte utilisateur.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/users/{id}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 204 No Content

## Erreurs Courantes

### Utilisateur Non Trouvé

**Code** : 404 Not Found

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur non trouvé avec l'ID: 999",
  "path": "/api/users/999"
}
```

### Accès Non Autorisé

**Code** : 403 Forbidden

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Accès refusé à cette ressource",
  "path": "/api/users"
}
```

### Validation des Données

**Code** : 422 Unprocessable Entity

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Validation failed for object='userUpdateRequest'",
  "path": "/api/users/me",
  "errors": [
    {
      "field": "email",
      "message": "must be a valid email address"
    }
  ]
}
```
