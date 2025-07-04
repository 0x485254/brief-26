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
  -b cookies.txt \
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
        // ...
      ]
    }
  ]
}
```

## Récupération des Tirages

### Récupération de tous les tirages d'une liste

Pour récupérer tous les tirages effectués sur une liste, utilisez l'endpoint `/api/lists/{listId}/draws` :

```bash
curl -X GET http://localhost:8080/api/lists/1/draws \
  -b cookies.txt
```

Vous recevrez une réponse contenant la liste des tirages :

```json
[
  {
    "id": 1,
    "name": "Tirage du 15 juillet",
    "createdAt": "2023-07-03T12:34:56Z",
    "listId": 1,
    "groupSize": 4
  },
  {
    "id": 2,
    "name": "Tirage du 20 juillet",
    "createdAt": "2023-07-20T10:15:30Z",
    "listId": 1,
    "groupSize": 3
  }
]
```

### Récupération d'un tirage spécifique

Pour récupérer les détails d'un tirage spécifique, utilisez l'endpoint `/api/draws/{drawId}` :

```bash
curl -X GET http://localhost:8080/api/draws/1 \
  -b cookies.txt
```

Vous recevrez une réponse contenant les détails du tirage et les groupes générés :

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
    // ...
  ]
}
```

## Modification d'un Tirage

Pour modifier les paramètres d'un tirage et régénérer les groupes, utilisez l'endpoint `/api/draws/{drawId}` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/draws/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name": "Tirage du 15 juillet - Mise à jour",
    "groupSize": 3,
    "balancingAttributes": ["age", "experience", "skills"],
    "balancingWeights": {
      "age": 1,
      "experience": 4,
      "skills": 2
    }
  }'
```

Vous recevrez une réponse contenant les détails mis à jour du tirage et les nouveaux groupes générés.

## Suppression d'un Tirage

Pour supprimer un tirage, utilisez l'endpoint `/api/draws/{drawId}` avec la méthode DELETE :

```bash
curl -X DELETE http://localhost:8080/api/draws/1 \
  -b cookies.txt
```

En cas de succès, vous recevrez une réponse avec le code 204 No Content.

## Modification Manuelle des Groupes

Pour modifier manuellement la composition des groupes d'un tirage, utilisez l'endpoint `/api/draws/{drawId}/groups` avec la méthode PUT :

```bash
curl -X PUT http://localhost:8080/api/draws/1/groups \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "groups": [
      {
        "id": 1,
        "name": "Groupe A",
        "personIds": [1, 4, 7, 10]
      },
      {
        "id": 2,
        "name": "Groupe B",
        "personIds": [2, 5, 8, 11]
      },
      {
        "id": 3,
        "name": "Groupe C",
        "personIds": [3, 6, 9, 12]
      }
    ]
  }'
```

Vous recevrez une réponse contenant les groupes mis à jour.

## Exportation des Groupes

Pour exporter les groupes d'un tirage dans différents formats, utilisez l'endpoint `/api/draws/{drawId}/export` :

```bash
curl -X GET "http://localhost:8080/api/draws/1/export?format=csv" \
  -b cookies.txt \
  -o groupes.csv
```

Les formats disponibles sont :

- `csv` : Format CSV (par défaut)
- `xlsx` : Format Excel
- `pdf` : Format PDF
- `json` : Format JSON

## Régénération des Groupes

Pour régénérer les groupes d'un tirage sans modifier ses paramètres, utilisez l'endpoint `/api/draws/{drawId}/regenerate` :

```bash
curl -X POST http://localhost:8080/api/draws/1/regenerate \
  -b cookies.txt
