---
title: Référence de l'API
description: Documentation complète des endpoints de l'API EasyGroup
---

# Référence de l'API EasyGroup

Cette section fournit une documentation détaillée de tous les endpoints disponibles dans l'API EasyGroup pour la création intelligente de groupes d'apprenants.

## URL de Base

Lors de l'exécution en local, l'URL de base est :

```
http://localhost:8080
```

En production, l'URL de base dépend de votre configuration de déploiement.

## Format des Données

L'API EasyGroup utilise le format JSON pour les requêtes et les réponses. Sauf indication contraire, toutes les requêtes doivent inclure l'en-tête `Content-Type: application/json` et toutes les réponses seront au format JSON.

## Authentification

L'API EasyGroup utilise une authentification basée sur des tokens JWT (JSON Web Tokens). Pour plus de détails sur l'authentification, consultez la section [Authentification](/api-reference/authentication).

## Pagination

Les endpoints qui retournent des listes d'éléments utilisent la pagination pour limiter la taille des réponses. Les paramètres de pagination suivants sont disponibles :

- `page` : Numéro de la page à récupérer (commence à 0)
- `size` : Nombre d'éléments par page
- `sort` : Champ sur lequel trier les résultats (format : `champ,direction` où direction est `asc` ou `desc`)

Exemple de requête avec pagination :

```
GET /api/lists?page=0&size=10&sort=createdAt,desc
```

Exemple de réponse paginée :

```json
{
  "content": [
    {
      "id": 1,
      "name": "Ma liste",
      "description": "Description de ma liste",
      "createdAt": "2023-07-03T12:34:56Z",
      "updatedAt": "2023-07-03T12:34:56Z",
      "ownerId": 1,
      "personCount": 10
    },
    {
      "id": 2,
      "name": "Autre liste",
      "description": "Description d'une autre liste",
      "createdAt": "2023-07-02T10:20:30Z",
      "updatedAt": "2023-07-02T10:20:30Z",
      "ownerId": 1,
      "personCount": 5
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 42,
  "last": false,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 10,
  "first": true,
  "empty": false
}
```

## Gestion des Erreurs

L'API EasyGroup utilise les codes de statut HTTP standard pour indiquer le succès ou l'échec d'une requête. En cas d'erreur, la réponse inclut un objet JSON avec des détails sur l'erreur.

### Codes de Statut

- **200 OK** : La requête a réussi
- **201 Created** : La ressource a été créée avec succès
- **204 No Content** : La requête a réussi mais il n'y a pas de contenu à renvoyer
- **400 Bad Request** : La requête est invalide ou mal formée
- **401 Unauthorized** : L'authentification est requise ou a échoué
- **403 Forbidden** : L'utilisateur n'a pas les permissions nécessaires
- **404 Not Found** : La ressource demandée n'existe pas
- **422 Unprocessable Entity** : La requête est bien formée mais contient des erreurs sémantiques
- **500 Internal Server Error** : Une erreur est survenue côté serveur

### Format des Erreurs

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for object='authRequest'",
  "path": "/api/auth/register",
  "errors": [
    {
      "field": "email",
      "message": "must be a valid email address"
    },
    {
      "field": "password",
      "message": "must be at least 8 characters long"
    }
  ]
}
```

## Sections de la Référence API

La référence de l'API est organisée en sections thématiques :

- [**Authentification**](/api-reference/authentication) - Endpoints pour l'inscription, la connexion et la gestion des tokens
- [**Utilisateurs**](/api-reference/users) - Endpoints pour la gestion des utilisateurs
- [**Listes**](/api-reference/lists) - Endpoints pour la gestion des listes de personnes
- [**Personnes**](/api-reference/persons) - Endpoints pour la gestion des personnes dans les listes
- [**Tirages et Groupes**](/api-reference/draws-groups) - Endpoints pour la création et la gestion des tirages et des groupes

## Versionnement de l'API

L'API EasyGroup utilise le versionnement sémantique. La version actuelle de l'API est v1.0.0.

## Limites de Taux

Pour assurer la stabilité du service, l'API EasyGroup implémente des limites de taux. Les limites actuelles sont :

- **Utilisateurs non authentifiés** : 60 requêtes par heure
- **Utilisateurs authentifiés** : 1000 requêtes par heure

Si vous dépassez ces limites, vous recevrez une réponse avec le code de statut **429 Too Many Requests**.

## Outils et Ressources

### Swagger UI

Une interface Swagger UI est disponible pour explorer et tester l'API interactivement :

```
http://localhost:8080/swagger-ui.html
```

### Postman Collection

Une collection Postman est disponible pour tester l'API :

[Télécharger la collection Postman](https://example.com/easygroup-postman-collection.json)

## Support et Feedback

Si vous rencontrez des problèmes avec l'API ou si vous avez des suggestions d'amélioration, veuillez contacter l'équipe de développement ou ouvrir une issue sur le dépôt GitHub du projet.
