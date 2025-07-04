---
title: Tirages et Groupes
description: Documentation des endpoints de gestion des tirages et groupes de l'API EasyGroup
---

# Endpoints de Gestion des Tirages et Groupes

Cette page documente les endpoints de gestion des tirages et des groupes disponibles dans l'API EasyGroup.

## Création d'un Tirage

Permet de créer un nouveau tirage à partir d'une liste existante et de générer des groupes.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/lists/{listId}/draws</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "title": "Tirage du 15 janvier",
  "numberOfGroups": 3,
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 2,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 3,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "skills",
      "weight": 1,
      "strategy": "ENSURE_DIVERSITY"
    }
  ]
}
```

### Exemple de Requête

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "title": "Tirage du 15 janvier",
    "numberOfGroups": 3,
    "balancingCriteria": [
      {
        "attribute": "age",
        "weight": 2,
        "strategy": "DISTRIBUTE_EVENLY"
      },
      {
        "attribute": "experience",
        "weight": 3,
        "strategy": "DISTRIBUTE_EVENLY"
      },
      {
        "attribute": "skills",
        "weight": 1,
        "strategy": "ENSURE_DIVERSITY"
      }
    ]
  }'
```

### Réponse en Cas de Succès

**Code** : 201 Created

```json
{
  "id": 1,
  "title": "Tirage du 15 janvier",
  "createdAt": "2023-01-15T10:30:00Z",
  "numberOfGroups": 3,
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 2,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 3,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "skills",
      "weight": 1,
      "strategy": "ENSURE_DIVERSITY"
    }
  ],
  "groups": [
    {
      "id": 1,
      "name": "Groupe 1",
      "persons": [
        {
          "id": 1,
          "firstName": "Jean",
          "lastName": "Dupont"
        },
        {
          "id": 4,
          "firstName": "Sophie",
          "lastName": "Dubois"
        }
      ]
    },
    {
      "id": 2,
      "name": "Groupe 2",
      "persons": [
        {
          "id": 2,
          "firstName": "Marie",
          "lastName": "Martin"
        },
        {
          "id": 5,
          "firstName": "Thomas",
          "lastName": "Petit"
        }
      ]
    },
    {
      "id": 3,
      "name": "Groupe 3",
      "persons": [
        {
          "id": 3,
          "firstName": "Pierre",
          "lastName": "Durand"
        },
        {
          "id": 6,
          "firstName": "Julie",
          "lastName": "Robert"
        }
      ]
    }
  ]
}
```

### Réponses d'Erreur

**Condition** : Si l'utilisateur n'est pas authentifié.

**Code** : 401 Unauthorized

**Condition** : Si l'utilisateur n'a pas accès à la liste spécifiée.

**Code** : 403 Forbidden

**Condition** : Si la liste spécifiée n'existe pas.

**Code** : 404 Not Found

**Condition** : Si les critères d'équilibrage sont invalides.

**Code** : 400 Bad Request

```json
{
  "error": "Invalid balancing criteria",
  "message": "The attribute 'skills' does not exist in the list"
}
```

## Récupération des Tirages d'une Liste

Permet de récupérer tous les tirages effectués pour une liste spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}/draws</span>
</div>

### Exemple de Requête

```bash
curl -X GET http://localhost:8080/api/lists/1/draws \
  -b cookies.txt
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
[
  {
    "id": 1,
    "title": "Tirage du 15 janvier",
    "createdAt": "2023-01-15T10:30:00Z",
    "numberOfGroups": 3
  },
  {
    "id": 2,
    "title": "Tirage du 20 janvier",
    "createdAt": "2023-01-20T14:45:00Z",
    "numberOfGroups": 2
  }
]
```

## Récupération d'un Tirage Spécifique

Permet de récupérer les détails d'un tirage spécifique, y compris les groupes générés.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}</span>
</div>

### Exemple de Requête

