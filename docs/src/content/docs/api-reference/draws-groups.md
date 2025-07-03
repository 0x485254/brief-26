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
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
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
          "lastName": "Martin"
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

## Récupération des Tirages

### Récupération de Tous les Tirages d'une Liste

Permet de récupérer tous les tirages associés à une liste spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/lists/{listId}/draws</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Paramètres de Requête

- `page` : Numéro de la page (défaut: 0)
- `size` : Nombre d'éléments par page (défaut: 20)
- `sort` : Champ de tri (défaut: createdAt,desc)

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "content": [
    {
      "id": 2,
      "title": "Tirage du 20 janvier",
      "createdAt": "2023-01-20T14:15:00Z",
      "numberOfGroups": 2,
      "groups": [
        {
          "id": 4,
          "name": "Groupe 1"
        },
        {
          "id": 5,
          "name": "Groupe 2"
        }
      ]
    },
    {
      "id": 1,
      "title": "Tirage du 15 janvier",
      "createdAt": "2023-01-15T10:30:00Z",
      "numberOfGroups": 3,
      "groups": [
        {
          "id": 1,
          "name": "Groupe 1"
        },
        {
          "id": 2,
          "name": "Groupe 2"
        },
        {
          "id": 3,
          "name": "Groupe 3"
        }
      ]
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

### Récupération d'un Tirage Spécifique

Permet de récupérer les détails d'un tirage spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
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
            "age": 30,
            "experience": "intermediate",
            "skills": ["java", "spring"]
          }
        },
        {
          "id": 4,
          "firstName": "Sophie",
          "lastName": "Dubois",
          "attributes": {
            "age": 35,
            "experience": "intermediate",
            "skills": ["python", "django"]
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
            "age": 25,
            "experience": "beginner",
            "skills": ["javascript", "react"]
          }
        },
        {
          "id": 5,
          "firstName": "Thomas",
          "lastName": "Petit",
          "attributes": {
            "age": 40,
            "experience": "expert",
            "skills": ["java", "spring", "kubernetes"]
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
          "lastName": "Martin",
          "attributes": {
            "age": 28,
            "experience": "beginner",
            "skills": ["javascript", "react"]
          }
        },
        {
          "id": 6,
          "firstName": "Julie",
          "lastName": "Robert",
          "attributes": {
            "age": 32,
            "experience": "expert",
            "skills": ["python", "django", "flask"]
          }
        }
      ]
    }
  ]
}
```

## Modification d'un Tirage

Permet de modifier les informations d'un tirage existant.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/draws/{drawId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "title": "Tirage du 15 janvier - Modifié"
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "title": "Tirage du 15 janvier - Modifié",
  "createdAt": "2023-01-15T10:30:00Z",
  "updatedAt": "2023-01-16T09:45:00Z",
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
      "name": "Groupe 1"
    },
    {
      "id": 2,
      "name": "Groupe 2"
    },
    {
      "id": 3,
      "name": "Groupe 3"
    }
  ]
}
```

## Suppression d'un Tirage

Permet de supprimer un tirage et tous ses groupes associés.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/draws/{drawId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 204 No Content

## Gestion des Groupes

### Récupération des Groupes d'un Tirage

Permet de récupérer tous les groupes générés pour un tirage spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}/groups</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
[
  {
    "id": 1,
    "name": "Groupe 1",
    "persons": [
      {
        "id": 1,
        "firstName": "Jean",
        "lastName": "Dupont",
        "attributes": {
          "age": 30,
          "experience": "intermediate",
          "skills": ["java", "spring"]
        }
      },
      {
        "id": 4,
        "firstName": "Sophie",
        "lastName": "Dubois",
        "attributes": {
          "age": 35,
          "experience": "intermediate",
          "skills": ["python", "django"]
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
          "age": 25,
          "experience": "beginner",
          "skills": ["javascript", "react"]
        }
      },
      {
        "id": 5,
        "firstName": "Thomas",
        "lastName": "Petit",
        "attributes": {
          "age": 40,
          "experience": "expert",
          "skills": ["java", "spring", "kubernetes"]
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
        "lastName": "Martin",
        "attributes": {
          "age": 28,
          "experience": "beginner",
          "skills": ["javascript", "react"]
        }
      },
      {
        "id": 6,
        "firstName": "Julie",
        "lastName": "Robert",
        "attributes": {
          "age": 32,
          "experience": "expert",
          "skills": ["python", "django", "flask"]
        }
      }
    ]
  }
]
```

### Récupération d'un Groupe Spécifique

Permet de récupérer les détails d'un groupe spécifique.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/groups/{groupId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Groupe 1",
  "drawId": 1,
  "persons": [
    {
      "id": 1,
      "firstName": "Jean",
      "lastName": "Dupont",
      "attributes": {
        "age": 30,
        "experience": "intermediate",
        "skills": ["java", "spring"]
      }
    },
    {
      "id": 4,
      "firstName": "Sophie",
      "lastName": "Dubois",
      "attributes": {
        "age": 35,
        "experience": "intermediate",
        "skills": ["python", "django"]
      }
    }
  ]
}
```

### Modification d'un Groupe

Permet de modifier les informations d'un groupe existant.

<div class="api-endpoint">
  <span class="method">PUT</span>
  <span class="path">/api/groups/{groupId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "name": "Équipe Alpha"
}
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Équipe Alpha",
  "drawId": 1,
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
}
```

## Gestion des Personnes dans les Groupes

### Ajout d'une Personne à un Groupe

Permet d'ajouter manuellement une personne à un groupe existant.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/groups/{groupId}/persons/{personId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Équipe Alpha",
  "drawId": 1,
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
      "lastName": "Bernard"
    }
  ]
}
```

