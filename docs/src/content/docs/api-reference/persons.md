---
title: Personnes
description: Documentation des endpoints de gestion des personnes de l'API EasyGroup
---

# Endpoints de Gestion des Personnes

Cette page documente les endpoints de gestion des personnes disponibles dans l'API EasyGroup.

## Ajout d'une Personne à une Liste

Permet d'ajouter une nouvelle personne à une liste existante.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/persons</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "firstName": "Jean",
  "lastName": "Dupont",
  "email": "jean.dupont@exemple.com",
  "attributes": {
    "age": 30,
    "experience": "intermediate",
    "skills": ["java", "spring"]
  }
}
```

### Réponse en Cas de Succès

**Code** : 201 Created

```json
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
  "updatedAt": "2023-07-03T12:34:56Z",
  "listId": 1
}
```

## Récupération des Personnes

### Récupération de Toutes les Personnes d'une Liste

Permet de récupérer toutes les personnes d'une liste spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}/persons</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Paramètres de Requête

- `page` : Numéro de la page (défaut: 0)
- `size` : Nombre d'éléments par page (défaut: 20)
- `sort` : Champ de tri (défaut: id,asc)
- `search` : Terme de recherche (optionnel)
- `attribute.{nom}` : Filtre sur un attribut spécifique (optionnel)
- `attribute.{nom}.gte` : Filtre sur un attribut numérique (supérieur ou égal à, optionnel)
- `attribute.{nom}.lte` : Filtre sur un attribut numérique (inférieur ou égal à, optionnel)

### Réponse en Cas de Succès

**Code** : 200 OK

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

### Récupération d'une Personne Spécifique

Permet de récupérer les détails d'une personne spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/persons/{personId}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 200 OK

```json
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
  "updatedAt": "2023-07-03T12:34:56Z",
  "listId": 1
}
```

## Modification d'une Personne

Permet de modifier les informations d'une personne existante.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/persons/{personId}</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "firstName": "Jean-Pierre",
  "lastName": "Dupont",
  "email": "jean-pierre.dupont@exemple.com",
  "attributes": {
    "age": 31,
    "experience": "expert",
    "skills": ["java", "spring", "kubernetes"]
  }
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "firstName": "Jean-Pierre",
  "lastName": "Dupont",
  "email": "jean-pierre.dupont@exemple.com",
  "attributes": {
    "age": 31,
    "experience": "expert",
    "skills": ["java", "spring", "kubernetes"]
  },
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T13:45:67Z",
  "listId": 1
}
```

## Suppression d'une Personne

Permet de supprimer une personne d'une liste.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/persons/{personId}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 204 No Content

## Gestion des Attributs

### Ajout ou Modification d'Attributs

Permet d'ajouter ou de modifier des attributs d'une personne.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/persons/{personId}/attributes</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Corps de la Requête

```json
{
  "age": 32,
  "experience": "expert",
  "skills": ["java", "spring", "kubernetes", "docker"],
  "languages": ["french", "english"],
  "role": "developer"
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "age": 32,
  "experience": "expert",
  "skills": ["java", "spring", "kubernetes", "docker"],
  "languages": ["french", "english"],
  "role": "developer"
}
```

### Suppression d'un Attribut

Permet de supprimer un attribut spécifique d'une personne.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/persons/{personId}/attributes/{attributeName}</span>
</div>

### En-têtes de la Requête

Note: L'authentification se fait via un cookie HTTP-only qui est automatiquement inclus dans la requête.

### Réponse en Cas de Succès

**Code** : 204 No Content

## Recherche et Filtrage

### Recherche de Personnes par Nom

Permet de rechercher des personnes par nom dans une liste.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}/persons/search</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

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
    "firstName": "Jean-Pierre",
    "lastName": "Dupont",
    "email": "jean-pierre.dupont@exemple.com",
    "attributes": {
      "age": 32,
      "experience": "expert",
      "skills": ["java", "spring", "kubernetes", "docker"]
    },
    "createdAt": "2023-07-03T12:34:56Z",
    "updatedAt": "2023-07-03T13:45:67Z"
  }
]
```

### Filtrage par Attribut

Permet de filtrer les personnes par attribut.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}/persons</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Paramètres de Requête

- `attribute.experience` : Filtre sur l'attribut "experience"
- `attribute.age.gte` : Filtre sur l'attribut "age" (supérieur ou égal à)
- `page` : Numéro de la page (défaut: 0)
- `size` : Nombre d'éléments par page (défaut: 20)

### Exemple de Requête

```
GET /api/lists/1/persons?attribute.experience=expert&attribute.age.gte=30
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Jean-Pierre",
      "lastName": "Dupont",
      "email": "jean-pierre.dupont@exemple.com",
      "attributes": {
        "age": 32,
        "experience": "expert",
        "skills": ["java", "spring", "kubernetes", "docker"]
      },
      "createdAt": "2023-07-03T12:34:56Z",
      "updatedAt": "2023-07-03T13:45:67Z"
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
  "totalElements": 1,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

