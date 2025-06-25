# Guide de Déploiement Docker

Ce guide explique comment déployer l'application Spring Boot en utilisant Docker et Docker Compose pour différents environnements (développement, pré-production et production).

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

Pour construire l'image Docker pour l'application :

```bash
docker build -t spring-boot-starter .
```

Vous pouvez spécifier l'environnement pendant la construction :

```bash
docker build --build-arg SPRING_PROFILES_ACTIVE=prod -t spring-boot-starter .
```

### Exécution du Conteneur Docker

Pour exécuter l'application dans un conteneur Docker :

```bash
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev spring-boot-starter
```

L'application sera accessible à l'adresse http://localhost:8080.

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

Chaque environnement a son propre ensemble de variables d'environnement par défaut dans les fichiers Docker Compose. Vous pouvez les remplacer par :

1. Création d'un fichier `.env` à la racine du projet
2. Passage de variables directement dans la ligne de commande :

```bash
POSTGRES_PASSWORD=secure_password docker-compose -f docker-compose.prod.yml up -d
```

### Propriétés Spécifiques à l'Environnement

L'application inclut des fichiers de propriétés spécifiques à l'environnement :

- `application-dev.properties` : Paramètres de développement
- `application-preprod.properties` : Paramètres de pré-production
- `application-prod.properties` : Paramètres de production

Ces fichiers sont activés automatiquement en fonction de la variable d'environnement `SPRING_PROFILES_ACTIVE`.

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
