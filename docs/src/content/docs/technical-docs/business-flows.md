---
title: Flux Métiers
description: Documentation des principaux flux métiers de l'API EasyGroup
---

# Flux Métiers

Cette page documente les principaux flux métiers de l'API EasyGroup, illustrant les interactions entre les différents composants du système et les acteurs externes.

## Authentification et Gestion des Utilisateurs

### Inscription d'un Utilisateur

Le diagramme suivant illustre le processus d'inscription d'un nouvel utilisateur dans le système.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant DB as Base de Données
    
    User->>Client: Saisit informations d'inscription
    Client->>API: POST /api/auth/register
    API->>Auth: Transmet la demande d'inscription
    Auth->>Auth: Valide les données
    Auth->>Auth: Hache le mot de passe (Argon2id)
    Auth->>DB: Enregistre le nouvel utilisateur
    DB->>Auth: Confirme l'enregistrement
    Auth->>Auth: Génère un token JWT
    Auth->>API: Retourne le token et les infos utilisateur
    API->>Client: Répond avec token (cookie HTTP-only) et infos utilisateur
    Client->>User: Affiche confirmation d'inscription
```

### Connexion d'un Utilisateur

Le diagramme suivant illustre le processus de connexion d'un utilisateur existant.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant DB as Base de Données
    
    User->>Client: Saisit identifiants de connexion
    Client->>API: POST /api/auth/login
    API->>Auth: Transmet la demande de connexion
    Auth->>DB: Vérifie les identifiants
    DB->>Auth: Retourne les informations utilisateur
    Auth->>Auth: Vérifie le mot de passe (Argon2id)
    Auth->>Auth: Génère un token JWT
    Auth->>API: Retourne le token et les infos utilisateur
    API->>Client: Répond avec token (cookie HTTP-only) et infos utilisateur
    Client->>User: Affiche le tableau de bord
```

## Gestion des Listes

### Création d'une Liste

Le diagramme suivant illustre le processus de création d'une nouvelle liste.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant List as Service Listes
    participant DB as Base de Données
    
    User->>Client: Saisit les informations de la liste
    Client->>API: POST /api/lists
    API->>Auth: Vérifie l'authentification
    Auth->>API: Confirme l'authentification
    API->>List: Transmet la demande de création
    List->>List: Valide les données
    List->>DB: Enregistre la nouvelle liste
    DB->>List: Confirme l'enregistrement
    List->>API: Retourne les informations de la liste
    API->>Client: Répond avec les informations de la liste
    Client->>User: Affiche la liste créée
```

### Partage d'une Liste

Le diagramme suivant illustre le processus de partage d'une liste avec un autre utilisateur.

```mermaid
sequenceDiagram
    actor User as Utilisateur (Propriétaire)
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant List as Service Listes
    participant DB as Base de Données
    
    User->>Client: Saisit l'email de l'utilisateur et les permissions
    Client->>API: POST /api/lists/{listId}/share
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>List: Transmet la demande de partage
    List->>DB: Vérifie l'existence de l'utilisateur cible
    DB->>List: Confirme l'existence de l'utilisateur
    List->>DB: Enregistre le partage de la liste
    DB->>List: Confirme l'enregistrement
    List->>API: Retourne la confirmation du partage
    API->>Client: Répond avec la confirmation du partage
    Client->>User: Affiche la confirmation du partage
```

## Gestion des Personnes

### Ajout de Personnes à une Liste

Le diagramme suivant illustre le processus d'ajout de personnes à une liste.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant List as Service Listes
    participant Person as Service Personnes
    participant DB as Base de Données
    
    User->>Client: Saisit les informations des personnes
    Client->>API: POST /api/lists/{listId}/persons
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>List: Vérifie l'existence de la liste
    List->>API: Confirme l'existence de la liste
    API->>Person: Transmet la demande d'ajout
    Person->>Person: Valide les données
    Person->>DB: Enregistre les nouvelles personnes
    DB->>Person: Confirme l'enregistrement
    Person->>API: Retourne les informations des personnes
    API->>Client: Répond avec les informations des personnes
    Client->>User: Affiche les personnes ajoutées
```

### Importation de Personnes depuis un Fichier

Le diagramme suivant illustre le processus d'importation de personnes depuis un fichier CSV ou Excel.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant List as Service Listes
    participant Person as Service Personnes
    participant DB as Base de Données
    
    User->>Client: Télécharge un fichier CSV/Excel
    Client->>Client: Valide le format du fichier
    Client->>API: POST /api/lists/{listId}/persons/import
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>List: Vérifie l'existence de la liste
    List->>API: Confirme l'existence de la liste
    API->>Person: Transmet la demande d'importation
    Person->>Person: Parse et valide les données du fichier
    Person->>DB: Enregistre les personnes importées
    DB->>Person: Confirme l'enregistrement
    Person->>API: Retourne le résumé de l'importation
    API->>Client: Répond avec le résumé de l'importation
    Client->>User: Affiche le résumé de l'importation
