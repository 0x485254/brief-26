---
title: Listes
description: Documentation des endpoints de gestion des listes de l'API EasyGroup
---

# Endpoints de Gestion des Listes

Cette page documente les endpoints de gestion des listes disponibles dans l'API EasyGroup.

## Création d'une Liste

Permet de créer une nouvelle liste de personnes.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet"
}
```

### Réponse en Cas de Succès

**Code** : 201 Created

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

### Récupération de Toutes les Listes de l'Utilisateur

Permet de récupérer toutes les listes créées par l'utilisateur ou partagées avec lui.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Paramètres de Requête

- `page` : Numéro de la page (défaut: 0)
- `size` : Nombre d'éléments par page (défaut: 20)
- `sort` : Champ de tri (défaut: createdAt,desc)
- `search` : Terme de recherche (optionnel)
- `archived` : Filtre sur les listes archivées (true/false, optionnel)
- `ownerId` : Filtre sur le propriétaire (optionnel)
- `createdAfter` : Filtre sur la date de création (format ISO, optionnel)
- `createdBefore` : Filtre sur la date de création (format ISO, optionnel)

### Réponse en Cas de Succès

**Code** : 200 OK

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

### Récupération d'une Liste Spécifique

Permet de récupérer les détails d'une liste spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

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
    "username": "utilisateur",
    "email": "utilisateur@exemple.com"
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

Permet de modifier une liste existante.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/lists/{listId}</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "name": "Nouveau nom de la liste",
  "description": "Nouvelle description de la liste"
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

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

## Suppression d'une Liste

Permet de supprimer une liste existante.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/lists/{listId}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 204 No Content

## Partage de Listes

### Partage avec un Utilisateur

Permet de partager une liste avec un autre utilisateur.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/share</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "userId": 2
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z",
  "ownerId": 1,
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

### Partage avec Plusieurs Utilisateurs

Permet de partager une liste avec plusieurs utilisateurs en une seule requête.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/share-multiple</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "userIds": [2, 3, 4]
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z",
  "ownerId": 1,
  "personCount": 10,
  "sharedWith": [
    {
      "id": 2,
      "username": "utilisateur2",
      "email": "utilisateur2@exemple.com"
    },
    {
      "id": 3,
      "username": "utilisateur3",
      "email": "utilisateur3@exemple.com"
    },
    {
      "id": 4,
      "username": "utilisateur4",
      "email": "utilisateur4@exemple.com"
    }
  ]
}
```

### Révocation du Partage

Permet de révoquer le partage d'une liste avec un utilisateur.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/unshare</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "userId": 2
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z",
  "ownerId": 1,
  "personCount": 10,
  "sharedWith": []
}
```

## Archivage de Listes

### Archivage d'une Liste

Permet d'archiver une liste sans la supprimer.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/archive</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z",
  "ownerId": 1,
  "personCount": 10,
  "archived": true
}
```

### Désarchivage d'une Liste

Permet de désarchiver une liste précédemment archivée.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/unarchive</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Ma liste de participants",
  "description": "Liste des participants à l'atelier du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z",
  "ownerId": 1,
  "personCount": 10,
  "archived": false
}
```

## Importation et Exportation

### Importation depuis un Fichier CSV

Permet d'importer des personnes dans une liste à partir d'un fichier CSV.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/import</span>
</div>

### En-têtes de la Requête

```
Content-Type: multipart/form-data
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```
file=@personnes.csv
```

Le fichier CSV doit avoir le format suivant :

```csv
firstName,lastName,email,age,experience,skills
Jean,Dupont,jean.dupont@exemple.com,30,intermediate,"java,spring"
Marie,Martin,marie.martin@exemple.com,25,beginner,"javascript,react"
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "imported": 2,
  "errors": 0,
  "message": "2 personnes importées avec succès"
}
```

### Exportation vers un Fichier CSV

Permet d'exporter les personnes d'une liste au format CSV.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}/export</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

**Content-Type** : text/csv

**Content-Disposition** : attachment; filename="liste-1-personnes.csv"

```csv
firstName,lastName,email,age,experience,skills
Jean,Dupont,jean.dupont@exemple.com,30,intermediate,"java,spring"
Marie,Martin,marie.martin@exemple.com,25,beginner,"javascript,react"
```

## Recherche et Filtrage

### Recherche de Listes par Nom

Permet de rechercher des listes par nom.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/search</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Paramètres de Requête

- `name` : Terme de recherche (obligatoire)
- `page` : Numéro de la page (défaut: 0)
- `size` : Nombre d'éléments par page (défaut: 20)

### Réponse en Cas de Succès

**Code** : 200 OK

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

## Statistiques de Liste

### Récupération des Statistiques d'une Liste

Permet de récupérer des statistiques sur une liste (nombre de personnes, distribution des attributs, etc.).

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}/stats</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "personCount": 10,
  "attributeDistribution": {
    "age": {
      "min": 22,
      "max": 45,
      "average": 30.5
    },
    "experience": {
      "beginner": 3,
      "intermediate": 5,
      "expert": 2
    },
    "skills": {
      "java": 4,
      "spring": 3,
      "javascript": 5,
      "react": 4,
      "python": 2,
      "django": 1
    }
  },
  "drawCount": 3,
  "lastDrawAt": "2023-07-03T13:45:67Z"
}
```

## Erreurs Courantes

### Liste Non Trouvée

**Code** : 404 Not Found

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Liste non trouvée avec l'ID: 999",
  "path": "/api/lists/999"
}
```

### Accès Non Autorisé

**Code** : 403 Forbidden

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Vous n'avez pas les permissions nécessaires pour accéder à cette liste",
  "path": "/api/lists/1"
}
```

### Validation des Données

**Code** : 422 Unprocessable Entity

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Validation failed for object='listCreateRequest'",
  "path": "/api/lists",
  "errors": [
    {
      "field": "name",
      "message": "must not be blank"
    }
  ]
}
```

### Erreur d'Importation CSV

**Code** : 400 Bad Request

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Erreur lors de l'importation du fichier CSV: format incorrect à la ligne 3",
  "path": "/api/lists/1/import"
}
```
