# Configuration CI/CD

Ce projet utilise GitHub Actions pour l'intégration continue et le déploiement continu.

## Flux de Travail CI/CD

Le flux de travail CI/CD est configuré pour s'exécuter automatiquement lors des événements suivants :
- Push sur les branches `main` et `dev`
- Push de tags commençant par `v` (ex: v1.0.0)
- Pull requests vers les branches `main` et `dev`

Le pipeline complet comprend les étapes suivantes :

1. **Test** : Exécution des tests et vérifications de qualité du code
2. **Build** : Construction du projet et de l'image Docker
3. **Déploiement** : Déploiement automatique vers les environnements appropriés en fonction du contexte

### Configuration du Flux de Travail

Le flux de travail est défini dans `.github/workflows/ci.yml`. Voici une décomposition de la configuration :

```yaml
name: CI/CD

on:
  push:
    branches: [ 'main', 'dev' ]
    tags: [ 'v*' ]
  pull_request:
    branches: [ 'main', 'dev' ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Run tests
      run: mvn test

    - name: Run code quality checks
      run: mvn verify -DskipTests

  build:
    runs-on: ubuntu-latest
    needs: test
    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn -B package --file pom.xml -DskipTests

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Login to Docker Hub
      if: github.event_name != 'pull_request'
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKERHUB_USERNAME }}
        password: ${{ secrets.DOCKERHUB_TOKEN }}

    - name: Extract metadata for Docker
      id: meta
      uses: docker/metadata-action@v4
      with:
        images: easygroup/app
        tags: |
          type=ref,event=branch
          type=ref,event=pr
          type=semver,pattern={{version}}
          type=semver,pattern={{major}}.{{minor}}
          type=sha,format=short

    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: ${{ github.event_name != 'pull_request' }}
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max

  deploy-dev:
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/develop'
    environment: development
    steps:
    - uses: actions/checkout@v3

    - name: Deploy to development environment
      run: |
        echo "Deploying to development environment"
        # Add deployment commands here

  deploy-preprod:
    runs-on: ubuntu-latest
    needs: build
    if: startsWith(github.ref, 'refs/tags/v')
    environment: pre-production
    steps:
    - uses: actions/checkout@v3

    - name: Deploy to pre-production environment
      run: |
        echo "Deploying to pre-production environment"
        # Add deployment commands here

  deploy-prod:
    runs-on: ubuntu-latest
    needs: deploy-preprod
    if: startsWith(github.ref, 'refs/tags/v')
    environment: production
    steps:
    - uses: actions/checkout@v3

    - name: Deploy to production environment
      run: |
        echo "Deploying to production environment"
        # Add deployment commands here
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

## Note sur le Registre Docker

Ce projet ne publie pas d'images Docker vers un registre externe. Les images Docker sont construites localement et utilisées directement pour le déploiement. Cette approche simplifie la configuration et évite la dépendance à un service externe.

## Améliorations Futures

Les améliorations potentielles futures du pipeline CI/CD pourraient inclure :

1. **Déploiements Automatisés** : Déployer automatiquement vers les environnements de développement, de staging ou de production en fonction de la branche ou du tag.
2. **Vérifications de Qualité du Code** : Intégrer des outils comme SonarQube pour l'analyse de la qualité du code.
3. **Analyse de Sécurité** : Ajouter une analyse des vulnérabilités de sécurité.
4. **Tests de Performance** : Incorporer des tests de performance pour les chemins critiques.