```bash
curl -X GET http://localhost:8080/api/draws/1 \
  -b cookies.txt
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "title": "Tirage du 15 janvier",
  "createdAt": "2023-01-15T10:30:00Z",
  "numberOfGroups": 3,
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 2,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 3,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "skills",
      "weight": 1,
      "strategy": "ENSURE_DIVERSITY"
    }
  ],
  "groups": [
    {
      "id": 1,
      "name": "Groupe 1",
      "persons": [
        {
          "id": 1,
          "firstName": "Jean",
          "lastName": "Dupont",
          "attributes": {
            "age": "35",
            "experience": "senior",
            "skills": "java,python"
          }
        },
        {
          "id": 4,
          "firstName": "Sophie",
          "lastName": "Dubois",
          "attributes": {
            "age": "28",
            "experience": "junior",
            "skills": "javascript,html"
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
          "firstName": "Marie",
          "lastName": "Martin",
          "attributes": {
            "age": "42",
            "experience": "senior",
            "skills": "c++,rust"
          }
        },
        {
          "id": 5,
          "firstName": "Thomas",
          "lastName": "Petit",
          "attributes": {
            "age": "31",
            "experience": "mid",
            "skills": "python,javascript"
          }
        }
      ]
    },
    {
      "id": 3,
      "name": "Groupe 3",
      "persons": [
        {
          "id": 3,
          "firstName": "Pierre",
          "lastName": "Durand",
          "attributes": {
            "age": "39",
            "experience": "senior",
            "skills": "java,kotlin"
          }
        },
        {
          "id": 6,
          "firstName": "Julie",
          "lastName": "Robert",
          "attributes": {
            "age": "25",
            "experience": "junior",
            "skills": "html,css"
          }
        }
      ]
    }
  ]
}
```

## Modification d'un Tirage

Permet de modifier un tirage existant, y compris ses critères d'équilibrage, et de régénérer les groupes.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/draws/{drawId}</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "title": "Tirage du 15 janvier - Mise à jour",
  "numberOfGroups": 4,
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 1,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 4,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "skills",
      "weight": 2,
      "strategy": "ENSURE_DIVERSITY"
    }
  ]
}
```

### Exemple de Requête

```bash
curl -X PUT http://localhost:8080/api/draws/1 \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "title": "Tirage du 15 janvier - Mise à jour",
    "numberOfGroups": 4,
    "balancingCriteria": [
      {
        "attribute": "age",
        "weight": 1,
        "strategy": "DISTRIBUTE_EVENLY"
      },
      {
        "attribute": "experience",
        "weight": 4,
        "strategy": "DISTRIBUTE_EVENLY"
      },
      {
        "attribute": "skills",
        "weight": 2,
        "strategy": "ENSURE_DIVERSITY"
      }
    ]
  }'
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "title": "Tirage du 15 janvier - Mise à jour",
  "createdAt": "2023-01-15T10:30:00Z",
  "updatedAt": "2023-01-16T09:15:00Z",
  "numberOfGroups": 4,
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 1,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 4,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "skills",
      "weight": 2,
      "strategy": "ENSURE_DIVERSITY"
    }
  ],
  "groups": [
    // Nouveaux groupes générés
  ]
}
```

## Suppression d'un Tirage

Permet de supprimer un tirage existant et tous ses groupes associés.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/draws/{drawId}</span>
</div>

### Exemple de Requête

```bash
curl -X DELETE http://localhost:8080/api/draws/1 \
  -b cookies.txt
```

### Réponse en Cas de Succès

**Code** : 204 No Content

## Modification Manuelle des Groupes

Permet de modifier manuellement la composition des groupes d'un tirage.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/draws/{drawId}/groups</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "groups": [
    {
      "id": 1,
      "name": "Groupe A",
      "personIds": [1, 4, 7]
    },
    {
      "id": 2,
      "name": "Groupe B",
      "personIds": [2, 5, 8]
    },
    {
      "id": 3,
      "name": "Groupe C",
      "personIds": [3, 6, 9]
    }
  ]
}
```

