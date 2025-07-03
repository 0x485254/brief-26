---
title: Authentification
description: Documentation des endpoints d'authentification de l'API EasyGroup
---

# Endpoints d'Authentification

Cette page documente les endpoints d'authentification disponibles dans l'API EasyGroup.

## Architecture de Sécurité

L'API EasyGroup implémente une architecture de sécurité robuste avec les caractéristiques suivantes :

- **Tokens JWT dans des Cookies HTTP-only** : Méthode sécurisée pour l'authentification et l'autorisation
- **Sessions stateless** : Aucune session n'est stockée côté serveur
- **Hachage de mot de passe Argon2id** : Algorithme de hachage résistant aux attaques
- **Filtres de sécurité** : Validation des tokens JWT à chaque requête

## Authentification par Cookie HTTP-Only

L'API EasyGroup utilise exclusivement des cookies HTTP-only pour l'authentification. Cette approche offre une meilleure sécurité en protégeant le token JWT contre les attaques XSS.

### Inscription

Permet de créer un nouveau compte utilisateur et de recevoir un email de validation.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/auth/register</span>
</div>

#### Corps de la Requête

```json
{
  "email": "utilisateur@exemple.com",
  "password": "motdepasse123",
  "firstName": "Jean",
  "lastName": "Dupont"
}
```

#### Réponse en Cas de Succès

**Code** : 201 Created

```json
true
```

Après l'inscription, un email de validation est envoyé à l'adresse fournie. L'utilisateur doit cliquer sur le lien de validation pour activer son compte.

### Connexion

Permet à un utilisateur de se connecter et de recevoir un cookie HTTP-only contenant un token JWT.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/auth/login</span>
</div>

#### Corps de la Requête

```json
{
  "email": "utilisateur@exemple.com",
  "password": "motdepasse123"
}
```

#### Réponse en Cas de Succès

**Code** : 200 OK

**Cookies définis** : Un cookie HTTP-only contenant le token JWT est automatiquement défini

```json
{
  "id": 1,
  "email": "utilisateur@exemple.com",
  "firstName": "Jean",
  "lastName": "Dupont",
  "token": null,
  "role": "USER",
  "isActivated": true
}
```

### Déconnexion

Permet à un utilisateur de se déconnecter en supprimant le cookie d'authentification.

<div class="api-endpoint">
  <span class="method">POST</span>
  <span class="path">/api/auth/logout</span>
</div>

#### Réponse en Cas de Succès

**Code** : 200 OK

**Cookies supprimés** : Le cookie HTTP-only contenant le token JWT est supprimé

```
(Réponse vide)
```

### Vérification d'Email

Permet de vérifier l'email d'un utilisateur après l'inscription.

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/auth/verify?token={token}</span>
</div>

#### Réponse en Cas de Succès

**Code** : 302 Found

L'utilisateur est redirigé vers la page de connexion après la vérification réussie.

## Protection CSRF

Pour les requêtes non-GET avec l'authentification par cookie, vous devez inclure un token CSRF dans l'en-tête `X-CSRF-TOKEN`.

### Obtention du Token CSRF

<div class="api-endpoint">
  <span class="method">GET</span>
  <span class="path">/api/auth/csrf-token</span>
</div>

#### Réponse en Cas de Succès

**Code** : 200 OK

```json
{
  "token": "abcdef123456..."
}
```

## Structure du Token JWT

Les tokens JWT émis par l'API EasyGroup contiennent les informations suivantes :

```json
{
  "userId": "1",
  "role": "USER",
  "sub": "utilisateur@exemple.com",
  "iat": 1688379296,
  "exp": 1688382896
}
```

- `userId` : ID de l'utilisateur
- `role` : Rôle de l'utilisateur
- `sub` : Email de l'utilisateur (sujet du token)
- `iat` : Timestamp de création du token
- `exp` : Timestamp d'expiration du token

## Configuration des Cookies

Les cookies d'authentification sont configurés avec les attributs de sécurité suivants :

- **HttpOnly** : Empêche l'accès au cookie via JavaScript
- **Secure** : Le cookie n'est envoyé que sur des connexions HTTPS (en production)
- **SameSite=Strict** : Le cookie n'est pas envoyé lors des requêtes cross-site
- **Path=/** : Le cookie est disponible pour tout le domaine
- **Expiration** : 24 heures par défaut

## Erreurs Courantes

### Identifiants Invalides

**Code** : 401 Unauthorized

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Nom d'utilisateur ou mot de passe incorrect",
  "path": "/api/auth/login"
}
```

### Token Expiré

**Code** : 401 Unauthorized

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT expiré",
  "path": "/api/users/me"
}
```

### Token Invalide

**Code** : 401 Unauthorized

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT invalide",
  "path": "/api/users/me"
}
```

### Validation des Données d'Inscription

**Code** : 422 Unprocessable Entity

```json
{
  "timestamp": "2023-07-03T12:34:56Z",
  "status": 422,
  "error": "Unprocessable Entity",
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

## Bonnes Pratiques

### Stockage Sécurisé des Tokens JWT

- **Ne stockez pas** le token JWT dans le localStorage ou le sessionStorage, car ils sont vulnérables aux attaques XSS
- Utilisez plutôt un cookie HttpOnly avec l'authentification par cookie, ou stockez le token dans la mémoire de l'application

### Gestion de l'Expiration des Tokens

- Rafraîchissez le token avant son expiration pour éviter les interruptions de service
- Implémentez une logique de déconnexion automatique après une période d'inactivité

### Sécurité des Mots de Passe

- Utilisez des mots de passe forts (au moins 8 caractères, avec des lettres majuscules et minuscules, des chiffres et des caractères spéciaux)
- Ne réutilisez pas les mots de passe entre différents services
