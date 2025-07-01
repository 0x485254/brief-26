# Flux Métiers - EasyGroup

Ce document présente les principaux flux métiers de l'application EasyGroup à l'aide de diagrammes Mermaid.

## Flux d'Authentification

Le diagramme ci-dessous illustre le processus d'authentification des utilisateurs.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Frontend
    participant AuthController
    participant UserService
    participant DB as Base de Données

    %% Inscription
    User->>Frontend: Saisit informations d'inscription
    Frontend->>AuthController: POST /api/auth/register
    AuthController->>UserService: Valide les données
    UserService->>UserService: Hache le mot de passe (Argon2id)
    UserService->>DB: Enregistre l'utilisateur
    UserService->>AuthController: Retourne les détails utilisateur
    AuthController->>Frontend: Définit cookie JWT + retourne utilisateur
    Frontend->>User: Affiche confirmation et redirige

    %% Connexion
    User->>Frontend: Saisit email et mot de passe
    Frontend->>AuthController: POST /api/auth/login
    AuthController->>UserService: Vérifie les identifiants
    UserService->>DB: Récupère l'utilisateur
    UserService->>UserService: Vérifie le mot de passe
    UserService->>AuthController: Retourne les détails utilisateur
    AuthController->>Frontend: Définit cookie JWT + retourne utilisateur
    Frontend->>User: Redirige vers tableau de bord
```

## Flux de Gestion des Listes

Le diagramme ci-dessous illustre le processus de création et de partage d'une liste.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Frontend
    participant ListController
    participant ListService
    participant DB as Base de Données

    %% Création de liste
    User->>Frontend: Crée une nouvelle liste
    Frontend->>ListController: POST /api/lists
    ListController->>ListService: Crée la liste
    ListService->>DB: Enregistre la liste
    DB->>ListService: Confirme création
    ListService->>ListController: Retourne détails de la liste
    ListController->>Frontend: Retourne la liste créée
    Frontend->>User: Affiche la liste créée

    %% Partage de liste
    User->>Frontend: Partage la liste (email)
    Frontend->>ListController: POST /api/lists/{id}/share
    ListController->>ListService: Partage la liste
    ListService->>DB: Enregistre le partage
    DB->>ListService: Confirme partage
    ListService->>ListController: Retourne statut
    ListController->>Frontend: Confirme partage
    Frontend->>User: Affiche confirmation
```

## Flux de Création de Groupes

Le diagramme ci-dessous illustre le processus de création de groupes équilibrés.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Frontend
    participant DrawController
    participant DrawService
    participant GroupService
    participant DB as Base de Données

    %% Création de tirage
    User->>Frontend: Demande création de groupes
    Frontend->>DrawController: POST /api/lists/{id}/draws
    DrawController->>DrawService: Crée un tirage
    DrawService->>DB: Récupère personnes de la liste
    DB->>DrawService: Retourne personnes
    DrawService->>GroupService: Génère groupes équilibrés
    GroupService->>DrawService: Retourne groupes
    DrawService->>DB: Enregistre tirage et groupes
    DB->>DrawService: Confirme enregistrement
    DrawService->>DrawController: Retourne tirage avec groupes
    DrawController->>Frontend: Retourne résultat
    Frontend->>User: Affiche les groupes générés
```

## Flux d'Ajout de Personnes

Le diagramme ci-dessous illustre le processus d'ajout de personnes à une liste.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Frontend
    participant PersonController
    participant PersonService
    participant DB as Base de Données

    %% Ajout manuel
    User->>Frontend: Saisit informations personne
    Frontend->>PersonController: POST /api/lists/{id}/persons
    PersonController->>PersonService: Valide et crée personne
    PersonService->>DB: Enregistre personne
    DB->>PersonService: Confirme enregistrement
    PersonService->>PersonController: Retourne personne créée
    PersonController->>Frontend: Retourne personne
    Frontend->>User: Affiche confirmation

    %% Import CSV
    User->>Frontend: Télécharge fichier CSV
    Frontend->>PersonController: POST /api/lists/{id}/persons/import
    PersonController->>PersonService: Traite le fichier CSV
    PersonService->>PersonService: Valide les données
    PersonService->>DB: Enregistre personnes en batch
    DB->>PersonService: Confirme enregistrement
    PersonService->>PersonController: Retourne résumé import
    PersonController->>Frontend: Retourne résultat
    Frontend->>User: Affiche résumé de l'import
```

## Flux d'Administration

Le diagramme ci-dessous illustre les processus d'administration.

```mermaid
sequenceDiagram
    actor Admin as Administrateur
    participant Frontend
    participant AdminController
    participant UserService
    participant DB as Base de Données

    %% Gestion utilisateurs
    Admin->>Frontend: Accède à la liste des utilisateurs
    Frontend->>AdminController: GET /api/users
    AdminController->>UserService: Récupère tous les utilisateurs
    UserService->>DB: Requête utilisateurs
    DB->>UserService: Retourne utilisateurs
    UserService->>AdminController: Retourne liste utilisateurs
    AdminController->>Frontend: Retourne utilisateurs
    Frontend->>Admin: Affiche tableau utilisateurs

    %% Activation/Désactivation
    Admin->>Frontend: Change statut utilisateur
    Frontend->>AdminController: PUT /api/users/{id}/activate
    AdminController->>UserService: Change statut
    UserService->>DB: Met à jour statut
    DB->>UserService: Confirme mise à jour
    UserService->>AdminController: Retourne utilisateur mis à jour
    AdminController->>Frontend: Retourne résultat
    Frontend->>Admin: Affiche confirmation
```

## Diagramme d'États d'une Liste

Le diagramme ci-dessous illustre les différents états possibles d'une liste.

```mermaid
stateDiagram-v2
    [*] --> Créée: Utilisateur crée liste
    Créée --> EnCours: Ajout de personnes
    EnCours --> Partagée: Partage avec d'autres utilisateurs
    EnCours --> AvecTirages: Création d'un tirage
    Partagée --> AvecTirages: Création d'un tirage
    AvecTirages --> AvecTirages: Création d'autres tirages
    AvecTirages --> [*]: Suppression
    Partagée --> [*]: Suppression
    EnCours --> [*]: Suppression
    Créée --> [*]: Suppression
```

## Diagramme d'Architecture Globale

Le diagramme ci-dessous illustre l'architecture globale de l'application.

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