### Exemple de Requête

```bash
curl -X PUT http://localhost:8080/api/draws/1/groups \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "groups": [
      {
        "id": 1,
        "name": "Groupe A",
        "personIds": [1, 4, 7]
      },
      {
        "id": 2,
        "name": "Groupe B",
        "personIds": [2, 5, 8]
      },
      {
        "id": 3,
        "name": "Groupe C",
        "personIds": [3, 6, 9]
      }
    ]
  }'
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "groups": [
    {
      "id": 1,
      "name": "Groupe A",
      "persons": [
        {
          "id": 1,
          "firstName": "Jean",
          "lastName": "Dupont"
        },
        {
          "id": 4,
          "firstName": "Sophie",
          "lastName": "Dubois"
        },
        {
          "id": 7,
          "firstName": "Lucas",
          "lastName": "Moreau"
        }
      ]
    },
    {
      "id": 2,
      "name": "Groupe B",
      "persons": [
        {
          "id": 2,
          "firstName": "Marie",
          "lastName": "Martin"
        },
        {
          "id": 5,
          "firstName": "Thomas",
          "lastName": "Petit"
        },
        {
          "id": 8,
          "firstName": "Emma",
          "lastName": "Leroy"
        }
      ]
    },
    {
      "id": 3,
      "name": "Groupe C",
      "persons": [
        {
          "id": 3,
          "firstName": "Pierre",
          "lastName": "Durand"
        },
        {
          "id": 6,
          "firstName": "Julie",
          "lastName": "Robert"
        },
        {
          "id": 9,
          "firstName": "Hugo",
          "lastName": "Simon"
        }
      ]
    }
  ]
}
```

## Exportation des Groupes

Permet d'exporter les groupes d'un tirage dans différents formats.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}/export</span>
</div>

### Paramètres de Requête

| Paramètre | Type   | Description                                                |
|-----------|--------|------------------------------------------------------------|
| format    | string | Format d'exportation (csv, pdf, xlsx). Par défaut : csv    |

### Exemple de Requête

```bash
curl -X GET "http://localhost:8080/api/draws/1/export?format=csv" \
  -b cookies.txt \
  -o groupes.csv
```

### Réponse en Cas de Succès

**Code** : 200 OK

Le contenu de la réponse dépend du format demandé.

## Régénération des Groupes

Permet de régénérer les groupes d'un tirage existant sans modifier ses critères d'équilibrage.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/draws/{drawId}/regenerate</span>
</div>

### Exemple de Requête

```bash
curl -X POST http://localhost:8080/api/draws/1/regenerate \
  -b cookies.txt
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "title": "Tirage du 15 janvier",
  "createdAt": "2023-01-15T10:30:00Z",
  "updatedAt": "2023-01-16T11:20:00Z",
  "numberOfGroups": 3,
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 2,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 3,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "skills",
      "weight": 1,
      "strategy": "ENSURE_DIVERSITY"
    }
  ],
  "groups": [
    // Nouveaux groupes générés
  ]
}
```

## Partage d'un Tirage

