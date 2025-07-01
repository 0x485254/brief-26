# User Stories - EasyGroup

Ce document présente les user stories (récits utilisateurs) pour l'application EasyGroup, organisées par fonctionnalité principale.

## Authentification et Gestion des Utilisateurs

### En tant qu'utilisateur non authentifié, je veux...

1. M'inscrire sur la plateforme afin de créer un compte
   - **Critères d'acceptation**:
     - Je peux fournir mon email, mot de passe, prénom et nom
     - Je reçois une confirmation de création de compte
     - Je suis automatiquement connecté après inscription

2. Me connecter à mon compte existant afin d'accéder à mes listes
   - **Critères d'acceptation**:
     - Je peux saisir mon email et mot de passe
     - Je suis redirigé vers mon tableau de bord après connexion
     - Un cookie sécurisé est créé pour maintenir ma session

3. Réinitialiser mon mot de passe si je l'ai oublié
   - **Critères d'acceptation**:
     - Je peux demander une réinitialisation via mon email
     - Je reçois un lien sécurisé pour créer un nouveau mot de passe
     - Je peux définir un nouveau mot de passe et me connecter avec celui-ci

### En tant qu'utilisateur authentifié, je veux...

4. Consulter et modifier mon profil utilisateur
   - **Critères d'acceptation**:
     - Je peux voir mes informations personnelles
     - Je peux modifier mon prénom et nom
     - Je peux changer mon mot de passe

5. Me déconnecter de l'application
   - **Critères d'acceptation**:
     - Je peux me déconnecter depuis n'importe quelle page
     - Ma session est terminée et le cookie supprimé
     - Je suis redirigé vers la page d'accueil

6. Supprimer mon compte
   - **Critères d'acceptation**:
     - Je peux demander la suppression de mon compte
     - Je reçois une confirmation avant suppression définitive
     - Toutes mes données personnelles sont supprimées

## Gestion des Listes

### En tant qu'utilisateur authentifié, je veux...

7. Créer une nouvelle liste de personnes
   - **Critères d'acceptation**:
     - Je peux donner un nom à ma liste
     - La liste est créée et associée à mon compte
     - Je suis redirigé vers la page de gestion de cette liste

8. Voir toutes mes listes existantes
   - **Critères d'acceptation**:
     - Je peux voir un tableau de toutes mes listes
     - Pour chaque liste, je vois son nom et si elle est partagée
     - Je peux cliquer sur une liste pour la gérer

9. Modifier le nom d'une liste existante
   - **Critères d'acceptation**:
     - Je peux éditer le nom d'une liste que j'ai créée
     - Le changement est enregistré immédiatement
     - Je vois le nouveau nom dans ma liste de listes

10. Supprimer une liste existante
    - **Critères d'acceptation**:
      - Je peux supprimer une liste que j'ai créée
      - Je reçois une confirmation avant suppression définitive
      - La liste et toutes les personnes qu'elle contient sont supprimées

11. Partager une liste avec d'autres utilisateurs
    - **Critères d'acceptation**:
      - Je peux spécifier l'email d'un utilisateur avec qui partager
      - L'utilisateur reçoit une notification de partage
      - L'utilisateur peut accéder à la liste partagée

12. Voir les listes partagées avec moi
    - **Critères d'acceptation**:
      - Je peux voir un tableau des listes partagées avec moi
      - Pour chaque liste, je vois son nom et son propriétaire
      - Je peux cliquer sur une liste pour la gérer

## Gestion des Personnes

### En tant qu'utilisateur avec accès à une liste, je veux...

13. Ajouter une personne à une liste
    - **Critères d'acceptation**:
      - Je peux saisir le nom, genre, âge et autres caractéristiques
      - La personne est ajoutée à la liste spécifiée
      - Je vois la personne dans la liste des personnes

14. Voir toutes les personnes d'une liste
    - **Critères d'acceptation**:
      - Je peux voir un tableau de toutes les personnes
      - Pour chaque personne, je vois ses caractéristiques principales
      - Je peux filtrer et trier les personnes selon différents critères

15. Modifier les informations d'une personne
    - **Critères d'acceptation**:
      - Je peux éditer toutes les caractéristiques d'une personne
      - Les changements sont enregistrés immédiatement
      - Je vois les informations mises à jour dans la liste

16. Supprimer une personne d'une liste
    - **Critères d'acceptation**:
      - Je peux supprimer une personne de la liste
      - Je reçois une confirmation avant suppression définitive
      - La personne n'apparaît plus dans la liste

17. Importer plusieurs personnes à partir d'un fichier CSV
    - **Critères d'acceptation**:
      - Je peux télécharger un fichier CSV avec des données structurées
      - Le système valide les données avant importation
      - Les personnes sont ajoutées à la liste spécifiée

## Création et Gestion des Groupes

### En tant qu'utilisateur avec accès à une liste, je veux...

18. Créer un tirage pour générer des groupes équilibrés
    - **Critères d'acceptation**:
      - Je peux spécifier le nombre de groupes souhaité
      - Je peux donner un titre au tirage
      - Le système génère des groupes équilibrés selon les critères

19. Voir les détails d'un tirage existant
    - **Critères d'acceptation**:
      - Je peux voir la date de création et le titre du tirage
      - Je peux voir les groupes générés et leur composition
      - Je peux voir les statistiques d'équilibre des groupes

20. Modifier manuellement la composition des groupes
    - **Critères d'acceptation**:
      - Je peux déplacer une personne d'un groupe à un autre
      - Je peux voir l'impact de ce changement sur l'équilibre des groupes
      - Les modifications sont enregistrées immédiatement

21. Exporter les groupes générés
    - **Critères d'acceptation**:
      - Je peux exporter les groupes au format CSV ou PDF
      - L'export contient toutes les informations pertinentes
      - Je peux choisir quelles caractéristiques inclure dans l'export

22. Voir l'historique des tirages pour une liste
    - **Critères d'acceptation**:
      - Je peux voir tous les tirages effectués pour une liste
      - Pour chaque tirage, je vois la date et le titre
      - Je peux cliquer sur un tirage pour voir ses détails

## Administration (pour les administrateurs)

### En tant qu'administrateur, je veux...

23. Voir tous les utilisateurs de la plateforme
    - **Critères d'acceptation**:
      - Je peux voir un tableau de tous les utilisateurs
      - Pour chaque utilisateur, je vois ses informations principales
      - Je peux filtrer et trier les utilisateurs

24. Activer ou désactiver un compte utilisateur
    - **Critères d'acceptation**:
      - Je peux changer le statut d'activation d'un compte
      - L'utilisateur est notifié du changement de statut
      - Un utilisateur désactivé ne peut plus se connecter

25. Voir les statistiques d'utilisation de la plateforme
    - **Critères d'acceptation**:
      - Je peux voir le nombre d'utilisateurs, de listes et de tirages
      - Je peux voir des graphiques d'activité sur différentes périodes
      - Je peux exporter ces statistiques