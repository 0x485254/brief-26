# Authentification et Autorisation

Ce document décrit le système d'authentification et d'autorisation utilisé dans l'application EasyGroup.

## Aperçu

L'application EasyGroup utilise l'authentification basée sur JWT (JSON Web Token) et l'autorisation basée sur les rôles. Le système est conçu selon les principes SOLID et utilise Spring Security pour l'implémentation.

## Flux d'Authentification

1. **Inscription**: Les utilisateurs s'inscrivent avec leur email et mot de passe.
2. **Connexion**: Les utilisateurs se connectent avec leurs identifiants et reçoivent un token JWT.
3. **Requêtes Authentifiées**: Pour les requêtes suivantes, les utilisateurs incluent le token JWT dans l'en-tête Authorization.

## Composants

### Configuration de Sécurité

La configuration de sécurité est définie dans `SecurityConfig.java`. Elle configure:

- L'encodage des mots de passe utilisant l'algorithme Argon2id
- Le filtre d'authentification JWT
- Les règles de sécurité pour les endpoints
- Le fournisseur et le gestionnaire d'authentification

### Service JWT

Le `JwtService` gère la génération et la validation des tokens JWT. Il:

- Génère des tokens avec les informations utilisateur (ID, rôle)
- Valide les tokens
- Extrait les revendications (claims) des tokens

### Service de Détails Utilisateur

Le `CustomUserDetailsService` charge les détails utilisateur depuis la base de données pour Spring Security. Il:

- Charge l'utilisateur par email
- Convertit l'entité User en UserDetails de Spring Security
- Ajoute les autorités basées sur les rôles appropriés

### Filtre d'Authentification

Le `JwtAuthenticationFilter` intercepte les requêtes et valide les tokens JWT. Il:

- Extrait le token de l'en-tête Authorization
- Valide le token
- Définit l'authentification dans le SecurityContext si le token est valide

## Décorateurs de Garde

L'application fournit deux décorateurs de garde pour protéger les endpoints:

### IsAuthenticated

L'annotation `@IsAuthenticated` restreint l'accès aux utilisateurs authentifiés uniquement. Exemple d'utilisation:

```java
@GetMapping("/profile")
@IsAuthenticated
public ResponseEntity<User> getCurrentUser() {
    // Seuls les utilisateurs authentifiés peuvent accéder à cet endpoint
}
```

### IsAdmin

L'annotation `@IsAdmin` restreint l'accès aux utilisateurs administrateurs uniquement. Exemple d'utilisation:

```java
@GetMapping("/users")
@IsAdmin
public ResponseEntity<List<User>> getAllUsers() {
    // Seuls les utilisateurs administrateurs peuvent accéder à cet endpoint
}
```

## Tests

Le système d'authentification et d'autorisation est testé dans:

- `AuthenticationTest.java`: Tests pour la fonctionnalité d'authentification
- `GuardDecoratorTest.java`: Tests pour les décorateurs de garde

## Utilisation

### Authentification Côté Client

Pour s'authentifier depuis une application cliente:

1. Envoyer une requête POST à `/api/auth/login` avec l'email et le mot de passe.
2. Stocker le token reçu.
3. Inclure le token dans l'en-tête Authorization pour les requêtes suivantes:
   ```
   Authorization: Bearer <token>
   ```

### Protection des Endpoints

Pour protéger un endpoint:

1. Utiliser `@IsAuthenticated` pour restreindre l'accès aux utilisateurs authentifiés.
2. Utiliser `@IsAdmin` pour restreindre l'accès aux utilisateurs administrateurs.
