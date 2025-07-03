---
title: Création de Groupes
description: Guide de création et gestion des groupes dans l'API EasyGroup
---

# Création de Groupes

Ce guide explique comment générer et gérer des groupes équilibrés à partir de vos listes de personnes avec l'API EasyGroup.

## Vue d'ensemble

La création de groupes équilibrés est la fonctionnalité principale d'EasyGroup. L'API permet de générer des groupes en tenant compte des attributs des personnes pour assurer un équilibre optimal. L'API EasyGroup propose plusieurs endpoints pour gérer les groupes :

- Création d'un tirage (génération de groupes)
- Récupération des tirages et des groupes
- Modification des paramètres d'un tirage
- Suppression d'un tirage
- Exportation des groupes

## Création d'un Tirage (Génération de Groupes)

Pour générer des groupes à partir d'une liste, envoyez une requête POST à l'endpoint `/api/lists/{listId}/draws` :

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Tirage du 15 juillet",
    "groupSize": 4,
    "balancingAttributes": ["age", "experience", "skills"],
    "balancingWeights": {
      "age": 2,
      "experience": 3,
      "skills": 1
    }
  }'
```

Les paramètres disponibles sont :

- `name` : Nom du tirage (optionnel)
- `groupSize` : Nombre de personnes par groupe (obligatoire)
- `balancingAttributes` : Liste des attributs à prendre en compte pour l'équilibrage (obligatoire)
- `balancingWeights` : Poids à attribuer à chaque attribut pour l'équilibrage (optionnel)
- `fixedGroups` : Groupes prédéfinis à respecter (optionnel)
- `constraints` : Contraintes supplémentaires à respecter (optionnel)

Vous recevrez une réponse contenant les groupes générés :

```json
{
  "id": 1,
  "name": "Tirage du 15 juillet",
  "createdAt": "2023-07-03T12:34:56Z",
  "listId": 1,
  "groupSize": 4,
  "balancingAttributes": ["age", "experience", "skills"],
  "balancingWeights": {
    "age": 2,
    "experience": 3,
    "skills": 1
  },
  "groups": [
    {
      "id": 1,
      "name": "Groupe 1",
      "persons": [
        {
          "id": 1,
          "firstName": "Jean-Pierre",
          "lastName": "Dupont",
          "email": "jean-pierre.dupont@exemple.com",
          "attributes": {
            "age": 32,
            "experience": "expert",
            "skills": ["java", "spring", "kubernetes", "docker"]
          }
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
          }
        },
        {
          "id": 7,
          "firstName": "Thomas",
          "lastName": "Leroy",
          "email": "thomas.leroy@exemple.com",
          "attributes": {
            "age": 24,
            "experience": "beginner",
            "skills": ["javascript", "react"]
          }
        },
        {
          "id": 10,
          "firstName": "Émilie",
          "lastName": "Moreau",
          "email": "emilie.moreau@exemple.com",
          "attributes": {
            "age": 29,
            "experience": "intermediate",
            "skills": ["php", "laravel"]
          }
        }
      ]
    },
    {
      "id": 2,
      "name": "Groupe 2",
      "persons": [
        {
          "id": 2,
          "firstName": "Pierre",
          "lastName": "Martin",
          "email": "pierre.martin@exemple.com",
          "attributes": {
            "age": 28,
            "experience": "beginner",
            "skills": ["javascript", "react"]
          }
        }
      ]
    }
  ]
}
```

## Récupération des Tirages et des Groupes

### Récupération de tous les tirages d'une liste

Pour récupérer tous les tirages effectués sur une liste, utilisez l'endpoint `/api/lists/{listId}/draws` :

```bash
curl -X GET http://localhost:8080/api/lists/1/draws \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant la liste des tirages :

```json
{
  "content": [
    {
      "id": 1,
      "name": "Tirage du 15 juillet",
      "createdAt": "2023-07-03T12:34:56Z",
      "listId": 1,
      "groupSize": 4,
      "groupCount": 3
    },
    {
      "id": 2,
      "name": "Tirage du 20 juillet",
      "createdAt": "2023-07-04T10:20:30Z",
      "listId": 1,
      "groupSize": 3,
      "groupCount": 4
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

### Récupération d'un tirage spécifique

Pour récupérer les détails d'un tirage spécifique, utilisez l'endpoint `/api/draws/{drawId}` :

```bash
curl -X GET http://localhost:8080/api/draws/1 \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant les détails du tirage et les groupes générés (voir l'exemple de réponse dans la section [Création d'un Tirage](#création-dun-tirage-génération-de-groupes)).

### Récupération d'un groupe spécifique

Pour récupérer les détails d'un groupe spécifique, utilisez l'endpoint `/api/groups/{groupId}` :

```bash
curl -X GET http://localhost:8080/api/groups/1 \
  -H "Authorization: Bearer votre_token_jwt"
```

Vous recevrez une réponse contenant les détails du groupe :

```json
{
  "id": 1,
  "name": "Groupe 1",
  "drawId": 1,
  "persons": [
    {
      "id": 1,
      "firstName": "Jean-Pierre",
      "lastName": "Dupont",
      "email": "jean-pierre.dupont@exemple.com",
      "attributes": {
        "age": 32,
        "experience": "expert",
        "skills": ["java", "spring", "kubernetes", "docker"]
      }
    },
    {
      "id": 2,
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
}
```

