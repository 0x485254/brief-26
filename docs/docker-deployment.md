# Guide de Déploiement Docker pour EasyGroup

Ce guide explique comment déployer l'application EasyGroup en utilisant Docker et Docker Compose pour différents environnements (développement, pré-production et production).

## Prérequis

- Docker installé sur votre machine
- Docker Compose installé sur votre machine

## Déploiements Spécifiques à l'Environnement

Ce projet inclut trois configurations Docker Compose spécifiques à l'environnement :

1. **Développement** (`docker-compose.dev.yml`) : Pour le développement local avec rechargement à chaud et débogage
2. **Pré-production** (`docker-compose.preprod.yml`) : Pour les tests dans un environnement similaire à la production
3. **Production** (`docker-compose.prod.yml`) : Pour le déploiement en production avec haute disponibilité

## Construction et Exécution avec Docker

### Construction de l'Image Docker

Pour construire l'image Docker pour l'application EasyGroup :

```bash
docker build -t easygroup/app .
```

Vous pouvez spécifier l'environnement pendant la construction :

```bash
docker build --build-arg SPRING_PROFILES_ACTIVE=prod -t easygroup/app .
```

### Exécution du Conteneur Docker

Pour exécuter l'application dans un conteneur Docker :

```bash
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev -e DB_HOST=localhost easygroup/app
```

L'application sera accessible à l'adresse http://localhost:8080.

> **Note** : Pour une utilisation en production, assurez-vous de configurer correctement les variables d'environnement pour la connexion à la base de données et autres paramètres sensibles.

## Déploiement avec Docker Compose

Docker Compose simplifie le processus de déploiement en gérant plusieurs conteneurs et leurs configurations.

### Environnement de Développement

Pour le développement local avec rechargement à chaud et débogage :

```bash
docker-compose -f docker-compose.dev.yml up -d
```

Fonctionnalités :
- Code source monté en volumes pour le rechargement à chaud
- Débogage à distance activé sur le port 5005
- Base de données PostgreSQL avec configuration de développement
- Tous les points de terminaison Actuator exposés

### Environnement de Pré-production

Pour les tests dans un environnement similaire à la production :

```bash
docker-compose -f docker-compose.preprod.yml up -d
```

Fonctionnalités :
- Limites de ressources pour le CPU et la mémoire
- Base de données PostgreSQL avec configuration de pré-production
- Points de terminaison Actuator limités
- Support des variables d'environnement pour les identifiants de base de données

### Environnement de Production

Pour le déploiement en production :

```bash
docker-compose -f docker-compose.prod.yml up -d
```

Fonctionnalités :
- Réplication de service (2 répliques)
- Vérifications de santé pour l'application et la base de données
- Limites de ressources optimisées pour la production
- Variables d'environnement pour toutes les informations sensibles
- Configuration de volume persistant pour la base de données

### Visualisation des Logs

Pour voir les logs des conteneurs en cours d'exécution :

```bash
docker-compose -f docker-compose.[env].yml logs -f
```

Remplacez `[env]` par `dev`, `preprod`, ou `prod`.

### Arrêt de l'Application

Pour arrêter l'application :

```bash
docker-compose -f docker-compose.[env].yml down
```

## Configuration

### Variables d'Environnement

EasyGroup utilise des variables d'environnement pour configurer l'application. Un fichier `.env.example` est fourni comme modèle. Pour configurer l'application :

1. Créez un fichier `.env` à la racine du projet en copiant le fichier `.env.example` :

```bash
cp .env.example .env
```

2. Modifiez les valeurs dans le fichier `.env` selon votre environnement :

```
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=easygroup
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Application Configuration
APP_PORT=8080
APP_ENV=dev

# Security Configuration
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION_MS=86400000

# Logging Configuration
LOG_LEVEL=INFO
```

3. Les variables d'environnement peuvent également être passées directement dans la ligne de commande :

```bash
DB_PASSWORD=secure_password docker-compose -f docker-compose.prod.yml up -d
```

### Propriétés Spécifiques à l'Environnement

L'application inclut des fichiers de propriétés spécifiques à l'environnement :

- `application-dev.properties` : Paramètres de développement (journalisation détaillée, rechargement à chaud, etc.)
- `application-preprod.properties` : Paramètres de pré-production (mise en cache, connexions de base de données optimisées, etc.)
- `application-prod.properties` : Paramètres de production (journalisation minimale, optimisations de performance, etc.)

Ces fichiers sont activés automatiquement en fonction de la variable d'environnement `APP_ENV` ou `SPRING_PROFILES_ACTIVE`.

## Considérations pour la Production

Pour les déploiements en production, considérez :

1. Utilisation des secrets Docker pour les informations sensibles :
   ```bash
   docker secret create postgres_password /path/to/password/file
   ```

2. Mise en place d'une journalisation et d'une surveillance appropriées :
   - La configuration de production inclut déjà la journalisation basée sur des fichiers
   - Envisagez l'intégration avec un service d'agrégation de logs

3. Utilisation d'une plateforme d'orchestration de conteneurs comme Kubernetes pour :
   - Mise à l'échelle automatique
   - Mises à jour progressives
   - Auto-guérison
   - Équilibrage de charge
