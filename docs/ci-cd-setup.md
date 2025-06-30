# Configuration CI/CD

Ce projet utilise GitHub Actions pour l'intégration continue et le déploiement continu.

## Flux de Travail CI/CD

Le flux de travail CI/CD est configuré pour s'exécuter automatiquement lors des événements suivants :
- **Build & Tests** : Déclenché lors des push sur toutes les branches et des pull requests vers toutes les branches
- **Déploiement** : Déclenché uniquement lors des push sur la branche main

Le pipeline complet comprend les étapes suivantes :

1. **Build** : Construction du projet et création des artefacts
2. **Test** : Exécution des tests et vérifications de qualité du code
3. **Déploiement** : Déploiement automatique vers l'environnement de production (uniquement pour la branche main)

### Configuration du Flux de Travail

Le flux de travail est défini dans deux fichiers distincts :
- `.github/workflows/build.yml` : Pour la construction et les tests
- `.github/workflows/deploy.yml` : Pour le déploiement en production

Voici une décomposition de ces configurations :

### Workflow de Build & Tests (`.github/workflows/build.yml`)

```yaml
name: Build & Tests

on:
  push:
    branches: [ '*' ]
  pull_request:
    branches: [ '*' ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn -B package --file pom.xml -DskipTests

    - name: Upload build artifacts
      uses: actions/upload-artifact@v4
      with:
        name: app-build
        path: target/*.jar
        retention-days: 1
  test:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Download build artifacts
        uses: actions/download-artifact@v4
        with:
          name: app-build
          path: target/

      - name: Run tests
        run: mvn test

      - name: Run code quality checks
        run: mvn verify -DskipTests
```

### Workflow de Déploiement (`.github/workflows/deploy.yml`)

```yaml
name: Deploy to Production

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2

      - name: Build Docker image
        run: |
          docker build -t easygroup:latest .
          docker save easygroup:latest | gzip > easygroup.tar.gz

      - name: Install SSH key
        uses: shimataro/ssh-key-action@v2
        with:
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          known_hosts: ${{ secrets.SSH_KNOWN_HOSTS }}

      - name: Transfer files to server
        run: |
          scp easygroup.tar.gz docker-compose.prod.yml ${{ secrets.SSH_USER }}@${{ secrets.SSH_HOST }}:~/

      - name: Deploy on server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SSH_HOST }}
          username: ${{ secrets.SSH_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd ~/
            docker load < easygroup.tar.gz

            # Create .env file from secrets
            cat > .env << EOL
            DB_HOST=${{ secrets.DB_HOST }}
            DB_PORT=${{ secrets.DB_PORT }}
            DB_NAME=${{ secrets.DB_NAME }}
            DB_USERNAME=${{ secrets.DB_USERNAME }}
            DB_PASSWORD=${{ secrets.DB_PASSWORD }}
            APP_PORT=${{ secrets.APP_PORT }}
            JWT_SECRET=${{ secrets.JWT_SECRET }}
            JWT_EXPIRATION_MS=${{ secrets.JWT_EXPIRATION_MS }}
            LOG_LEVEL=${{ secrets.LOG_LEVEL }}
            CORS_URLS=${{ secrets.CORS_URLS }}
            EOL

            # Deploy with docker-compose
            docker-compose -f docker-compose.prod.yml down
            docker-compose -f docker-compose.prod.yml up -d
```

## Avantages

- **Tests Automatisés** : Chaque modification de code est automatiquement testée, garantissant que les nouvelles modifications ne cassent pas les fonctionnalités existantes.
- **Détection Précoce des Bugs** : Les problèmes sont détectés tôt dans le processus de développement.
- **Environnement de Construction Cohérent** : L'environnement de construction est cohérent pour tous les développeurs et pour la production.
- **Retour Rapide** : Les développeurs obtiennent un retour rapide sur leurs modifications.
- **Vérification Docker** : Chaque modification est vérifiée pour s'assurer qu'elle se construit avec succès en tant qu'image Docker, garantissant que la conteneurisation fonctionne correctement.

## Maintenance des Images Docker

Le projet utilise des images Docker à la fois pour la construction et l'exécution de l'application. Ces images sont spécifiées dans le `Dockerfile` :

- **Étape de Construction** : Utilise `maven:3.9.5-eclipse-temurin-17` pour compiler et empaqueter l'application
- **Étape d'Exécution** : Utilise `eclipse-temurin:17-jre-jammy` pour exécuter l'application

Lors de la mise à jour des images Docker, considérez les points suivants :

1. Vérifiez les dernières versions stables des images sur [Docker Hub](https://hub.docker.com/)
2. Préférez les versions LTS (Long Term Support) pour une utilisation en production
3. Testez soigneusement les nouvelles images avant de les déployer en production
4. Gardez les images des étapes de construction et d'exécution compatibles (par exemple, même version de Java)
5. Envisagez d'utiliser des tags de version spécifiques plutôt que `latest` pour assurer des constructions reproductibles

## Déploiement en Production

Le workflow de déploiement (`.github/workflows/deploy.yml`) est configuré pour déployer automatiquement l'application vers l'environnement de production lorsqu'un push est effectué sur la branche `main`. Ce processus comprend :

1. **Construction de l'Image Docker** : L'image Docker est construite à partir du Dockerfile du projet
2. **Transfert Sécurisé** : L'image Docker et le fichier docker-compose.prod.yml sont transférés vers le serveur de production via SSH
3. **Configuration Automatisée** : Un fichier .env est créé sur le serveur avec les variables d'environnement stockées dans les secrets GitHub
4. **Déploiement avec Docker Compose** : L'application est déployée en utilisant docker-compose.prod.yml

### Secrets Requis

Pour que le déploiement fonctionne correctement, les secrets suivants doivent être configurés dans les paramètres du dépôt GitHub :

- `SSH_PRIVATE_KEY` : Clé SSH privée pour l'accès au serveur
- `SSH_KNOWN_HOSTS` : Empreintes des hôtes connus pour la vérification SSH
- `SSH_HOST` : Adresse IP ou nom d'hôte du serveur de production
- `SSH_USER` : Nom d'utilisateur pour la connexion SSH
- Variables d'environnement de l'application : `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `APP_PORT`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `LOG_LEVEL`, `CORS_URLS`

## Améliorations Futures

Les améliorations potentielles futures du pipeline CI/CD pourraient inclure :

1. **Environnements Multiples** : Ajouter des workflows pour déployer vers des environnements de développement et de staging en plus de la production.
2. **Vérifications de Qualité du Code** : Intégrer des outils comme SonarQube pour l'analyse de la qualité du code.
3. **Analyse de Sécurité** : Ajouter une analyse des vulnérabilités de sécurité pour les dépendances et le code.
4. **Tests de Performance** : Incorporer des tests de performance pour les chemins critiques.
5. **Registre Docker** : Utiliser un registre Docker privé pour stocker et gérer les images Docker au lieu de les transférer directement.
