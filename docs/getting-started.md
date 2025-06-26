# Démarrage avec EasyGroup

Ce guide vous aidera à démarrer avec l'application EasyGroup pour la création intelligente de groupes d'apprenants.

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- PostgreSQL 14 ou supérieur
- Docker et Docker Compose (recommandé pour le déploiement simplifié)

## Configuration de l'Environnement

1. Créez un fichier `.env` à la racine du projet en vous basant sur le fichier `.env.example` :

```bash
cp .env.example .env
```

2. Modifiez les valeurs dans le fichier `.env` selon votre environnement, notamment les informations de connexion à la base de données.

## Exécution en Local

### Avec Maven

1. Assurez-vous que PostgreSQL est en cours d'exécution et accessible avec les informations de connexion spécifiées dans votre fichier `.env`
2. Exécutez l'application en utilisant Maven :

```bash
mvn spring-boot:run
```

3. L'application démarrera sur le port 8080. Vous pouvez y accéder à l'adresse http://localhost:8080

### Avec Docker Compose (Recommandé)

1. Assurez-vous que Docker et Docker Compose sont installés
2. Lancez l'application et la base de données avec Docker Compose :

```bash
docker-compose up -d
```

3. L'application sera accessible à l'adresse http://localhost:8080

## Construction de l'Application

Pour construire l'application sous forme de fichier JAR :

```bash
mvn clean package
```

Le fichier JAR sera créé dans le répertoire `target`.

## Exécution du JAR

```bash
java -jar target/easygroup-0.0.1-SNAPSHOT.jar
```

## Points de Terminaison Disponibles

### Authentification par JWT (Token)
- `POST /api/auth/register` - Enregistrement d'un nouvel utilisateur
- `POST /api/auth/login` - Authentification d'un utilisateur

### Authentification par Cookie (HTTP-Only)
- `POST /api/auth/cookie/register` - Enregistrement d'un nouvel utilisateur avec cookie
- `POST /api/auth/cookie/login` - Authentification d'un utilisateur avec cookie
- `POST /api/auth/cookie/logout` - Déconnexion d'un utilisateur (suppression du cookie)

Pour plus de détails, consultez la [documentation d'authentification](./authentication.md) et la [documentation d'authentification par cookie](./cookie-authentication.md).

### Autres Points de Terminaison
- Gestion des listes et des groupes (voir la [documentation API complète](./api-documentation.md))

## Configuration

L'application peut être configurée via les fichiers suivants :

- `.env` - Variables d'environnement pour le développement local et Docker
- `application.properties` - Configuration générale
- `application-dev.properties` - Configuration spécifique à l'environnement de développement
- `application-preprod.properties` - Configuration spécifique à l'environnement de pré-production
- `application-prod.properties` - Configuration spécifique à l'environnement de production

Propriétés clés :

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` - Informations de connexion à la base de données
- `APP_PORT` - Le port sur lequel l'application s'exécute (par défaut : 8080)
- `APP_ENV` - L'environnement d'exécution (dev, preprod, prod)