### Suppression d'une Personne d'un Groupe

Permet de retirer manuellement une personne d'un groupe.

<div class="api-endpoint">
  <span class="method">DELETE</span>
  <span class="path">/api/groups/{groupId}/persons/{personId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "name": "Équipe Alpha",
  "drawId": 1,
  "persons": [
    {
      "id": 1,
      "firstName": "Jean",
      "lastName": "Dupont"
    }
  ]
}
```

### Déplacement d'une Personne entre Groupes

Permet de déplacer une personne d'un groupe à un autre.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/groups/{sourceGroupId}/persons/{personId}/move/{targetGroupId}</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "sourceGroup": {
    "id": 1,
    "name": "Équipe Alpha",
    "persons": [
      {
        "id": 1,
        "firstName": "Jean",
        "lastName": "Dupont"
      }
    ]
  },
  "targetGroup": {
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
      },
      {
        "id": 4,
        "firstName": "Sophie",
        "lastName": "Dubois"
      }
    ]
  }
}
```

## Rééquilibrage des Groupes

### Rééquilibrage Automatique des Groupes

Permet de rééquilibrer automatiquement les groupes d'un tirage existant.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/draws/{drawId}/rebalance</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Corps de la Requête

```json
{
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 1,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 3,
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

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "id": 1,
  "title": "Tirage du 15 janvier - Modifié",
  "createdAt": "2023-01-15T10:30:00Z",
  "updatedAt": "2023-01-17T11:20:00Z",
  "numberOfGroups": 3,
  "balancingCriteria": [
    {
      "attribute": "age",
      "weight": 1,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "experience",
      "weight": 3,
      "strategy": "DISTRIBUTE_EVENLY"
    },
    {
      "attribute": "skills",
      "weight": 2,
      "strategy": "ENSURE_DIVERSITY"
    }
  ],
  "groups": [
    {
      "id": 1,
      "name": "Équipe Alpha",
      "persons": [
        {
          "id": 1,
          "firstName": "Jean",
          "lastName": "Dupont"
        },
        {
          "id": 6,
          "firstName": "Julie",
          "lastName": "Robert"
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
          "lastName": "Martin"
        },
        {
          "id": 4,
          "firstName": "Sophie",
          "lastName": "Dubois"
        }
      ]
    }
  ]
}
```

## Export des Groupes

### Export des Groupes en CSV

Permet d'exporter les groupes d'un tirage au format CSV.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}/export/csv</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK
**Content-Type** : text/csv
**Content-Disposition** : attachment; filename="tirage-1-groupes.csv"

```
Groupe,Prénom,Nom,Email,Âge,Expérience,Compétences
"Équipe Alpha","Jean","Dupont","jean.dupont@exemple.com",30,"intermediate","java,spring"
"Équipe Alpha","Julie","Robert","julie.robert@exemple.com",32,"expert","python,django,flask"
"Groupe 2","Marie","Martin","marie.martin@exemple.com",25,"beginner","javascript,react"
"Groupe 2","Thomas","Petit","thomas.petit@exemple.com",40,"expert","java,spring,kubernetes"
"Groupe 3","Pierre","Martin","pierre.martin@exemple.com",28,"beginner","javascript,react"
"Groupe 3","Sophie","Dubois","sophie.dubois@exemple.com",35,"intermediate","python,django"
```

