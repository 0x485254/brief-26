---
title: Gestion des Listes
description: Guide de gestion des listes de personnes dans l'API EasyGroup
---

# Gestion des Listes

Ce guide explique comment créer et gérer des listes de personnes dans l'API EasyGroup, y compris la création, la modification, le partage et la suppression de listes.

## Vue d'ensemble

Les listes sont un concept central dans EasyGroup. Elles permettent de regrouper des personnes pour ensuite générer des groupes équilibrés. L'API EasyGroup propose plusieurs endpoints pour gérer les listes :

- Création d'une liste
- Récupération des listes
- Modification d'une liste
- Partage d'une liste
- Suppression d'une liste

## Création d'une Liste

Pour créer une nouvelle liste, envoyez une requête POST à l'endpoint `/api/lists` :

```bash
curl -X POST http://localhost:8080/api/lists \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Ma liste de participants",
    "description": "Liste des participants à l'atelier du 15 juillet"
  }'
```

Vous recevrez une réponse similaire à :

```json
{
  "id": 1,
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T12:34:56Z",
  "ownerId": 1,
  "personCount": 0
}
```

## Récupération des Listes

### Récupération de toutes vos listes

Pour récupérer toutes les listes que vous avez créées ou qui ont été partagées avec vous, utilisez l'endpoint `/api/lists` :

