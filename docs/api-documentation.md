# Documentation de l'API EasyGroup

Ce document fournit des informations sur les points de terminaison REST API disponibles dans l'application EasyGroup pour la création intelligente de groupes d'apprenants.

## Modèles de Données

Pour comprendre la structure des données manipulées par cette API, consultez la [documentation des modèles de données](./database-models.md) qui détaille :
- Le Modèle Conceptuel de Données (MCD)
- Le Modèle Logique de Données (MLD)
- Le Modèle Physique de Données (MPD)

## URL de Base

Lors de l'exécution en local, l'URL de base est :

```
http://localhost:8080
```

## Authentification et Sécurité

EasyGroup utilise une authentification basée sur des cookies HTTP-only contenant des tokens JWT (JSON Web Tokens) pour sécuriser l'API. Cette approche offre une meilleure protection contre les attaques XSS (Cross-Site Scripting) par rapport aux tokens stockés dans le localStorage ou sessionStorage.

### Architecture de Sécurité

L'application implémente une architecture de sécurité robuste avec les caractéristiques suivantes :

- **Cookies HTTP-only** : Les tokens JWT sont stockés dans des cookies HTTP-only, inaccessibles via JavaScript
- **Protection CSRF** : Mise en œuvre via CookieCsrfTokenRepository
- **Sessions stateless** : Aucune session n'est stockée côté serveur
- **Hachage de mot de passe Argon2id** : Algorithme de hachage résistant aux attaques par GPU et par canal auxiliaire
- **Filtres de sécurité** : Validation des tokens JWT à chaque requête

### Points de Terminaison d'Authentification

#### Inscription d'un Utilisateur

Permet de créer un nouveau compte utilisateur et de définir un cookie HTTP-only contenant un token JWT.

- **URL** : `/api/auth/register`
- **Méthode** : `POST`
- **Authentification requise** : Non
- **Permissions requises** : Aucune
- **Corps de la Requête** :

```json
{
  "email": "utilisateur@exemple.com",
  "password": "motdepasse123",
  "firstName": "Prénom",
  "lastName": "Nom"
}
```

#### Réponse en Cas de Succès

- **Code** : 201 Created
- **Cookies définis** : `JWT_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` (HTTP-only)
- **Exemple de contenu** :

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "utilisateur@exemple.com",
  "firstName": "Prénom",
  "lastName": "Nom",
  "role": "USER",
  "isActivated": true
}
```

#### Connexion d'un Utilisateur

Permet à un utilisateur de se connecter et de recevoir un cookie HTTP-only contenant un token JWT.

- **URL** : `/api/auth/login`
- **Méthode** : `POST`
- **Authentification requise** : Non
- **Permissions requises** : Aucune
- **Corps de la Requête** :

```json
{
  "email": "utilisateur@exemple.com",
  "password": "motdepasse123"
}
```

#### Réponse en Cas de Succès

- **Code** : 200 OK
- **Cookies définis** : `JWT_TOKEN=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` (HTTP-only)
- **Exemple de contenu** :

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "utilisateur@exemple.com",
  "firstName": "Prénom",
  "lastName": "Nom",
  "role": "USER",
  "isActivated": true
}
```

#### Déconnexion d'un Utilisateur

Permet à un utilisateur de se déconnecter en supprimant le cookie d'authentification.

- **URL** : `/api/auth/logout`
- **Méthode** : `POST`
- **Authentification requise** : Non
- **Permissions requises** : Aucune

#### Réponse en Cas de Succès

- **Code** : 200 OK
- **Cookies supprimés** : `JWT_TOKEN` (valeur vide, Max-Age=0)

### Flux d'Authentification

1. **Inscription** :
   - L'utilisateur envoie ses informations d'inscription
   - Le serveur valide les données et crée un nouvel utilisateur
   - Le mot de passe est haché avec Argon2id avant d'être stocké
   - Un token JWT est généré et placé dans un cookie HTTP-only
   - Les détails de l'utilisateur sont renvoyés dans la réponse (sans le token)

2. **Connexion** :
   - L'utilisateur envoie ses identifiants
   - Le serveur authentifie l'utilisateur en vérifiant le mot de passe
   - Un token JWT est généré et placé dans un cookie HTTP-only
   - Les détails de l'utilisateur sont renvoyés dans la réponse (sans le token)

3. **Requêtes authentifiées** :
   - Le cookie contenant le token JWT est automatiquement envoyé avec chaque requête
   - Le filtre CookieAuthenticationFilter extrait et valide le token
   - Si le token est valide, l'utilisateur est authentifié pour la requête
   - Si le token est invalide ou absent, la requête est rejetée pour les endpoints protégés

4. **Déconnexion** :
   - L'utilisateur envoie une requête de déconnexion
   - Le serveur supprime le cookie d'authentification en définissant sa durée de vie à 0

### Configuration des Cookies

Les cookies d'authentification sont configurés avec les attributs de sécurité suivants :