```

## Création et Gestion des Groupes

### Création de Groupes (Tirage)

Le diagramme suivant illustre le processus de création de groupes à partir d'une liste de personnes.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant List as Service Listes
    participant Person as Service Personnes
    participant Draw as Service Tirages
    participant Group as Service Groupes
    participant DB as Base de Données
    
    User->>Client: Configure les paramètres du tirage
    Client->>API: POST /api/lists/{listId}/draws
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>List: Vérifie l'existence de la liste
    List->>API: Confirme l'existence de la liste
    API->>Person: Récupère les personnes de la liste
    Person->>DB: Requête les personnes
    DB->>Person: Retourne les personnes
    Person->>API: Retourne les personnes
    API->>Draw: Transmet la demande de tirage
    Draw->>Draw: Valide les paramètres
    Draw->>DB: Enregistre le tirage
    DB->>Draw: Confirme l'enregistrement
    Draw->>Group: Demande la création des groupes
    Group->>Group: Applique l'algorithme d'équilibrage
    Group->>DB: Enregistre les groupes
    DB->>Group: Confirme l'enregistrement
    Group->>Draw: Retourne les groupes créés
    Draw->>API: Retourne le tirage avec les groupes
    API->>Client: Répond avec le tirage et les groupes
    Client->>User: Affiche les groupes générés
```

### Modification Manuelle des Groupes

Le diagramme suivant illustre le processus de modification manuelle des groupes après leur création.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant Group as Service Groupes
    participant DB as Base de Données
    
    User->>Client: Déplace une personne entre groupes
    Client->>API: POST /api/groups/{sourceGroupId}/persons/{personId}/move/{targetGroupId}
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>Group: Transmet la demande de déplacement
    Group->>DB: Met à jour l'appartenance aux groupes
    DB->>Group: Confirme la mise à jour
    Group->>API: Retourne les groupes mis à jour
    API->>Client: Répond avec les groupes mis à jour
    Client->>User: Affiche les groupes mis à jour
```

### Rééquilibrage des Groupes

Le diagramme suivant illustre le processus de rééquilibrage automatique des groupes.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant Draw as Service Tirages
    participant Group as Service Groupes
    participant DB as Base de Données
    
    User->>Client: Demande un rééquilibrage des groupes
    Client->>API: POST /api/draws/{drawId}/rebalance
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>Draw: Transmet la demande de rééquilibrage
    Draw->>DB: Récupère les informations du tirage
    DB->>Draw: Retourne les informations du tirage
    Draw->>Group: Demande le rééquilibrage des groupes
    Group->>DB: Récupère les personnes et les groupes
    DB->>Group: Retourne les personnes et les groupes
    Group->>Group: Applique l'algorithme d'équilibrage
    Group->>DB: Met à jour les groupes
    DB->>Group: Confirme la mise à jour
    Group->>Draw: Retourne les groupes rééquilibrés
    Draw->>API: Retourne le tirage avec les groupes rééquilibrés
    API->>Client: Répond avec le tirage et les groupes
    Client->>User: Affiche les groupes rééquilibrés
```

## Exportation des Résultats

### Exportation des Groupes en CSV

Le diagramme suivant illustre le processus d'exportation des groupes au format CSV.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant Draw as Service Tirages
    participant Group as Service Groupes
    participant DB as Base de Données
    
    User->>Client: Demande l'exportation en CSV
    Client->>API: GET /api/draws/{drawId}/export/csv
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>Draw: Transmet la demande d'exportation
    Draw->>DB: Récupère les informations du tirage
    DB->>Draw: Retourne les informations du tirage
    Draw->>Group: Demande les groupes et les personnes
    Group->>DB: Récupère les groupes et les personnes
    DB->>Group: Retourne les groupes et les personnes
    Group->>Draw: Retourne les groupes et les personnes
    Draw->>Draw: Génère le fichier CSV
    Draw->>API: Retourne le fichier CSV
    API->>Client: Répond avec le fichier CSV
    Client->>User: Télécharge le fichier CSV
