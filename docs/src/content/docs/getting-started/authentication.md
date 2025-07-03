---
title: Authentification
description: Guide d'authentification pour l'API EasyGroup
---

# Authentification avec l'API EasyGroup

Ce guide explique les différentes méthodes d'authentification disponibles dans l'API EasyGroup et comment les utiliser.

## Vue d'ensemble

L'API EasyGroup propose deux méthodes d'authentification principales :

1. **Authentification par JWT (JSON Web Token)** - Recommandée pour les applications frontend et les clients API
2. **Authentification par Cookie HTTP-Only** - Recommandée pour les applications web avec serveur

## Authentification par JWT

### Inscription

Pour créer un compte utilisateur, envoyez une requête POST à l'endpoint `/api/auth/register` :

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "votre_nom_utilisateur",
    "email": "votre_email@exemple.com",
    "password": "votre_mot_de_passe"
  }'
```

### Connexion

Pour vous authentifier et obtenir un token JWT, envoyez une requête POST à l'endpoint `/api/auth/login` :

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "votre_nom_utilisateur",
    "password": "votre_mot_de_passe"
  }'
```

Vous recevrez une réponse contenant votre token JWT :

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 3600
}
```

### Utilisation du Token

Pour accéder aux endpoints protégés, incluez le token JWT dans l'en-tête `Authorization` de vos requêtes :

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Rafraîchissement du Token

Les tokens JWT expirent après une période définie (par défaut : 1 heure). Pour obtenir un nouveau token sans avoir à se reconnecter, utilisez l'endpoint `/api/auth/refresh` :

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Authentification par Cookie HTTP-Only

### Inscription

Pour créer un compte utilisateur avec l'authentification par cookie, envoyez une requête POST à l'endpoint `/api/auth/cookie/register` :

```bash
curl -X POST http://localhost:8080/api/auth/cookie/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "votre_nom_utilisateur",
    "email": "votre_email@exemple.com",
    "password": "votre_mot_de_passe"
  }' \
  -c cookies.txt
```

### Connexion

Pour vous authentifier et obtenir un cookie HTTP-Only, envoyez une requête POST à l'endpoint `/api/auth/cookie/login` :

```bash
curl -X POST http://localhost:8080/api/auth/cookie/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "votre_nom_utilisateur",
    "password": "votre_mot_de_passe"
  }' \
  -c cookies.txt
```

Le serveur définira un cookie HTTP-Only dans votre navigateur, qui sera automatiquement inclus dans les requêtes suivantes.

### Utilisation du Cookie

Pour accéder aux endpoints protégés, incluez le cookie dans vos requêtes :

```bash
curl -X GET http://localhost:8080/api/users/me \
  -b cookies.txt
```

Dans un navigateur web, le cookie est automatiquement inclus dans toutes les requêtes vers le même domaine.

### Déconnexion

Pour vous déconnecter et supprimer le cookie, envoyez une requête POST à l'endpoint `/api/auth/cookie/logout` :

```bash
curl -X POST http://localhost:8080/api/auth/cookie/logout \
  -b cookies.txt
```

## Sécurité

### Hachage des Mots de Passe

EasyGroup utilise l'algorithme Argon2id pour le hachage des mots de passe, qui est considéré comme l'un des algorithmes de hachage les plus sécurisés disponibles actuellement.

### Protection CSRF

Pour l'authentification par cookie, EasyGroup implémente une protection CSRF (Cross-Site Request Forgery) pour empêcher les attaques par falsification de requête inter-sites.

Pour les requêtes non-GET avec l'authentification par cookie, vous devez inclure un token CSRF dans l'en-tête `X-CSRF-TOKEN` :

```bash
# Obtenir le token CSRF
curl -X GET http://localhost:8080/api/auth/csrf-token \
  -b cookies.txt

# Utiliser le token CSRF dans une requête POST
curl -X POST http://localhost:8080/api/lists \
  -H "Content-Type: application/json" \
  -H "X-CSRF-TOKEN: votre_token_csrf" \
  -b cookies.txt \
  -d '{
    "name": "Ma liste",
    "description": "Description de ma liste"
  }'
```

Dans une application web, le token CSRF peut être récupéré via JavaScript et inclus dans les requêtes.

## Bonnes Pratiques

### Stockage Sécurisé des Tokens JWT

Si vous utilisez l'authentification par JWT dans une application frontend :

- **Ne stockez pas** le token JWT dans le localStorage ou le sessionStorage, car ils sont vulnérables aux attaques XSS
- Utilisez plutôt un cookie HttpOnly avec l'authentification par cookie, ou stockez le token dans la mémoire de l'application

### Gestion des Erreurs d'Authentification

L'API renvoie des codes d'erreur HTTP appropriés en cas de problème d'authentification :

- **401 Unauthorized** : Identifiants invalides ou token expiré
- **403 Forbidden** : L'utilisateur n'a pas les permissions nécessaires
- **422 Unprocessable Entity** : Données d'inscription invalides

## Exemples d'Intégration

### Exemple avec JavaScript (Fetch API)

```javascript
// Connexion et obtention du token JWT
async function login(username, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  return data.token;
}

// Utilisation du token pour accéder à un endpoint protégé
async function getUserProfile(token) {
  const response = await fetch('http://localhost:8080/api/users/me', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
}

// Exemple d'utilisation
async function example() {
  const token = await login('votre_nom_utilisateur', 'votre_mot_de_passe');
  const userProfile = await getUserProfile(token);
  console.log(userProfile);
}
```

### Exemple avec Axios

```javascript
const axios = require('axios');

// Création d'une instance Axios avec le token JWT
function createAuthenticatedClient(token) {
  return axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
}

// Exemple d'utilisation
async function example() {
  // Connexion
  const loginResponse = await axios.post('http://localhost:8080/api/auth/login', {
    username: 'votre_nom_utilisateur',
    password: 'votre_mot_de_passe'
  });
  
  const token = loginResponse.data.token;
  
  // Création d'un client authentifié
  const client = createAuthenticatedClient(token);
  
  // Utilisation du client pour accéder à un endpoint protégé
  const userProfileResponse = await client.get('/users/me');
  console.log(userProfileResponse.data);
}
```

## Étapes Suivantes

Maintenant que vous comprenez comment vous authentifier auprès de l'API EasyGroup, vous pouvez explorer les fonctionnalités de l'API :

- [Guide utilisateur](/user-guide/) - Pour en savoir plus sur l'utilisation de l'API
- [Référence de l'API](/api-reference/) - Pour une documentation complète des endpoints disponibles