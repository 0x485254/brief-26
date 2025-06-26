# Authentification Basée sur les Cookies

Ce document décrit le système d'authentification basé sur les cookies utilisé dans l'application EasyGroup.

## Aperçu

L'application EasyGroup fournit deux mécanismes d'authentification:
1. Authentification basée sur les tokens JWT (via l'en-tête Authorization)
2. Authentification basée sur les cookies HTTP-only

L'authentification basée sur les cookies est plus sécurisée contre les attaques XSS car le token JWT est stocké dans un cookie HTTP-only qui ne peut pas être accédé par JavaScript.

## Composants

### Service de Cookie

Le `CookieService` gère les opérations de cookies:
- Création de cookies avec des tokens JWT
- Ajout de cookies aux réponses HTTP
- Suppression de cookies pour la déconnexion
- Extraction des tokens des cookies dans les requêtes

### Filtre d'Authentification par Cookie

Le `CookieAuthenticationFilter` intercepte les requêtes et authentifie les utilisateurs basé sur les tokens JWT dans les cookies:
- Extrait le token JWT des cookies
- Valide le token
- Configure l'authentification dans le contexte de sécurité

### Service d'Authentification par Cookie

Le `CookieAuthService` gère l'authentification et l'inscription des utilisateurs:
- Inscrit de nouveaux utilisateurs
- Authentifie les utilisateurs et définit les cookies de token JWT
- Fournit une fonctionnalité de déconnexion pour supprimer les cookies

### Contrôleur d'Authentification par Cookie

Le `CookieAuthController` fournit des endpoints pour l'authentification basée sur les cookies:
- `/api/auth/cookie/register` - Inscrire un nouvel utilisateur et définir un cookie de token JWT
- `/api/auth/cookie/login` - Authentifier un utilisateur et définir un cookie de token JWT
- `/api/auth/cookie/logout` - Déconnecter un utilisateur en supprimant le cookie de token JWT

### Configuration de Sécurité des Cookies

Le `CookieSecurityConfig` configure Spring Security pour l'authentification basée sur les cookies:
- Active la protection CSRF avec un référentiel de tokens basé sur les cookies
- Configure la chaîne de filtres de sécurité pour utiliser le filtre d'authentification par cookie
- Configure le fournisseur et le gestionnaire d'authentification

## Utilisation

### Authentification Côté Client

Pour s'authentifier depuis une application cliente:

1. Inscrire un nouvel utilisateur:
   ```
   POST /api/auth/cookie/register
   Content-Type: application/json

   {
     "email": "user@example.com",
     "password": "password123"
   }
   ```

2. Se connecter avec les identifiants:
   ```
   POST /api/auth/cookie/login
   Content-Type: application/json

   {
     "email": "user@example.com",
     "password": "password123"
   }
   ```

3. Le token JWT sera défini comme un cookie HTTP-only, et les requêtes suivantes incluront automatiquement le cookie.

4. Se déconnecter pour supprimer le cookie:
   ```
   POST /api/auth/cookie/logout
   ```

### Protection CSRF

Lors de l'utilisation de l'authentification basée sur les cookies, la protection CSRF est activée. Le serveur définira un cookie de token CSRF qui n'est pas HTTP-only, et les clients doivent inclure ce token dans un en-tête pour les requêtes modifiant l'état (POST, PUT, DELETE):

```
X-CSRF-TOKEN: <token du cookie XSRF-TOKEN>
```

## Considérations de Sécurité

L'implémentation de l'authentification basée sur les cookies inclut plusieurs fonctionnalités de sécurité:

1. Les cookies HTTP-only empêchent l'accès JavaScript au token JWT
2. L'attribut Secure garantit que les cookies sont envoyés uniquement via HTTPS
3. L'attribut SameSite prévient les attaques CSRF
4. Protection CSRF pour les requêtes modifiant l'état
5. Gestion de session sans état

## Configuration

L'authentification basée sur les cookies peut être configurée via les propriétés de l'application:

```properties
# Paramètres des cookies
cookie.jwt.name=JWT_TOKEN
cookie.jwt.expiration=86400
cookie.jwt.secure=true
cookie.jwt.http-only=true
cookie.jwt.path=/
cookie.jwt.domain=
cookie.jwt.same-site=Strict

# Paramètres JWT
jwt.secret=your-secret-key
jwt.expiration=86400000
```
