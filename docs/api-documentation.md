# Documentation de l'API

Ce document fournit des informations sur les points de terminaison REST API disponibles dans cette application starter Spring Boot.

## URL de Base

Lors de l'exécution en local, l'URL de base est :

```
http://localhost:8080
```

## Points de Terminaison

### Point de Terminaison d'Accueil

Renvoie un message de bienvenue et l'état actuel de l'application.

- **URL** : `/`
- **Méthode** : `GET`
- **Authentification requise** : Non
- **Permissions requises** : Aucune

#### Réponse en Cas de Succès

- **Code** : 200 OK
- **Exemple de contenu** :

```json
{
  "message": "Welcome to Spring Boot Starter API",
  "status": "running"
}
```

### Point de Terminaison de Vérification de Santé

Renvoie l'état de santé actuel de l'application.

- **URL** : `/health`
- **Méthode** : `GET`
- **Authentification requise** : Non
- **Permissions requises** : Aucune

#### Réponse en Cas de Succès

- **Code** : 200 OK
- **Exemple de contenu** :

```json
{
  "status": "UP"
}
```

## Points de Terminaison Actuator

Spring Boot Actuator fournit plusieurs points de terminaison prêts pour la production. Les suivants sont activés :

- **Health** : `/actuator/health` - Affiche les informations de santé de l'application
- **Info** : `/actuator/info` - Affiche les informations de l'application
- **Metrics** : `/actuator/metrics` - Affiche les informations de métriques pour l'application

## Réponses d'Erreur

### 404 Non Trouvé

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/non-existent-path"
}
```

### 500 Erreur Interne du Serveur

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/some-path"
}
```