## Opérations en Masse

### Ajout de Plusieurs Personnes

Permet d'ajouter plusieurs personnes à une liste en une seule requête.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/persons/batch</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Corps de la Requête

```json
[
  {
    "firstName": "Pierre",
    "lastName": "Martin",
    "email": "pierre.martin@exemple.com",
    "attributes": {
      "age": 28,
      "experience": "beginner",
      "skills": ["javascript", "react"]
    }
  },
  {
    "firstName": "Sophie",
    "lastName": "Dubois",
    "email": "sophie.dubois@exemple.com",
    "attributes": {
      "age": 35,
      "experience": "intermediate",
      "skills": ["python", "django"]
    }
  }
]
```

### Réponse en Cas de Succès

**Code** : 201 Created

```json
[
  {
    "id": 3,
    "firstName": "Pierre",
    "lastName": "Martin",
    "email": "pierre.martin@exemple.com",
    "attributes": {
      "age": 28,
      "experience": "beginner",
      "skills": ["javascript", "react"]
    },
    "createdAt": "2023-07-03T14:56:78Z",
    "updatedAt": "2023-07-03T14:56:78Z",
    "listId": 1
  },
  {
    "id": 4,
    "firstName": "Sophie",
    "lastName": "Dubois",
    "email": "sophie.dubois@exemple.com",
    "attributes": {
      "age": 35,
      "experience": "intermediate",
      "skills": ["python", "django"]
    },
    "createdAt": "2023-07-03T14:56:78Z",
    "updatedAt": "2023-07-03T14:56:78Z",
    "listId": 1
  }
]
```

### Suppression de Plusieurs Personnes

Permet de supprimer plusieurs personnes en une seule requête.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/persons/batch</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "personIds": [3, 4]
}
```

### Réponse en Cas de Succès

**Code** : 204 No Content

## Déplacement de Personnes

### Déplacement d'une Personne vers une Autre Liste

Permet de déplacer une personne d'une liste à une autre.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/persons/{personId}/move</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "targetListId": 2
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "firstName": "Jean-Pierre",
  "lastName": "Dupont",
  "email": "jean-pierre.dupont@exemple.com",
  "attributes": {
    "age": 32,
    "experience": "expert",
    "skills": ["java", "spring", "kubernetes", "docker"]
  },
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T15:67:89Z",
  "listId": 2
}
```

### Déplacement de Plusieurs Personnes vers une Autre Liste

Permet de déplacer plusieurs personnes d'une liste à une autre en une seule requête.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/persons/batch/move</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "personIds": [1, 2],
  "targetListId": 2
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "movedCount": 2,
  "message": "2 personnes déplacées avec succès"
}
```

## Erreurs Courantes

### Personne Non Trouvée

**Code** : 404 Not Found

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Personne non trouvée avec l'ID: 999",
  "path": "/api/persons/999"
}
```

### Liste Non Trouvée

**Code** : 404 Not Found

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Liste non trouvée avec l'ID: 999",
  "path": "/api/lists/999/persons"
}
```

### Accès Non Autorisé

**Code** : 403 Forbidden

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Vous n'avez pas les permissions nécessaires pour accéder à cette personne",
  "path": "/api/persons/1"
}
```

### Validation des Données

**Code** : 422 Unprocessable Entity

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Validation failed for object='personCreateRequest'",
  "path": "/api/lists/1/persons",
  "errors": [
    {
      "field": "email",
      "message": "must be a valid email address"
    },
    {
      "field": "firstName",
      "message": "must not be blank"
    }
  ]
}
```

### Conflit d'Email

**Code** : 409 Conflict

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 409,
  "error": "Conflict",
  "message": "Une personne avec l'email 'jean.dupont@exemple.com' existe déjà dans cette liste",
  "path": "/api/lists/1/persons"
}
```