- **HttpOnly** : Empêche l'accès au cookie via JavaScript
- **Secure** : Le cookie n'est envoyé que sur des connexions HTTPS (en production)
- **SameSite=Strict** : Le cookie n'est pas envoyé lors des requêtes cross-site
- **Path=/** : Le cookie est disponible pour tout le domaine
- **Expiration** : 24 heures par défaut

### Hachage de Mot de Passe Argon2id

EasyGroup utilise l'algorithme Argon2id pour le hachage des mots de passe, avec les paramètres suivants :

- **Longueur du sel** : 16 octets
- **Longueur du hachage** : 32 octets
- **Itérations** : 3
- **Mémoire** : 64 Mo
- **Parallélisme** : 4 threads

Cette configuration offre une excellente protection contre les attaques par force brute, y compris celles utilisant des GPU ou des ASIC.

## Points de Terminaison de Gestion des Listes

### Création d'une Liste

Permet de créer une nouvelle liste de personnes.

- **URL** : `/api/lists`
- **Méthode** : `POST`
- **Authentification requise** : Oui
- **Permissions requises** : Utilisateur authentifié
- **Corps de la Requête** :

```json
{
  "name": "Ma Liste"
}
```

#### Réponse en Cas de Succès

- **Code** : 201 Created
- **Exemple de contenu** :

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Ma Liste",
  "isShared": false
}
```

### Récupération des Listes d'un Utilisateur

Permet de récupérer toutes les listes appartenant à l'utilisateur connecté.

- **URL** : `/api/lists`
- **Méthode** : `GET`
- **Authentification requise** : Oui
- **Permissions requises** : Utilisateur authentifié

#### Réponse en Cas de Succès

- **Code** : 200 OK
- **Exemple de contenu** :

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Ma Liste",
    "isShared": false
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Autre Liste",
    "isShared": true
  }
]
```

## Points de Terminaison de Gestion des Personnes

### Ajout d'une Personne à une Liste

Permet d'ajouter une nouvelle personne à une liste existante.

- **URL** : `/api/lists/{listId}/persons`
- **Méthode** : `POST`
- **Authentification requise** : Oui
- **Permissions requises** : Propriétaire de la liste ou utilisateur avec accès partagé
- **Corps de la Requête** :

```json
{
  "name": "Jean Dupont",
  "gender": "MALE",
  "age": 25,
  "frenchLevel": 3,
  "oldDwwm": false,
  "techLevel": 2,
  "profile": "A_LAISE"
}
```

#### Réponse en Cas de Succès

- **Code** : 201 Created
- **Exemple de contenu** :

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Jean Dupont",
  "gender": "MALE",
  "age": 25,
  "frenchLevel": 3,
  "oldDwwm": false,
  "techLevel": 2,
  "profile": "A_LAISE"
}
```

### Récupération des Personnes d'une Liste

Permet de récupérer toutes les personnes d'une liste spécifique.

- **URL** : `/api/lists/{listId}/persons`
- **Méthode** : `GET`
- **Authentification requise** : Oui
- **Permissions requises** : Propriétaire de la liste ou utilisateur avec accès partagé

#### Réponse en Cas de Succès

- **Code** : 200 OK
- **Exemple de contenu** :

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Jean Dupont",
    "gender": "MALE",
    "age": 25,
    "frenchLevel": 3,
    "oldDwwm": false,
    "techLevel": 2,
    "profile": "A_LAISE"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Marie Martin",
    "gender": "FEMALE",
    "age": 30,
    "frenchLevel": 4,
    "oldDwwm": true,
    "techLevel": 3,
    "profile": "RESERVE"
  }
]
```

## Points de Terminaison de Gestion des Tirages et Groupes

### Création d'un Tirage

Permet de créer un nouveau tirage à partir d'une liste existante.

- **URL** : `/api/lists/{listId}/draws`
- **Méthode** : `POST`
- **Authentification requise** : Oui
- **Permissions requises** : Propriétaire de la liste ou utilisateur avec accès partagé
- **Corps de la Requête** :

```json
{
  "title": "Tirage du 15 janvier",
  "numberOfGroups": 3
}
```

#### Réponse en Cas de Succès

- **Code** : 201 Created
- **Exemple de contenu** :

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Tirage du 15 janvier",
  "createdAt": "2023-01-15T10:30:00",
  "groups": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Group 1"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "name": "Group 2"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440003",
      "name": "Group 3"
    }
  ]
}
```

### Récupération des Personnes d'un Groupe

Permet de récupérer toutes les personnes d'un groupe spécifique.

- **URL** : `/api/draws/{drawId}/groups/{groupId}/persons`
- **Méthode** : `GET`
- **Authentification requise** : Oui
- **Permissions requises** : Propriétaire de la liste ou utilisateur avec accès partagé

#### Réponse en Cas de Succès

- **Code** : 200 OK
- **Exemple de contenu** :

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Jean Dupont",
    "gender": "MALE",
    "age": 25,
    "frenchLevel": 3,
    "oldDwwm": false,
    "techLevel": 2,
    "profile": "A_LAISE"
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Marie Martin",
    "gender": "FEMALE",
    "age": 30,
    "frenchLevel": 4,
    "oldDwwm": true,
    "techLevel": 3,
    "profile": "RESERVE"
  }
]
```

## Points de Terminaison Actuator

Spring Boot Actuator fournit plusieurs points de terminaison pour la surveillance et la gestion de l'application. Les suivants sont activés dans EasyGroup :

- **Health** : `/actuator/health` - Affiche l'état de santé de l'application et de ses dépendances (base de données, etc.)
- **Info** : `/actuator/info` - Affiche les informations de l'application (version, environnement, etc.)
- **Metrics** : `/actuator/metrics` - Affiche diverses métriques de l'application (mémoire, CPU, requêtes HTTP, etc.)

## Réponses d'Erreur

### 400 Requête Incorrecte

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for object='authRequest'",
  "path": "/api/auth/register"
}
```

### 401 Non Autorisé

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication credentials not valid",
  "path": "/api/lists"
}
```

### 403 Interdit

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied to this resource",
  "path": "/api/lists/550e8400-e29b-41d4-a716-446655440000"
}
```

### 404 Non Trouvé

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "List not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/lists/550e8400-e29b-41d4-a716-446655440000"
}
```

### 500 Erreur Interne du Serveur

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/lists"
}
```
