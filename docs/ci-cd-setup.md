# Configuration CI/CD

Ce projet utilise GitHub Actions pour l'intégration continue et le déploiement continu.

## Flux de Travail CI

Le flux de travail CI est configuré pour s'exécuter automatiquement à chaque push sur n'importe quelle branche et sur les pull requests. Il effectue les étapes suivantes :

1. Récupère le code
2. Configure un environnement Java 17
3. Construit le projet avec Maven
4. Exécute tous les tests
5. Configure Docker Buildx
6. Construit une image Docker à partir du Dockerfile du projet

### Configuration du Flux de Travail

Le flux de travail est défini dans `.github/workflows/ci.yml`. Voici une décomposition de la configuration :

```yaml
name: CI

on:
  push:
    branches: [ '*' ]  # S'exécute lors d'un push sur n'importe quelle branche
  pull_request:
    branches: [ '*' ]  # S'exécute lors des pull requests sur n'importe quelle branche

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn -B package --file pom.xml

    - name: Run tests
      run: mvn test

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Build Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: false
        tags: spring-boot-starter:latest
        cache-from: type=gha
        cache-to: type=gha,mode=max
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

## Améliorations Futures

Les améliorations potentielles futures du pipeline CI/CD pourraient inclure :

1. **Déploiements Automatisés** : Déployer automatiquement vers les environnements de développement, de staging ou de production en fonction de la branche ou du tag.
2. **Vérifications de Qualité du Code** : Intégrer des outils comme SonarQube pour l'analyse de la qualité du code.
3. **Analyse de Sécurité** : Ajouter une analyse des vulnérabilités de sécurité.
4. **Tests de Performance** : Incorporer des tests de performance pour les chemins critiques.
5. **Publication d'Images Docker** : Pousser automatiquement les images Docker vers un registre de conteneurs.