## Modification des Paramètres d'un Tirage

Pour régénérer les groupes d'un tirage existant avec de nouveaux paramètres, utilisez l'endpoint `/api/draws/{drawId}/regenerate` :

```bash
curl -X POST http://localhost:8080/api/draws/1/regenerate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "groupSize": 3,
    "balancingAttributes": ["age", "experience"],
    "balancingWeights": {
      "age": 1,
      "experience": 2
    }
  }'
```

Vous recevrez une réponse contenant les nouveaux groupes générés.

## Suppression d'un Tirage

Pour supprimer un tirage, utilisez l'endpoint `/api/draws/{drawId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/draws/1 \
  -H "Authorization: Bearer votre_token_jwt"
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Exportation des Groupes

### Exportation au format CSV

Pour exporter les groupes d'un tirage au format CSV, utilisez l'endpoint `/api/draws/{drawId}/export` :

```bash
curl -X GET http://localhost:8080/api/draws/1/export \
  -H "Authorization: Bearer votre_token_jwt" \
  -o groupes.csv
```

### Exportation au format PDF

Pour exporter les groupes d'un tirage au format PDF, utilisez l'endpoint `/api/draws/{drawId}/export/pdf` :

```bash
curl -X GET http://localhost:8080/api/draws/1/export/pdf \
  -H "Authorization: Bearer votre_token_jwt" \
  -o groupes.pdf
```

## Algorithmes d'Équilibrage

EasyGroup propose plusieurs algorithmes d'équilibrage pour générer des groupes :

### Équilibrage Standard

L'algorithme d'équilibrage standard tente de répartir les personnes de manière à ce que chaque groupe ait une distribution similaire des attributs spécifiés.

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Tirage standard",
    "groupSize": 4,
    "balancingAttributes": ["age", "experience", "skills"],
    "algorithm": "standard"
  }'
```

### Équilibrage Aléatoire

L'algorithme d'équilibrage aléatoire répartit les personnes de manière aléatoire, sans tenir compte des attributs.

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Tirage aléatoire",
    "groupSize": 4,
    "algorithm": "random"
  }'
```

### Équilibrage Optimisé

L'algorithme d'équilibrage optimisé utilise des techniques avancées pour trouver la meilleure répartition possible en fonction des attributs spécifiés.

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Tirage optimisé",
    "groupSize": 4,
    "balancingAttributes": ["age", "experience", "skills"],
    "algorithm": "optimized",
    "optimizationIterations": 1000
  }'
```

## Contraintes Avancées

### Personnes à Séparer

Pour spécifier des personnes qui ne doivent pas se retrouver dans le même groupe :

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Tirage avec contraintes",
    "groupSize": 4,
    "balancingAttributes": ["age", "experience"],
    "constraints": {
      "separate": [
        [1, 2],  // Les personnes avec les IDs 1 et 2 ne doivent pas être dans le même groupe
        [3, 4, 5]  // Les personnes avec les IDs 3, 4 et 5 ne doivent pas être dans le même groupe
      ]
    }
  }'
```

### Personnes à Regrouper

Pour spécifier des personnes qui doivent se retrouver dans le même groupe :

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Tirage avec contraintes",
    "groupSize": 4,
    "balancingAttributes": ["age", "experience"],
    "constraints": {
      "together": [
        [1, 2],  // Les personnes avec les IDs 1 et 2 doivent être dans le même groupe
        [3, 4]  // Les personnes avec les IDs 3 et 4 doivent être dans le même groupe
      ]
    }
  }'
```

### Groupes Prédéfinis

Pour spécifier des groupes prédéfinis qui doivent être respectés :

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer votre_token_jwt" \
  -d '{
    "name": "Tirage avec groupes prédéfinis",
    "groupSize": 4,
    "balancingAttributes": ["age", "experience"],
    "fixedGroups": [
      {
        "name": "Groupe A",
        "personIds": [1, 2, 3, 4]
      },
      {
        "name": "Groupe B",
        "personIds": [5, 6, 7, 8]
      }
    ]
  }'
```

## Bonnes Pratiques

### Choix des Attributs d'Équilibrage

- Choisissez des attributs qui sont pertinents pour votre contexte
- Attribuez des poids plus élevés aux attributs les plus importants
- Limitez le nombre d'attributs d'équilibrage pour éviter des contraintes trop strictes

### Taille des Groupes

- Choisissez une taille de groupe qui est adaptée à votre contexte
- Assurez-vous que le nombre total de personnes est divisible par la taille de groupe, ou prévoyez une stratégie pour gérer les personnes restantes
- Testez différentes tailles de groupe pour trouver la configuration optimale

### Évaluation des Résultats

- Examinez les groupes générés pour vérifier qu'ils répondent à vos besoins
- Utilisez les métriques d'équilibrage fournies pour évaluer la qualité des groupes
- Régénérez les groupes avec des paramètres différents si nécessaire

## Étapes Suivantes

Maintenant que vous avez exploré toutes les sections du guide utilisateur, vous pouvez consulter la [référence de l'API](/api-reference/) pour une documentation complète de tous les endpoints disponibles, ou la [documentation technique](/technical-docs/) pour en savoir plus sur l'architecture et les modèles de données d'EasyGroup.
