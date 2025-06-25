# Démarrage avec Spring Boot Starter

Ce guide vous aidera à démarrer avec ce projet starter Spring Boot.

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- Docker et Docker Compose (optionnel, pour le déploiement conteneurisé)

## Exécution en Local

1. Cloner le dépôt
2. Naviguer vers le répertoire du projet
3. Exécuter l'application en utilisant Maven :

```bash
mvn spring-boot:run
```

4. L'application démarrera sur le port 8080. Vous pouvez y accéder à l'adresse http://localhost:8080

## Construction de l'Application

Pour construire l'application sous forme de fichier JAR :

```bash
mvn clean package
```

Le fichier JAR sera créé dans le répertoire `target`.

## Exécution du JAR

```bash
java -jar target/spring-boot-starter-0.0.1-SNAPSHOT.jar
```

## Points de Terminaison Disponibles

- `GET /` - Message de bienvenue
- `GET /health` - Point de terminaison de vérification de santé

## Configuration

L'application peut être configurée via le fichier `application.properties` situé dans `src/main/resources`.

Propriétés clés :

- `server.port` - Le port sur lequel l'application s'exécute (par défaut : 8080)
- `spring.application.name` - Le nom de l'application
