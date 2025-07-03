---
title: Authentification
description: Guide d'authentification pour l'API EasyGroup
---

# Authentification avec l'API EasyGroup

Ce guide explique la méthode d'authentification disponible dans l'API EasyGroup et comment l'utiliser.

## Vue d'ensemble

L'API EasyGroup utilise l'authentification par Cookie HTTP-Only, qui est recommandée pour les applications web car elle offre une meilleure sécurité contre les attaques XSS (Cross-Site Scripting).

## Authentification par Cookie HTTP-Only

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

Pour vous authentifier et obtenir un cookie HTTP-Only, envoyez une requête POST à l'endpoint `/api/auth/login` :

```bash
curl -X POST http://localhost:8080/api/auth/login \
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

Pour vous déconnecter et supprimer le cookie, envoyez une requête POST à l'endpoint `/api/auth/logout` :

```bash
curl -X POST http://localhost:8080/api/auth/logout \
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

### Sécurité des Cookies

L'authentification par Cookie HTTP-Only offre plusieurs avantages de sécurité :

- Les cookies HTTP-Only ne sont pas accessibles via JavaScript, ce qui les protège contre les attaques XSS
- Les cookies sont automatiquement envoyés avec chaque requête au même domaine
- La protection CSRF implémentée par EasyGroup empêche les attaques par falsification de requête

### Gestion des Erreurs d'Authentification

L'API renvoie des codes d'erreur HTTP appropriés en cas de problème d'authentification :

- **401 Unauthorized** : Identifiants invalides ou token expiré
- **403 Forbidden** : L'utilisateur n'a pas les permissions nécessaires
- **422 Unprocessable Entity** : Données d'inscription invalides

## Exemples d'Intégration

### Exemple avec JavaScript (Fetch API)

```javascript
// Connexion avec cookies
async function login(username, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password }),
    credentials: 'include' // Important pour inclure les cookies
  });

  return await response.json();
}

// Accès à un endpoint protégé avec cookies
async function getUserProfile() {
  const response = await fetch('http://localhost:8080/api/users/me', {
    credentials: 'include' // Important pour inclure les cookies
  });

  return await response.json();
}

// Exemple d'utilisation
async function example() {
  await login('votre_nom_utilisateur', 'votre_mot_de_passe');
  const userProfile = await getUserProfile();
  console.log(userProfile);
}
```

### Exemple avec Axios

```javascript
const axios = require('axios');

// Création d'une instance Axios avec support des cookies
function createAuthenticatedClient() {
  return axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true // Important pour inclure les cookies
  });
}

// Exemple d'utilisation
async function example() {
  // Connexion
  await axios.post('http://localhost:8080/api/auth/login', {
    username: 'votre_nom_utilisateur',
    password: 'votre_mot_de_passe'
  }, { withCredentials: true });

  // Création d'un client authentifié
  const client = createAuthenticatedClient();

  // Utilisation du client pour accéder à un endpoint protégé
  const userProfileResponse = await client.get('/users/me');
  console.log(userProfileResponse.data);
}
```

## Étapes Suivantes

Maintenant que vous comprenez comment vous authentifier auprès de l'API EasyGroup, vous pouvez explorer les fonctionnalités de l'API :

- [Guide utilisateur](/user-guide/) - Pour en savoir plus sur l'utilisation de l'API
- [Référence de l'API](/api-reference/) - Pour une documentation complète des endpoints disponibles
