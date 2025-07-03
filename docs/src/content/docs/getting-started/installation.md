---
title: Installation
description: Guide d'installation et de configuration de l'API EasyGroup
---

# Installation et Configuration

Ce guide vous explique comment installer et configurer l'API EasyGroup sur votre environnement.

## Configuration de l'Environnement

1. Clonez le dépôt GitHub :

```bash
git clone https://github.com/votre-organisation/easygroup.git
cd easygroup
```

2. Créez un fichier `.env` à la racine du projet en vous basant sur le fichier `.env.example` :

```bash
cp .env.example .env
```

3. Modifiez les valeurs dans le fichier `.env` selon votre environnement, notamment les informations de connexion à la base de données :

```env
# Configuration de la base de données
DB_HOST=localhost
DB_PORT=5432
DB_NAME=easygroup
DB_USERNAME=postgres
DB_PASSWORD=votre_mot_de_passe

# Configuration de l'application
APP_PORT=8080
APP_ENV=dev
APPLICATION_URL=http://localhost:8080

# Configuration SMTP pour les emails de vérification
SMTP_SERVER=smtp.example.com
SMTP_USERNAME=votre_email@exemple.com
SMTP_PASSWORD=votre_mot_de_passe_smtp
```

## Méthodes d'Installation

Vous pouvez exécuter l'API EasyGroup de plusieurs façons :

### Avec Maven (Développement)

1. Assurez-vous que PostgreSQL est en cours d'exécution et accessible avec les informations de connexion spécifiées dans votre fichier `.env`

2. Créez la base de données si elle n'existe pas déjà :

```sql
CREATE DATABASE easygroup;
```

3. Exécutez l'application en utilisant Maven :

```bash
mvn spring-boot:run
```

4. L'application démarrera sur le port spécifié dans votre fichier `.env` (par défaut : 8080). Vous pouvez y accéder à l'adresse http://localhost:8080

### Avec Docker Compose (Recommandé)

1. Assurez-vous que Docker et Docker Compose sont installés sur votre système.

2. Lancez l'application et la base de données avec Docker Compose :

```bash
docker-compose up -d
```

3. L'application sera accessible à l'adresse http://localhost:8080 (ou le port spécifié dans votre fichier `.env`).

4. Pour arrêter l'application :

```bash
docker-compose down
```

### Construction et Exécution du JAR

Pour construire l'application sous forme de fichier JAR :

```bash
mvn clean package
```

Le fichier JAR sera créé dans le répertoire `target`. Vous pouvez ensuite l'exécuter avec :

```bash
java -jar target/easygroup-0.0.1-SNAPSHOT.jar
```

## Configuration Avancée

L'application peut être configurée via les fichiers suivants :

- `.env` - Variables d'environnement pour le développement local et Docker
- `application.properties` - Configuration générale
- `application-dev.properties` - Configuration spécifique à l'environnement de développement
- `application-preprod.properties` - Configuration spécifique à l'environnement de pré-production
- `application-prod.properties` - Configuration spécifique à l'environnement de production

### Propriétés de Configuration Importantes

| Propriété | Description | Valeur par défaut |
|-----------|-------------|-------------------|
| `DB_HOST` | Hôte de la base de données | localhost |
| `DB_PORT` | Port de la base de données | 5432 |
| `DB_NAME` | Nom de la base de données | easygroup |
| `DB_USERNAME` | Nom d'utilisateur de la base de données | postgres |
| `DB_PASSWORD` | Mot de passe de la base de données | - |
| `APP_PORT` | Port sur lequel l'application s'exécute | 8080 |
| `APP_ENV` | Environnement d'exécution (dev, preprod, prod) | dev |
| `APPLICATION_URL` | URL de base de l'application (pour les liens dans les emails) | http://localhost:8080 |
| `SMTP_SERVER` | Serveur SMTP pour l'envoi d'emails | - |
| `SMTP_USERNAME` | Nom d'utilisateur SMTP | - |
| `SMTP_PASSWORD` | Mot de passe SMTP | - |

## Vérification de l'Installation

Pour vérifier que l'API est correctement installée et fonctionne, vous pouvez effectuer une requête HTTP vers le point de terminaison de santé :

```bash
curl http://localhost:8080/api/health
```

Vous devriez recevoir une réponse similaire à :

```json
{
  "status": "UP",
  "timestamp": "2023-07-03T12:34:56Z"
}
```

## Résolution des Problèmes Courants

### Problème de Connexion à la Base de Données

Si vous rencontrez des erreurs de connexion à la base de données, vérifiez :

1. Que PostgreSQL est en cours d'exécution
2. Que les informations de connexion dans le fichier `.env` sont correctes
3. Que la base de données existe et est accessible avec les identifiants fournis

### Problème de Port Déjà Utilisé

Si le port 8080 (ou celui que vous avez configuré) est déjà utilisé, vous pouvez :

1. Modifier la valeur de `APP_PORT` dans le fichier `.env`
2. Arrêter l'application qui utilise ce port

## Étapes Suivantes

Maintenant que vous avez installé et configuré l'API EasyGroup, vous pouvez passer aux [Premiers pas](/getting-started/first-steps) pour apprendre à utiliser l'API.