```bash
curl -X GET http://localhost:8080/api/lists \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant la liste de vos listes :

```json
{
  "content": [
    {
      "id": 1,
      "name": "Ma liste de participants",
      "description": "Liste des participants à l'atelier du 15 juillet",
      "createdAt": "2023-07-03T12:34:56Z",
      "updatedAt": "2023-07-03T12:34:56Z",
      "ownerId": 1,
      "personCount": 10
    },
    {
      "id": 2,
      "name": "Équipe projet X",
      "description": "Membres de l'équipe du projet X",
      "createdAt": "2023-07-02T10:20:30Z",
      "updatedAt": "2023-07-02T10:20:30Z",
      "ownerId": 1,
      "personCount": 5
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

### Récupération d'une liste spécifique

Pour récupérer les détails d'une liste spécifique, utilisez l'endpoint `/api/lists/{listId}` :

```bash
curl -X GET http://localhost:8080/api/lists/1 \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant les détails de la liste :

```json
{
  "id": 1,
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T12:34:56Z",
  "ownerId": 1,
  "owner": {
    "id": 1,
    "username": "votre_nom_utilisateur",
    "email": "votre_email@exemple.com"
  },
  "personCount": 10,
  "sharedWith": [
    {
      "id": 2,
      "username": "autre_utilisateur",
      "email": "autre_utilisateur@exemple.com"
    }
  ]
}
```

## Modification d'une Liste

Pour modifier une liste existante, utilisez l'endpoint `/api/lists/{listId}` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/lists/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Nouveau nom de la liste",
    "description": "Nouvelle description de la liste"
  }'
```

Vous recevrez une réponse contenant les informations mises à jour :

```json
{
  "id": 1,
  "name": "Nouveau nom de la liste",
  "description": "Nouvelle description de la liste",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z",
  "ownerId": 1,
  "personCount": 10
}
```

## Partage d'une Liste

### Partage avec un utilisateur

Pour partager une liste avec un autre utilisateur, utilisez l'endpoint `/api/lists/{listId}/share` :

```bash
curl -X POST http://localhost:8080/api/lists/1/share \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "userId": 2
  }'
```

En cas de succès, vous recevrez une réponse avec le code 200 OK.

### Partage avec plusieurs utilisateurs

Pour partager une liste avec plusieurs utilisateurs en une seule requête :

```bash
curl -X POST http://localhost:8080/api/lists/1/share-multiple \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "userIds": [2, 3, 4]
  }'
```

### Révocation du partage

Pour révoquer le partage d'une liste avec un utilisateur, utilisez l'endpoint `/api/lists/{listId}/unshare` :

```bash
curl -X POST http://localhost:8080/api/lists/1/unshare \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "userId": 2
  }'
```

## Suppression d'une Liste

Pour supprimer une liste, utilisez l'endpoint `/api/lists/{listId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/lists/1 \
  -H "Authorization: Bearer votre_token_jwt"
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Gestion des Personnes dans une Liste

### Ajout d'une personne

Pour ajouter une personne à une liste, consultez le guide [Gestion des Personnes](/user-guide/person-management).

### Récupération des personnes d'une liste

Pour récupérer toutes les personnes d'une liste, utilisez l'endpoint `/api/lists/{listId}/persons` :

```bash
curl -X GET http://localhost:8080/api/lists/1/persons \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant la liste des personnes :

```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Jean",
      "lastName": "Dupont",
      "email": "jean.dupont@exemple.com",
      "attributes": {
        "age": 30,
        "experience": "intermediate",
        "skills": ["java", "spring"]
      },
      "createdAt": "2023-07-03T12:34:56Z",
      "updatedAt": "2023-07-03T12:34:56Z"
    },
    {
      "id": 2,
      "firstName": "Marie",
      "lastName": "Martin",
      "email": "marie.martin@exemple.com",
      "attributes": {
        "age": 25,
        "experience": "beginner",
        "skills": ["javascript", "react"]
      },
      "createdAt": "2023-07-03T12:34:56Z",
      "updatedAt": "2023-07-03T12:34:56Z"
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

## Importation et Exportation de Listes

### Importation depuis un fichier CSV

Pour importer des personnes dans une liste à partir d'un fichier CSV, utilisez l'endpoint `/api/lists/{listId}/import` :

```bash
curl -X POST http://localhost:8080/api/lists/1/import \
  -H "Authorization: Bearer votre_token_jwt" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@personnes.csv"
```

Le fichier CSV doit avoir le format suivant :

```csv
firstName,lastName,email,age,experience,skills
Jean,Dupont,jean.dupont@exemple.com,30,intermediate,"java,spring"
Marie,Martin,marie.martin@exemple.com,25,beginner,"javascript,react"
```

### Exportation vers un fichier CSV

Pour exporter une liste au format CSV, utilisez l'endpoint `/api/lists/{listId}/export` :

```bash
curl -X GET http://localhost:8080/api/lists/1/export \
  -H "Authorization: Bearer votre_token_jwt" \
  -o personnes.csv
```

## Recherche et Filtrage de Listes

### Recherche par nom

Pour rechercher des listes par nom, utilisez l'endpoint `/api/lists/search` :

```bash
curl -X GET "http://localhost:8080/api/lists/search?name=atelier" \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant les listes correspondantes :

```json
[
  {
    "id": 1,
    "name": "Ma liste de participants",
    "description": "Liste des participants à l'atelier du 15 juillet",
    "createdAt": "2023-07-03T12:34:56Z",
    "updatedAt": "2023-07-03T12:34:56Z",
    "ownerId": 1,
    "personCount": 10
  }
]
```

### Filtrage par propriétaire

Pour filtrer les listes par propriétaire, utilisez le paramètre `ownerId` :

```bash
curl -X GET "http://localhost:8080/api/lists?ownerId=1" \
  -H "Authorization: Bearer votre_token_jwt"
```

### Filtrage par date de création

Pour filtrer les listes par date de création, utilisez les paramètres `createdAfter` et `createdBefore` :

```bash
curl -X GET "http://localhost:8080/api/lists?createdAfter=2023-07-01&createdBefore=2023-07-31" \
  -H "Authorization: Bearer votre_token_jwt"
```

## Archivage de Listes

### Archivage d'une liste

Pour archiver une liste sans la supprimer, utilisez l'endpoint `/api/lists/{listId}/archive` :

```bash
curl -X POST http://localhost:8080/api/lists/1/archive \
  -H "Authorization: Bearer votre_token_jwt"
```

### Désarchivage d'une liste

Pour désarchiver une liste, utilisez l'endpoint `/api/lists/{listId}/unarchive` :

```bash
curl -X POST http://localhost:8080/api/lists/1/unarchive \
  -H "Authorization: Bearer votre_token_jwt"
```

### Récupération des listes archivées

Pour récupérer toutes vos listes archivées, utilisez le paramètre `archived` :

```bash
curl -X GET "http://localhost:8080/api/lists?archived=true" \
  -H "Authorization: Bearer votre_token_jwt"
```

## Bonnes Pratiques

### Organisation des Listes

- Utilisez des noms descriptifs pour vos listes
- Ajoutez des descriptions détaillées pour faciliter l'identification
- Archivez les listes que vous n'utilisez plus au lieu de les supprimer
- Utilisez le partage de listes pour collaborer avec d'autres utilisateurs

### Importation de Données

- Vérifiez le format de votre fichier CSV avant l'importation
- Assurez-vous que les en-têtes correspondent aux attributs attendus
- Validez les données importées après l'importation

### Sécurité

- Ne partagez vos listes qu'avec des utilisateurs de confiance
- Vérifiez régulièrement les utilisateurs avec qui vos listes sont partagées
- Révoquez les partages lorsqu'ils ne sont plus nécessaires

## Étapes Suivantes

Maintenant que vous savez comment gérer les listes, vous pouvez passer à la [gestion des personnes](/user-guide/person-management) pour apprendre à ajouter et gérer des personnes dans vos listes.
