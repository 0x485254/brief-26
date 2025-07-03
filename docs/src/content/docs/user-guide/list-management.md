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
  -b cookies.txt \
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
  -b cookies.txt
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
  -b cookies.txt
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
  "personCount": 10
}
```

## Modification d'une Liste

Pour modifier une liste existante, utilisez l'endpoint `/api/lists/{listId}` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/lists/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name": "Atelier du 15 juillet - Participants",
    "description": "Liste mise à jour des participants à l'atelier"
  }'
```

Vous recevrez une réponse contenant les détails mis à jour de la liste :

```json
{
  "id": 1,
  "name": "Atelier du 15 juillet - Participants",
  "description": "Liste mise à jour des participants à l'atelier",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T14:45:12Z",
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
  -b cookies.txt \
  -d '{
    "userId": 2
  }'
```

### Suppression d'un partage

Pour supprimer un partage de liste, utilisez l'endpoint `/api/lists/{listId}/share/{userId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/lists/1/share/2 \
  -b cookies.txt
```

## Suppression d'une Liste

Pour supprimer une liste, utilisez l'endpoint `/api/lists/{listId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/lists/1 \
  -b cookies.txt
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Gestion des Personnes dans une Liste

### Ajout d'une personne

Pour ajouter une personne à une liste, consultez le guide [Gestion des Personnes](/user-guide/person-management).

### Récupération des personnes d'une liste

Pour récupérer toutes les personnes d'une liste, utilisez l'endpoint `/api/lists/{listId}/persons` :

```bash
curl -X GET http://localhost:8080/api/lists/1/persons \
  -b cookies.txt
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
        "age": "35",
        "niveau": "débutant",
        "spécialité": "marketing"
      }
    },
    {
      "id": 2,
      "firstName": "Marie",
      "lastName": "Martin",
      "email": "marie.martin@exemple.com",
      "attributes": {
        "age": "28",
        "niveau": "intermédiaire",
        "spécialité": "design"
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

## Importation et Exportation

### Importation de personnes depuis un fichier CSV

Pour importer des personnes dans une liste à partir d'un fichier CSV, utilisez l'endpoint `/api/lists/{listId}/import` :

```bash
curl -X POST http://localhost:8080/api/lists/1/import \
  -H "Content-Type: multipart/form-data" \
  -b cookies.txt \
  -F "file=@personnes.csv"
```

Le format du fichier CSV doit être le suivant :

```
firstName,lastName,email,attribut1,attribut2
Jean,Dupont,jean.dupont@exemple.com,valeur1,valeur2
Marie,Martin,marie.martin@exemple.com,valeur3,valeur4
```

### Exportation de personnes vers un fichier CSV

Pour exporter les personnes d'une liste vers un fichier CSV, utilisez l'endpoint `/api/lists/{listId}/export` :

```bash
curl -X GET http://localhost:8080/api/lists/1/export \
  -b cookies.txt \
  -o personnes.csv
```

## Recherche et Filtrage de Listes

### Recherche par nom

Pour rechercher des listes par nom, utilisez l'endpoint `/api/lists/search` :

```bash
curl -X GET "http://localhost:8080/api/lists/search?name=atelier" \
  -b cookies.txt
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
  -b cookies.txt
```

### Filtrage par date de création

Pour filtrer les listes par date de création, utilisez les paramètres `createdAfter` et `createdBefore` :

```bash
curl -X GET "http://localhost:8080/api/lists?createdAfter=2023-07-01&createdBefore=2023-07-31" \
  -b cookies.txt
```

## Archivage de Listes

### Archivage d'une liste

Pour archiver une liste sans la supprimer, utilisez l'endpoint `/api/lists/{listId}/archive` :

```bash
curl -X POST http://localhost:8080/api/lists/1/archive \
  -b cookies.txt
```

### Désarchivage d'une liste

Pour désarchiver une liste, utilisez l'endpoint `/api/lists/{listId}/unarchive` :

```bash
curl -X POST http://localhost:8080/api/lists/1/unarchive \
  -b cookies.txt
```

### Récupération des listes archivées

Pour récupérer toutes vos listes archivées, utilisez le paramètre `archived` :

```bash
curl -X GET "http://localhost:8080/api/lists?archived=true" \
  -b cookies.txt
```

## Bonnes Pratiques

### Organisation des Listes

- Utilisez des noms descriptifs pour vos listes
- Ajoutez des descriptions détaillées pour faciliter l'identification
- Archivez les listes que vous n'utilisez plus au lieu de les supprimer
- Utilisez le partage de listes pour collaborer avec d'autres utilisateurs

### Gestion des Personnes

- Importez des personnes depuis un fichier CSV pour gagner du temps
- Utilisez des attributs personnalisés pour stocker des informations spécifiques
- Exportez régulièrement vos listes pour sauvegarder vos données