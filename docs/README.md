# Documentation EasyGroup

Bienvenue dans la documentation du projet EasyGroup, une application pour la création intelligente de groupes d'apprenants à partir de listes partagées, en tenant compte de divers critères (âge, expérience, profils, etc.).

## Table des Matières

### Guides Utilisateurs et Fonctionnels

- [User Stories](./user-stories.md) - Récits utilisateurs décrivant les fonctionnalités du point de vue des utilisateurs
- [Flux Métiers](./business-flows.md) - Diagrammes des principaux flux métiers de l'application

### Documentation Technique

- [Guide de Démarrage](./getting-started.md) - Instructions pour démarrer avec l'application
- [Documentation API](./api-documentation.md) - Description détaillée des endpoints API disponibles
- [Modèles de Données](./database-models.md) - Description des modèles de données (MCD, MLD, MPD)
- [Guide pour les Développeurs Frontend](./frontend-developer-guide.md) - Guide pour l'intégration frontend avec l'API
- [Guide de Déploiement Docker](./docker-deployment.md) - Instructions pour le déploiement avec Docker
- [Configuration CI/CD](./ci-cd-setup.md) - Informations sur le pipeline CI/CD

## Architecture Globale

L'application EasyGroup est construite selon une architecture moderne et modulaire :

```mermaid
flowchart TB
    Client[Client Web/Mobile]
    API[API Spring Boot]
    Auth[Service Authentification]
    User[Service Utilisateurs]
    List[Service Listes]
    Person[Service Personnes]
    Draw[Service Tirages]
    Group[Service Groupes]
    DB[(Base de données PostgreSQL)]

    Client <--> API
    API --> Auth
    API --> User
    API --> List
    API --> Person
    API --> Draw
    API --> Group
    Auth --> DB
    User --> DB
    List --> DB
    Person --> DB
    Draw --> DB
    Group --> DB

    subgraph Frontend
    Client
    end

    subgraph Backend
    API
    Auth
    User
    List
    Person
    Draw
    Group
    end

    subgraph Persistence
    DB
    end
```

## Fonctionnalités Principales

1. **Gestion des Utilisateurs** - Inscription, connexion, gestion des profils
2. **Gestion des Listes** - Création, modification, suppression et partage de listes de personnes
3. **Gestion des Personnes** - Ajout, modification et suppression de personnes avec leurs caractéristiques
4. **Création de Groupes** - Génération automatique de groupes équilibrés selon divers critères
5. **Historique des Tirages** - Conservation de l'historique des tirages et des groupes créés

## Exemple de Flux Utilisateur

Le diagramme ci-dessous illustre le flux typique d'un utilisateur créant des groupes équilibrés :

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant App as Application EasyGroup
    participant API as API Backend
    participant DB as Base de Données

    User->>App: Se connecte
    App->>API: Authentifie l'utilisateur
    API->>DB: Vérifie les identifiants
    DB->>API: Confirme l'identité
    API->>App: Retourne le token JWT
    App->>User: Affiche le tableau de bord

    User->>App: Crée une liste
    App->>API: Enregistre la liste
    API->>DB: Stocke la liste
    DB->>API: Confirme la création
    API->>App: Retourne l'ID de la liste
    App->>User: Affiche la liste créée

    User->>App: Ajoute des personnes
    App->>API: Enregistre les personnes
    API->>DB: Stocke les personnes
    DB->>API: Confirme l'ajout
    API->>App: Retourne les personnes ajoutées
    App->>User: Affiche les personnes

    User->>App: Demande création de groupes
    App->>API: Demande un tirage
    API->>DB: Récupère les personnes
    DB->>API: Retourne les personnes
    API->>API: Génère des groupes équilibrés
    API->>DB: Enregistre le tirage et les groupes
    DB->>API: Confirme l'enregistrement
    API->>App: Retourne les groupes
    App->>User: Affiche les groupes générés
```

## Stack Technique

- **Backend** : Spring Boot 3.2.0, Java 17
- **Base de données** : PostgreSQL 14
- **Sécurité** : Spring Security avec Argon2id pour le hachage des mots de passe
- **Conteneurisation** : Docker et Docker Compose
- **CI/CD** : GitHub Actions

## Contribution

Pour contribuer au projet, veuillez contacter l'équipe de développement.

## Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](../LICENSE) pour plus de détails.
