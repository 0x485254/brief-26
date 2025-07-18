---
title: Premiers Pas
description: Guide pour commencer à utiliser l'API EasyGroup
---

# Premiers Pas avec l'API EasyGroup

Ce guide vous aidera à faire vos premiers pas avec l'API EasyGroup après l'avoir [installée et configurée](/getting-started/installation).

## Vue d'ensemble

Pour commencer à utiliser l'API EasyGroup, vous devrez suivre ces étapes :

1. Créer un compte utilisateur
2. Vérifier votre adresse email via le lien reçu par email
3. S'authentifier pour obtenir un cookie HTTP-Only contenant un JWT
4. Utiliser ce cookie pour accéder aux endpoints protégés
5. Créer votre première liste
6. Ajouter des personnes à votre liste
7. Générer des groupes équilibrés

## Création d'un Compte Utilisateur

Pour créer un compte utilisateur, envoyez une requête POST à l'endpoint `/api/auth/register` :

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "votre_email@exemple.com",
    "password": "votre_mot_de_passe",
    "firstName": "Votre Prénom",
    "lastName": "Votre Nom"
  }'
```

Si l'inscription réussit, vous recevrez une réponse avec le statut HTTP 201 (Created) et un corps de réponse indiquant que l'inscription a réussi :

```json
true
```

Un email de vérification sera envoyé à l'adresse email que vous avez fournie. Vous devrez cliquer sur le lien dans cet email pour activer votre compte avant de pouvoir vous connecter.

## Authentification

Une fois votre compte créé et activé via le lien de vérification envoyé par email, vous devez vous authentifier pour obtenir un cookie HTTP-Only contenant un JWT. Envoyez une requête POST à l'endpoint `/api/auth/login` :

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "votre_email@exemple.com",
    "password": "votre_mot_de_passe"
  }' \
  -c cookies.txt
```

Si l'authentification réussit, vous recevrez une réponse avec le statut HTTP 200 (OK) et un corps de réponse contenant vos informations utilisateur :

```json
{
  "id": 1,
  "email": "votre_email@exemple.com",
  "firstName": "Votre Prénom",
  "lastName": "Votre Nom",
  "token": null,
  "role": "USER",
  "isActivated": true
}
```

Le serveur définira également un cookie HTTP-Only contenant un JWT dans votre navigateur, qui sera automatiquement inclus dans les requêtes suivantes.

Pour plus de détails sur l'authentification, consultez le [guide d'authentification](/getting-started/authentication).

## Utilisation du Cookie HTTP-Only avec JWT

Pour accéder aux endpoints protégés, incluez le cookie contenant le JWT dans vos requêtes :

```bash
curl -X GET http://localhost:8080/api/users/me \
  -b cookies.txt
```

## Création d'une Liste

Pour créer une liste, envoyez une requête POST à l'endpoint `/api/lists` :

```bash
curl -X POST http://localhost:8080/api/lists \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name": "Ma première liste",
    "description": "Une liste pour tester l'API EasyGroup"
  }'
```

Vous recevrez une réponse similaire à :

```json
{
  "id": 1,
  "name": "Ma première liste",
  "description": "Une liste pour tester l'API EasyGroup",
  "createdAt": "2023-07-03T12:34:56Z",
  "updatedAt": "2023-07-03T12:34:56Z",
  "ownerId": 1
}
```

## Ajout de Personnes à une Liste

Pour ajouter une personne à votre liste, envoyez une requête POST à l'endpoint `/api/lists/{listId}/persons` :

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

Répétez cette étape pour ajouter plusieurs personnes à votre liste.

## Génération de Groupes Équilibrés

Une fois que vous avez ajouté suffisamment de personnes à votre liste (au moins 4 personnes recommandées), vous pouvez générer des groupes équilibrés en envoyant une requête POST à l'endpoint `/api/lists/{listId}/draws` :

```bash
curl -X POST http://localhost:8080/api/lists/1/draws \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name": "Mon premier tirage",
    "groupSize": 2,
    "balancingAttributes": ["age", "experience"]
  }'
```

Vous recevrez une réponse contenant les groupes générés :

```json
{
  "id": 1,
  "name": "Mon premier tirage",
  "createdAt": "2023-07-03T12:34:56Z",
  "groups": [
    {
      "id": 1,
      "name": "Groupe 1",
      "persons": [
        {
          "id": 1,
          "firstName": "Jean",
          "lastName": "Dupont",
          "email": "jean.dupont@exemple.com",
          "attributes": {
            "age": 30,
            "experience": "intermediate",
            "skills": ["java", "spring"]
          }
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
          }
        }
      ]
    },
    {
      "id": 2,
      "name": "Groupe 2",
      "persons": [
        {
          "id": 3,
          "firstName": "Pierre",
          "lastName": "Durand",
          "email": "pierre.durand@exemple.com",
          "attributes": {
            "age": 35,
            "experience": "expert",
            "skills": ["python", "django"]
          }
        },
        {
          "id": 4,
          "firstName": "Sophie",
          "lastName": "Lefebvre",
          "email": "sophie.lefebvre@exemple.com",
          "attributes": {
            "age": 28,
            "experience": "intermediate",
            "skills": ["php", "laravel"]
          }
        }
      ]
    }
  ]
}
```

## Récupération des Groupes

Vous pouvez récupérer les groupes générés en envoyant une requête GET à l'endpoint `/api/draws/{drawId}` :

```bash
curl -X GET http://localhost:8080/api/draws/1 \
  -b cookies.txt
```

## Étapes Suivantes

Maintenant que vous avez fait vos premiers pas avec l'API EasyGroup, vous pouvez explorer les fonctionnalités plus avancées :

- [Guide d'authentification](/getting-started/authentication) - Pour en savoir plus sur l'authentification
- [Guide utilisateur](/user-guide/) - Pour en savoir plus sur l'utilisation de l'API
- [Référence de l'API](/api-reference/) - Pour une documentation complète des endpoints disponibles