### Export des Groupes en PDF

Permet d'exporter les groupes d'un tirage au format PDF.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}/export/pdf</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK
**Content-Type** : application/pdf
**Content-Disposition** : attachment; filename="tirage-1-groupes.pdf"

## Statistiques des Groupes

### Récupération des Statistiques d'un Tirage

Permet de récupérer des statistiques sur les groupes d'un tirage.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/draws/{drawId}/stats</span>
</div>

### En-têtes de la Requête

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "drawId": 1,
  "title": "Tirage du 15 janvier - Modifié",
  "numberOfGroups": 3,
  "totalPersons": 6,
  "averagePersonsPerGroup": 2.0,
  "attributeStats": {
    "age": {
      "min": 25,
      "max": 40,
      "average": 31.67,
      "groupAverages": {
        "1": 31.0,
        "2": 32.5,
        "3": 31.5
      },
      "standardDeviation": 5.12,
      "balanceScore": 0.92
    },
    "experience": {
      "distribution": {
        "beginner": {
          "total": 2,
          "groupDistribution": {
            "1": 0,
            "2": 1,
            "3": 1
          }
        },
        "intermediate": {
          "total": 2,
          "groupDistribution": {
            "1": 1,
            "2": 0,
            "3": 1
          }
        },
        "expert": {
          "total": 2,
          "groupDistribution": {
            "1": 1,
            "2": 1,
            "3": 0
          }
        }
      },
      "balanceScore": 0.89
    },
    "skills": {
      "uniqueValues": ["java", "spring", "python", "django", "javascript", "react", "kubernetes", "flask"],
      "distribution": {
        "java": {
          "total": 2,
          "groupDistribution": {
            "1": 1,
            "2": 1,
            "3": 0
          }
        },
        "spring": {
          "total": 2,
          "groupDistribution": {
            "1": 1,
            "2": 1,
            "3": 0
          }
        },
        "python": {
          "total": 2,
          "groupDistribution": {
            "1": 1,
            "2": 0,
            "3": 1
          }
        },
        "django": {
          "total": 2,
          "groupDistribution": {
            "1": 1,
            "2": 0,
            "3": 1
          }
        },
        "javascript": {
          "total": 2,
          "groupDistribution": {
            "1": 0,
            "2": 1,
            "3": 1
          }
        },
        "react": {
          "total": 2,
          "groupDistribution": {
            "1": 0,
            "2": 1,
            "3": 1
          }
        },
        "kubernetes": {
          "total": 1,
          "groupDistribution": {
            "1": 0,
            "2": 1,
            "3": 0
          }
        },
        "flask": {
          "total": 1,
          "groupDistribution": {
            "1": 1,
            "2": 0,
            "3": 0
          }
        }
      },
      "diversityScore": 0.85
    }
  },
  "overallBalanceScore": 0.88
}
```

## Erreurs Courantes

### Tirage Non Trouvé

**Code** : 404 Not Found

```json
{
  "timestamp": "2023-01-17T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Tirage non trouvé avec l'ID: 999",
  "path": "/api/draws/999"
}
```

### Groupe Non Trouvé

**Code** : 404 Not Found

```json
{
  "timestamp": "2023-01-17T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Groupe non trouvé avec l'ID: 999",
  "path": "/api/groups/999"
}
```

### Personne Non Trouvée

**Code** : 404 Not Found

```json
{
  "timestamp": "2023-01-17T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Personne non trouvée avec l'ID: 999",
  "path": "/api/groups/1/persons/999"
}
```

### Accès Non Autorisé

**Code** : 403 Forbidden

```json
{
  "timestamp": "2023-01-17T12:34:56Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Vous n'avez pas les permissions nécessaires pour accéder à ce tirage",
  "path": "/api/draws/1"
}
```

### Validation des Données

**Code** : 422 Unprocessable Entity

```json
{
  "timestamp": "2023-01-17T12:34:56Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Validation failed for object='drawCreateRequest'",
  "path": "/api/lists/1/draws",
  "errors": [
    {
      "field": "numberOfGroups",
      "message": "must be greater than 0"
    },
    {
      "field": "title",
      "message": "must not be blank"
    }
  ]
}
```

### Conflit de Personne

**Code** : 409 Conflict

```json
{
  "timestamp": "2023-01-17T12:34:56Z",
  "status": 409,
  "error": "Conflict",
  "message": "La personne avec l'ID 1 est déjà présente dans un autre groupe de ce tirage",
  "path": "/api/groups/2/persons/1"
}
```