```

### Exportation des Groupes en PDF

Le diagramme suivant illustre le processus d'exportation des groupes au format PDF.

```mermaid
sequenceDiagram
    actor User as Utilisateur
    participant Client as Client (Frontend)
    participant API as API Gateway
    participant Auth as Service Authentification
    participant Draw as Service Tirages
    participant Group as Service Groupes
    participant DB as Base de Données
    
    User->>Client: Demande l'exportation en PDF
    Client->>API: GET /api/draws/{drawId}/export/pdf
    API->>Auth: Vérifie l'authentification et les autorisations
    Auth->>API: Confirme l'authentification et les autorisations
    API->>Draw: Transmet la demande d'exportation
    Draw->>DB: Récupère les informations du tirage
    DB->>Draw: Retourne les informations du tirage
    Draw->>Group: Demande les groupes et les personnes
    Group->>DB: Récupère les groupes et les personnes
    DB->>Group: Retourne les groupes et les personnes
    Group->>Draw: Retourne les groupes et les personnes
    Draw->>Draw: Génère le fichier PDF
    Draw->>API: Retourne le fichier PDF
    API->>Client: Répond avec le fichier PDF
    Client->>User: Télécharge le fichier PDF
```

## Algorithme d'Équilibrage des Groupes

L'algorithme d'équilibrage des groupes est un élément central de l'application EasyGroup. Le diagramme suivant illustre le processus d'équilibrage des groupes.

```mermaid
flowchart TD
    Start([Début]) --> LoadData[Charger les personnes et les critères d'équilibrage]
    LoadData --> InitGroups[Initialiser les groupes vides]
    InitGroups --> CalculateTargetSize[Calculer la taille cible des groupes]
    CalculateTargetSize --> SortPersons[Trier les personnes selon les critères]
    SortPersons --> DistributePersons[Distribuer les personnes dans les groupes]
    DistributePersons --> EvaluateBalance[Évaluer l'équilibre des groupes]
    EvaluateBalance --> IsBalanced{Équilibre satisfaisant?}
    IsBalanced -- Oui --> SaveGroups[Sauvegarder les groupes]
    IsBalanced -- Non --> OptimizeGroups[Optimiser la distribution]
    OptimizeGroups --> EvaluateBalance
    SaveGroups --> End([Fin])
    
    subgraph "Distribution des Personnes"
    DistributePersons --> DistributeByStrategy[Distribuer selon la stratégie]
    DistributeByStrategy --> EvenDistribution[Distribution équilibrée]
    DistributeByStrategy --> DiversityDistribution[Distribution diversifiée]
    DistributeByStrategy --> ClusterDistribution[Distribution par clusters]
    end
    
    subgraph "Optimisation"
    OptimizeGroups --> SwapPersons[Échanger des personnes entre groupes]
    SwapPersons --> RecalculateBalance[Recalculer l'équilibre]
    RecalculateBalance --> IsBetter{Amélioration?}
    IsBetter -- Oui --> KeepChanges[Conserver les changements]
    IsBetter -- Non --> RevertChanges[Annuler les changements]
    KeepChanges --> ContinueOptimization{Continuer l'optimisation?}
    RevertChanges --> ContinueOptimization
    ContinueOptimization -- Oui --> SwapPersons
    ContinueOptimization -- Non --> ExitOptimization[Terminer l'optimisation]
    end
```

### Stratégies d'Équilibrage

L'API EasyGroup propose plusieurs stratégies d'équilibrage pour répondre à différents besoins :

1. **Distribution Équilibrée (DISTRIBUTE_EVENLY)**
   - Objectif : Répartir les personnes de manière à ce que chaque groupe ait une distribution similaire pour un attribut donné.
   - Exemple : Répartir les personnes de manière à ce que l'âge moyen soit similaire dans tous les groupes.

2. **Diversité Assurée (ENSURE_DIVERSITY)**
   - Objectif : Maximiser la diversité au sein de chaque groupe pour un attribut donné.
   - Exemple : Répartir les personnes de manière à ce que chaque groupe contienne une diversité de compétences.

3. **Regroupement Similaire (CLUSTER_SIMILAR)**
   - Objectif : Regrouper les personnes ayant des attributs similaires.
   - Exemple : Regrouper les personnes ayant un niveau d'expérience similaire.

## Conclusion

Les flux métiers de l'API EasyGroup sont conçus pour offrir une expérience utilisateur fluide et intuitive, tout en garantissant la sécurité et l'intégrité des données. L'architecture modulaire et l'utilisation de services spécialisés permettent une séparation claire des responsabilités et facilitent la maintenance et l'évolution du système.

L'algorithme d'équilibrage des groupes, élément central de l'application, offre une grande flexibilité grâce à ses différentes stratégies et critères configurables, permettant de répondre à une variété de besoins en matière de création de groupes équilibrés.