```

Vous recevrez une réponse contenant les nouveaux groupes générés.

## Stratégies d'Équilibrage

EasyGroup propose plusieurs stratégies d'équilibrage pour générer des groupes optimaux :

### Équilibrage par Attributs Numériques

Pour les attributs numériques comme l'âge ou les années d'expérience, EasyGroup peut :

- Distribuer équitablement les valeurs entre les groupes
- Assurer une moyenne similaire dans chaque groupe
- Minimiser l'écart-type dans chaque groupe

### Équilibrage par Attributs Catégoriels

Pour les attributs catégoriels comme les compétences ou les départements, EasyGroup peut :

- Assurer une diversité maximale dans chaque groupe
- Garder ensemble les personnes ayant des valeurs similaires
- Séparer les personnes ayant des valeurs similaires

## Contraintes de Groupe

Vous pouvez ajouter des contraintes spécifiques lors de la génération de groupes :

### Contraintes de Personnes

Pour spécifier que certaines personnes doivent être dans le même groupe ou dans des groupes différents :

```bash
curl -X POST http://localhost:8080/api/draws/1/constraints \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "type": "MUST_BE_TOGETHER",
    "personIds": [1, 3]
  }'
```

Types de contraintes disponibles :

- `MUST_BE_TOGETHER` : Les personnes spécifiées doivent être dans le même groupe
- `MUST_BE_SEPARATED` : Les personnes spécifiées doivent être dans des groupes différents
- `MUST_BE_IN_GROUP` : La personne spécifiée doit être dans un groupe spécifique

### Suppression d'une Contrainte

Pour supprimer une contrainte :

```bash
curl -X DELETE http://localhost:8080/api/draws/1/constraints/1 \
  -b cookies.txt
```

## Statistiques d'Équilibrage

Pour obtenir des statistiques sur l'équilibrage des groupes d'un tirage, utilisez l'endpoint `/api/draws/{drawId}/stats` :

```bash
curl -X GET http://localhost:8080/api/draws/1/stats \
  -b cookies.txt
```

Vous recevrez une réponse contenant des statistiques détaillées sur l'équilibrage des groupes :

```json
{
  "drawId": 1,
  "name": "Tirage du 15 juillet",
  "groupSize": 4,
  "totalPersons": 12,
  "personsPerGroup": {
    "min": 4,
    "max": 4,
    "average": 4
  },
  "attributeStats": [
    {
      "attribute": "age",
      "averagePerGroup": [30.0, 31.5, 29.8],
      "standardDeviation": 0.9,
      "balanceScore": 0.92
    },
    {
      "attribute": "experience",
      "distribution": {
        "Groupe 1": {"expert": 1, "intermediate": 2, "beginner": 1},
        "Groupe 2": {"expert": 1, "intermediate": 1, "beginner": 2},
        "Groupe 3": {"expert": 1, "intermediate": 2, "beginner": 1}
      },
      "balanceScore": 0.88
    },
    {
      "attribute": "skills",
      "uniqueValuesPerGroup": [8, 7, 9],
      "diversityScore": 0.85
    }
  ],
  "overallBalanceScore": 0.89
}
```

## Bonnes Pratiques

### Choix des Attributs d'Équilibrage

- Identifiez les attributs les plus importants pour votre contexte
- Attribuez des poids plus élevés aux attributs prioritaires
- Utilisez un mélange d'attributs numériques et catégoriels pour un meilleur équilibrage

### Taille des Groupes

- Choisissez une taille de groupe adaptée à votre contexte
- Assurez-vous que le nombre total de personnes est divisible par la taille de groupe souhaitée
- Utilisez l'option `allowUnevenGroups` si nécessaire

### Contraintes

- Utilisez les contraintes avec parcimonie pour ne pas trop restreindre l'algorithme
- Privilégiez les contraintes de type `MUST_BE_TOGETHER` plutôt que `MUST_BE_IN_GROUP`
- Vérifiez que vos contraintes ne sont pas contradictoires

### Évaluation des Résultats

- Utilisez les statistiques d'équilibrage pour évaluer la qualité des groupes générés
- Régénérez les groupes plusieurs fois pour obtenir différentes possibilités
- Ajustez les poids des attributs en fonction des résultats obtenus