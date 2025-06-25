# Projet Starter Spring Boot

Un projet de démarrage pour les applications Spring Boot avec support Docker.

## Aperçu

Ce projet fournit une base pour construire des applications Spring Boot avec :

- Points de terminaison API RESTful
- Configuration Docker et Docker Compose
- Documentation complète

## Structure du Projet

```
.
├── src/                    # Code source
│   ├── main/
│   │   ├── java/           # Fichiers source Java
│   │   └── resources/      # Fichiers de configuration
│   └── test/               # Fichiers de test
├── docs/                   # Documentation
├── Dockerfile              # Configuration Docker
├── docker-compose.yml      # Configuration Docker Compose
├── pom.xml                 # Dépendances Maven
└── README.md               # Ce fichier
```

## Démarrage Rapide

### Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- Docker et Docker Compose (optionnel)

### Exécution en Local

```bash
# Cloner le dépôt
git clone https://github.com/yourusername/spring-boot-starter.git
cd spring-boot-starter

# Exécuter l'application
mvn spring-boot:run
```

L'application sera disponible à l'adresse http://localhost:8080

### Exécution avec Docker

Le projet inclut des configurations Docker pour trois environnements :

```bash
# Pour le développement local
docker compose -f docker-compose.dev.yml up -d

# Pour les tests de pré-production
docker compose -f docker-compose.preprod.yml up -d

# Pour le déploiement en production
docker compose -f docker-compose.prod.yml up -d

# Voir les logs
docker compose -f docker-compose.[env].yml logs -f
```

Consultez le [Guide de Déploiement Docker](docs/docker-deployment.md) pour des instructions détaillées.

## Points de Terminaison Disponibles

- `GET /` - Message de bienvenue
- `GET /health` - Vérification de santé
- Points de terminaison Actuator à `/actuator/*`

## Documentation

Une documentation détaillée est disponible dans le répertoire `docs` :

- [Guide de Démarrage](docs/getting-started.md)
- [Documentation API](docs/api-documentation.md)
- [Guide de Déploiement Docker](docs/docker-deployment.md)
- [Configuration CI/CD](docs/ci-cd-setup.md)

## Personnalisation du Starter

1. Mettre à jour le nom et la description de l'application dans `pom.xml`
2. Modifier la structure des packages dans `src/main/java`
3. Ajouter vos propres contrôleurs, services et dépôts
4. Configurer des dépendances supplémentaires dans `pom.xml`
5. Mettre à jour la documentation pour refléter vos changements

## Licence

Ce projet est sous licence MIT - voir le fichier LICENSE pour plus de détails.
