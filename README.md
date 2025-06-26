# EasyGroup - Création Intelligente de Groupes d'Apprenants

Application Spring Boot pour la création intelligente de groupes d'apprenants à partir de listes partagées, en tenant compte de divers critères (âge, expérience, profils, etc.).

## Aperçu

EasyGroup est une application qui permet de :

- Créer et gérer des listes de personnes avec leurs caractéristiques
- Partager ces listes avec d'autres utilisateurs
- Générer automatiquement des groupes équilibrés en fonction de divers critères
- Visualiser et exporter les groupes créés

## Structure du Projet

```
.
├── src/                    # Code source
│   ├── main/
│   │   ├── java/           # Fichiers source Java
│   │   │   └── com/easygroup/
│   │   │       ├── config/         # Configuration (sécurité, etc.)
│   │   │       ├── controller/     # Contrôleurs REST
│   │   │       ├── dto/            # Objets de transfert de données
│   │   │       ├── entity/         # Entités JPA
│   │   │       ├── repository/     # Repositories Spring Data
│   │   │       └── service/        # Services métier
│   │   └── resources/      # Fichiers de configuration
│   └── test/               # Fichiers de test
├── docs/                   # Documentation
├── Dockerfile              # Configuration Docker
├── docker-compose.yml      # Configuration Docker principale
├── docker-compose.dev.yml  # Configuration pour développement
├── docker-compose.preprod.yml # Configuration pour pré-production
├── docker-compose.prod.yml # Configuration pour production
├── .env.example            # Exemple de variables d'environnement
├── pom.xml                 # Dépendances Maven
└── README.md               # Ce fichier
```

## Démarrage Rapide

### Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- PostgreSQL 14 ou supérieur
- Docker et Docker Compose (recommandé)

### Configuration

1. Créez un fichier `.env` à la racine du projet en vous basant sur le fichier `.env.example` :

```bash
cp .env.example .env
```

2. Modifiez les valeurs dans le fichier `.env` selon votre environnement.

### Exécution en Local

```bash
# Cloner le dépôt
git clone https://github.com/yourusername/easygroup.git
cd easygroup

# Exécuter l'application
mvn spring-boot:run
```

L'application sera disponible à l'adresse http://localhost:8080

### Exécution avec Docker (Recommandé)

```bash
# Démarrer l'application et la base de données
docker-compose up -d

# Voir les logs
docker-compose logs -f
```

Pour des environnements spécifiques :

```bash
# Pour le développement
docker-compose -f docker-compose.dev.yml up -d

# Pour la pré-production
docker-compose -f docker-compose.preprod.yml up -d

# Pour la production
docker-compose -f docker-compose.prod.yml up -d
```

Consultez le [Guide de Déploiement Docker](docs/docker-deployment.md) pour des instructions détaillées.

## Points de Terminaison Disponibles

### Authentification
- `POST /api/auth/register` - Enregistrement d'un nouvel utilisateur
- `POST /api/auth/login` - Authentification d'un utilisateur

### Listes
- Endpoints pour la gestion des listes de personnes (création, modification, suppression, partage)

### Personnes
- Endpoints pour la gestion des personnes dans les listes

### Tirages et Groupes
- Endpoints pour la création et la gestion des tirages et des groupes

### Monitoring
- Points de terminaison Actuator à `/actuator/*` pour la surveillance de l'application

## Documentation

Une documentation détaillée est disponible dans le répertoire `docs` :

- [Guide de Démarrage](docs/getting-started.md) - Instructions pour démarrer avec l'application
- [Documentation API](docs/api-documentation.md) - Description des endpoints API disponibles
- [Guide de Déploiement Docker](docs/docker-deployment.md) - Instructions pour le déploiement avec Docker
- [Configuration CI/CD](docs/ci-cd-setup.md) - Informations sur le pipeline CI/CD

## Fonctionnalités Principales

1. **Gestion des Utilisateurs** - Inscription, connexion, gestion des profils
2. **Gestion des Listes** - Création, modification, suppression et partage de listes de personnes
3. **Gestion des Personnes** - Ajout, modification et suppression de personnes avec leurs caractéristiques
4. **Création de Groupes** - Génération automatique de groupes équilibrés selon divers critères
5. **Historique des Tirages** - Conservation de l'historique des tirages et des groupes créés

## Architecture Technique

- **Backend** : Spring Boot 3.2.0, Java 17
- **Base de données** : PostgreSQL 14
- **Sécurité** : Spring Security avec Argon2id pour le hachage des mots de passe
- **Conteneurisation** : Docker et Docker Compose
- **CI/CD** : GitHub Actions

## Licence

Ce projet est sous licence MIT - voir le fichier LICENSE pour plus de détails.
