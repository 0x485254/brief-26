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
  -b cookies.txt \
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
  -b cookies.txt
```

Vous recevrez une réponse paginée contenant les personnes de la liste (voir l'exemple dans le [guide de gestion des listes](/user-guide/list-management#récupération-des-personnes-dune-liste)).

### Récupération d'une personne spécifique

Pour récupérer les détails d'une personne spécifique, utilisez l'endpoint `/api/persons/{personId}` :

```bash
curl -X GET http://localhost:8080/api/persons/1 \
  -b cookies.txt
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
  -b cookies.txt \
  -d '{
    "firstName": "Jean-Pierre",
    "lastName": "Dupont",
    "email": "jean-pierre.dupont@exemple.com",
    "attributes": {
      "age": 32,
      "experience": "senior",
      "skills": ["java", "spring", "kubernetes"]
    }
  }'
```

Vous recevrez une réponse contenant les détails mis à jour de la personne :

```json
{
  "id": 1,
  "firstName": "Jean-Pierre",
  "lastName": "Dupont",
  "email": "jean-pierre.dupont@exemple.com",
  "attributes": {
    "age": 32,
    "experience": "senior",
    "skills": ["java", "spring", "kubernetes"]
  },
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T14:45:12Z",
  "listId": 1
}
```

## Suppression d'une Personne

Pour supprimer une personne, utilisez l'endpoint `/api/persons/{personId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/persons/1 \
  -b cookies.txt
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Gestion des Attributs d'une Personne

Les attributs sont des informations supplémentaires associées à une personne, qui peuvent être utilisées pour équilibrer les groupes. Ils sont stockés sous forme de paires clé-valeur.

### Ajout ou Modification d'un Attribut

Pour ajouter ou modifier un attribut d'une personne, utilisez l'endpoint `/api/persons/{personId}/attributes` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/persons/1/attributes \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "department": "IT",
    "yearsOfExperience": 5
  }'
```

Vous recevrez une réponse contenant les attributs mis à jour de la personne :

```json
{
  "age": 32,
  "experience": "senior",
  "skills": ["java", "spring", "kubernetes"],
  "department": "IT",
  "yearsOfExperience": 5
}
```

### Suppression d'un Attribut

Pour supprimer un attribut d'une personne, utilisez l'endpoint `/api/persons/{personId}/attributes/{attributeName}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/persons/1/attributes/yearsOfExperience \
  -b cookies.txt
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Recherche et Filtrage de Personnes

### Recherche par Nom

Pour rechercher des personnes par nom dans une liste, utilisez l'endpoint `/api/lists/{listId}/persons/search` :

```bash
curl -X GET "http://localhost:8080/api/lists/1/persons/search?name=dupont" \
  -b cookies.txt
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
      "experience": "senior",
      "skills": ["java", "spring", "kubernetes"],
      "department": "IT"
    }
  }
]
```

### Filtrage par Attribut

Pour filtrer les personnes par attribut dans une liste, utilisez les paramètres de requête sur l'endpoint `/api/lists/{listId}/persons` :

```bash
curl -X GET "http://localhost:8080/api/lists/1/persons?attribute.experience=senior&attribute.department=IT" \
  -b cookies.txt
```

Vous recevrez une réponse paginée contenant les personnes correspondantes.

## Importation et Exportation de Personnes

### Importation depuis un Fichier CSV

Pour importer des personnes dans une liste à partir d'un fichier CSV, utilisez l'endpoint `/api/lists/{listId}/import` (voir le [guide de gestion des listes](/user-guide/list-management#importation-de-personnes-depuis-un-fichier-csv)).

### Exportation vers un Fichier CSV

Pour exporter les personnes d'une liste vers un fichier CSV, utilisez l'endpoint `/api/lists/{listId}/export` (voir le [guide de gestion des listes](/user-guide/list-management#exportation-de-personnes-vers-un-fichier-csv)).

## Gestion des Attributs Personnalisés

### Définition des Attributs d'une Liste

Pour définir les attributs disponibles pour les personnes d'une liste, utilisez l'endpoint `/api/lists/{listId}/attributes` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/lists/1/attributes \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "attributes": [
      {
        "name": "age",
        "type": "number",
        "required": true
      },
      {
        "name": "experience",
        "type": "string",
        "enum": ["beginner", "intermediate", "senior"],
        "required": true
      },
      {
        "name": "skills",
        "type": "array",
        "required": false
      },
      {
        "name": "department",
        "type": "string",
        "required": false
      }
    ]
  }'
```

Vous recevrez une réponse contenant les attributs définis pour la liste :

```json
{
  "attributes": [
    {
      "name": "age",
      "type": "number",
      "required": true
    },
    {
      "name": "experience",
      "type": "string",
      "enum": ["beginner", "intermediate", "senior"],
      "required": true
    },
    {
      "name": "skills",
      "type": "array",
      "required": false
    },
    {
      "name": "department",
      "type": "string",
      "required": false
    }
  ]
}
```

### Récupération des Attributs d'une Liste

Pour récupérer les attributs définis pour une liste, utilisez l'endpoint `/api/lists/{listId}/attributes` :

```bash
curl -X GET http://localhost:8080/api/lists/1/attributes \
  -b cookies.txt
```

Vous recevrez une réponse contenant les attributs définis pour la liste.

## Bonnes Pratiques

### Organisation des Personnes

- Utilisez des noms complets pour faciliter l'identification
- Ajoutez des adresses email pour permettre la communication
- Utilisez des attributs cohérents pour toutes les personnes d'une liste

### Gestion des Attributs

- Définissez clairement les attributs importants pour votre contexte
- Utilisez des types d'attributs appropriés (nombre, texte, liste, etc.)
- Assurez-vous que les attributs utilisés pour l'équilibrage sont renseignés pour toutes les personnes

### Importation et Exportation

- Préparez soigneusement vos fichiers CSV pour l'importation
- Vérifiez que les en-têtes de colonnes correspondent aux noms d'attributs
- Exportez régulièrement vos listes pour sauvegarder vos données