Permet de partager un tirage avec d'autres utilisateurs.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/draws/{drawId}/share</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "userId": 2,
  "permission": "VIEW"
}
```

### Exemple de Requête

```bash
curl -X POST http://localhost:8080/api/draws/1/share \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "userId": 2,
    "permission": "VIEW"
  }'
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "message": "Tirage partagé avec succès"
}
```

## Récupération des Statistiques d'un Tirage

Permet de récupérer des statistiques sur l'équilibrage des groupes d'un tirage.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}/stats</span>
</div>

### Exemple de Requête

```bash
curl -X GET http://localhost:8080/api/draws/1/stats \
  -b cookies.txt
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "drawId": 1,
  "title": "Tirage du 15 janvier",
  "numberOfGroups": 3,
  "totalPersons": 6,
  "personsPerGroup": {
    "min": 2,
    "max": 2,
    "average": 2
  },
  "attributeStats": [
    {
      "attribute": "age",
      "averagePerGroup": [35.5, 36.5, 32.0],
      "standardDeviation": 2.3,
      "balanceScore": 0.85
    },
    {
      "attribute": "experience",
      "distribution": {
        "Groupe 1": {"senior": 1, "junior": 1},
        "Groupe 2": {"senior": 1, "mid": 1},
        "Groupe 3": {"senior": 1, "junior": 1}
      },
      "balanceScore": 0.92
    },
    {
      "attribute": "skills",
      "uniqueValuesPerGroup": [4, 4, 4],
      "diversityScore": 0.78
    }
  ],
  "overallBalanceScore": 0.85
}
```

## Stratégies d'Équilibrage Disponibles

Récupère la liste des stratégies d'équilibrage disponibles et leurs descriptions.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/balancing-strategies</span>
</div>

### Exemple de Requête

```bash
curl -X GET http://localhost:8080/api/draws/balancing-strategies \
  -b cookies.txt
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
[
  {
    "id": "DISTRIBUTE_EVENLY",
    "name": "Distribuer équitablement",
    "description": "Répartit les valeurs numériques de manière équilibrée entre les groupes",
    "applicableToTypes": ["numeric", "ordinal"],
    "examples": [
      "Âge",
      "Années d'expérience",
      "Niveau (débutant, intermédiaire, avancé)"
    ]
  },
  {
    "id": "ENSURE_DIVERSITY",
    "name": "Assurer la diversité",
    "description": "Maximise la diversité des valeurs catégorielles dans chaque groupe",
    "applicableToTypes": ["categorical", "tags"],
    "examples": [
      "Compétences",
      "Départements",
      "Centres d'intérêt"
    ]
  },
  {
    "id": "KEEP_TOGETHER",
    "name": "Garder ensemble",
    "description": "Essaie de garder les personnes ayant la même valeur dans le même groupe",
    "applicableToTypes": ["categorical", "boolean"],
    "examples": [
      "Équipe actuelle",
      "Langue maternelle",
      "Localisation"
    ]
  },
  {
    "id": "SEPARATE",
    "name": "Séparer",
    "description": "Essaie de séparer les personnes ayant la même valeur dans différents groupes",
    "applicableToTypes": ["categorical", "boolean"],
    "examples": [
      "Rôle dans l'équipe",
      "Expertise technique",
      "Ancienneté"
    ]
  }
]
```

## Contraintes de Groupe

### Ajout d'une Contrainte

Permet d'ajouter une contrainte pour le tirage (par exemple, deux personnes qui doivent être dans le même groupe).

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/draws/{drawId}/constraints</span>
</div>

### En-têtes de la Requête

```
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "type": "MUST_BE_TOGETHER",
  "personIds": [1, 3]
}
```

### Exemple de Requête

```bash
curl -X POST http://localhost:8080/api/draws/1/constraints \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "type": "MUST_BE_TOGETHER",
    "personIds": [1, 3]
  }'
```

### Réponse en Cas de Succès

**Code** : 201 Created

```json
{
  "id": 1,
  "type": "MUST_BE_TOGETHER",
  "personIds": [1, 3]
}
```

### Types de Contraintes Disponibles

- `MUST_BE_TOGETHER` : Les personnes spécifiées doivent être dans le même groupe
- `MUST_BE_SEPARATED` : Les personnes spécifiées doivent être dans des groupes différents
- `MUST_BE_IN_GROUP` : La personne spécifiée doit être dans un groupe spécifique

### Suppression d'une Contrainte

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/draws/{drawId}/constraints/{constraintId}</span>
</div>

### Exemple de Requête

```bash
curl -X DELETE http://localhost:8080/api/draws/1/constraints/1 \
  -b cookies.txt
```

### Réponse en Cas de Succès

**Code** : 204 No Content