---
title: Gestion des Personnes
description: Guide de gestion des personnes dans l'API EasyGroup
---

# Gestion des Personnes

Ce guide explique comment ajouter et gérer des personnes dans vos listes avec l'API EasyGroup, y compris la création, la modification et la suppression de personnes, ainsi que la gestion de leurs attributs.

## Vue d'ensemble

Les personnes sont les éléments de base des listes dans EasyGroup. Chaque personne possède des attributs qui peuvent être utilisés pour équilibrer les groupes. L'API EasyGroup propose plusieurs endpoints pour gérer les personnes :

- Ajout d'une personne à une liste
- Récupération des personnes d'une liste
- Modification d'une personne
- Suppression d'une personne
- Gestion des attributs d'une personne

## Ajout d'une Personne à une Liste

Pour ajouter une personne à une liste, envoyez une requête POST à l'endpoint `/api/lists/{listId}/persons` :

```bash
curl -X POST http://localhost:8080/api/lists/1/persons \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "firstName": "Jean",
    "lastName": "Dupont",
    "email": "jean.dupont@exemple.com",
    "attributes": {
      "age": 30,
      "experience": "intermediate",
      "skills": ["java", "spring"]
    }
  }'
```

Vous recevrez une réponse similaire à :

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
  "updatedAt": "2023-07-03T12:34:56Z"
}
```

## Récupération des Personnes d'une Liste

### Récupération de toutes les personnes

Pour récupérer toutes les personnes d'une liste, utilisez l'endpoint `/api/lists/{listId}/persons` :

```bash
curl -X GET http://localhost:8080/api/lists/1/persons \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse paginée contenant les personnes de la liste (voir l'exemple dans le [guide de gestion des listes](/user-guide/list-management#récupération-des-personnes-dune-liste)).

### Récupération d'une personne spécifique

Pour récupérer les détails d'une personne spécifique, utilisez l'endpoint `/api/persons/{personId}` :

```bash
curl -X GET http://localhost:8080/api/persons/1 \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant les détails de la personne :

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

Pour modifier une personne existante, utilisez l'endpoint `/api/persons/{personId}` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/persons/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "firstName": "Jean-Pierre",
    "lastName": "Dupont",
    "email": "jean-pierre.dupont@exemple.com",
    "attributes": {
      "age": 31,
      "experience": "expert",
      "skills": ["java", "spring", "kubernetes"]
    }
  }'
```

Vous recevrez une réponse contenant les informations mises à jour :

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

Pour supprimer une personne, utilisez l'endpoint `/api/persons/{personId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/persons/1 \
  -H "Authorization: Bearer votre_token_jwt"
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Gestion des Attributs d'une Personne

Les attributs sont des caractéristiques associées à une personne. Ils peuvent être de différents types :

- **Numériques** : Âge, années d'expérience, etc.
- **Catégoriels** : Niveau d'expérience (débutant, intermédiaire, expert), rôle, etc.
- **Listes** : Compétences, langues parlées, etc.

### Ajout ou Modification d'Attributs

Pour ajouter ou modifier des attributs d'une personne, utilisez l'endpoint `/api/persons/{personId}/attributes` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/persons/1/attributes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "age": 32,
    "experience": "expert",
    "skills": ["java", "spring", "kubernetes", "docker"],
    "languages": ["french", "english"],
    "role": "developer"
  }'
```

Vous recevrez une réponse contenant les attributs mis à jour :

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

Pour supprimer un attribut spécifique d'une personne, utilisez l'endpoint `/api/persons/{personId}/attributes/{attributeName}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/persons/1/attributes/role \
  -H "Authorization: Bearer votre_token_jwt"
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Importation et Exportation de Personnes

### Importation depuis un fichier CSV

Pour importer des personnes dans une liste à partir d'un fichier CSV, consultez la section [Importation depuis un fichier CSV](/user-guide/list-management#importation-depuis-un-fichier-csv) du guide de gestion des listes.

### Exportation vers un fichier CSV

Pour exporter les personnes d'une liste au format CSV, consultez la section [Exportation vers un fichier CSV](/user-guide/list-management#exportation-vers-un-fichier-csv) du guide de gestion des listes.

## Recherche et Filtrage de Personnes

### Recherche par nom

Pour rechercher des personnes par nom dans une liste, utilisez l'endpoint `/api/lists/{listId}/persons/search` :

```bash
curl -X GET "http://localhost:8080/api/lists/1/persons/search?name=jean" \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant les personnes correspondantes :

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
      "skills": ["java", "spring", "kubernetes", "docker"],
      "languages": ["french", "english"]
    },
    "createdAt": "2023-07-03T12:34:56Z",
    "updatedAt": "2023-07-03T13:45:67Z"
  }
]
```

### Filtrage par attribut

Pour filtrer les personnes par attribut, utilisez les paramètres de requête correspondants :

```bash
curl -X GET "http://localhost:8080/api/lists/1/persons?attribute.experience=expert&attribute.age.gte=30" \
  -H "Authorization: Bearer votre_token_jwt"
```

Cette requête retourne toutes les personnes de la liste 1 qui ont un niveau d'expérience "expert" et un âge supérieur ou égal à 30 ans.

## Gestion en Masse des Personnes

### Ajout de plusieurs personnes

Pour ajouter plusieurs personnes à une liste en une seule requête, utilisez l'endpoint `/api/lists/{listId}/persons/batch` :

```bash
curl -X POST http://localhost:8080/api/lists/1/persons/batch \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '[
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
  ]'
```

### Suppression de plusieurs personnes

Pour supprimer plusieurs personnes en une seule requête, utilisez l'endpoint `/api/persons/batch` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/persons/batch \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "personIds": [2, 3]
  }'
```

## Bonnes Pratiques

### Structure des Attributs

- Utilisez des noms d'attributs cohérents pour toutes les personnes d'une même liste
- Utilisez des types de données appropriés pour chaque attribut (nombres pour les valeurs numériques, chaînes pour les valeurs catégorielles, tableaux pour les listes)
- Évitez les attributs trop spécifiques qui ne s'appliquent qu'à quelques personnes

### Gestion des Données Personnelles

- Respectez les réglementations sur la protection des données (RGPD, etc.)
- N'incluez pas d'informations sensibles dans les attributs
- Informez les personnes de la collecte et de l'utilisation de leurs données

### Optimisation des Performances

- Limitez le nombre d'attributs par personne pour éviter de surcharger le système
- Utilisez l'importation en masse pour ajouter de nombreuses personnes
- Utilisez les filtres pour récupérer uniquement les personnes nécessaires

## Étapes Suivantes

Maintenant que vous savez comment gérer les personnes dans vos listes, vous pouvez passer à la [création de groupes](/user-guide/group-creation) pour apprendre à générer des groupes équilibrés à partir de vos listes.