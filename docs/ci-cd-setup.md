# Configuration CI/CD

Ce projet utilise GitHub Actions pour l'intégration continue et le déploiement continu.

## Flux de Travail CI/CD

Le flux de travail CI/CD est configuré pour s'exécuter automatiquement lors des événements suivants :
- Push sur toutes les branches
- Pull requests vers toutes les branches

Le pipeline complet comprend les étapes suivantes :

1. **Test** : Exécution des tests et vérifications de qualité du code
2. **Build** : Construction du projet et de l'image Docker
3. **Déploiement** : Déploiement automatique vers les environnements appropriés en fonction du contexte

### Configuration du Flux de Travail

Le flux de travail est défini dans un fichier unique : `.github/workflows/build.yml`. Voici une décomposition de la configuration :

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
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Download build artifacts
        uses: dawidd6/action-download-artifact@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          workflow: Build
          name: app-build
          path: target/
          workflow_conclusion: success

      - name: Run tests
        run: mvn test

      - name: Run code quality checks
        run: mvn verify -DskipTests
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
