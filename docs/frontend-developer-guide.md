# Guide pour les Développeurs Frontend - EasyGroup

Ce guide est destiné aux développeurs frontend qui travaillent sur l'application EasyGroup. Il fournit des informations essentielles pour interagir avec l'API backend et implémenter les fonctionnalités requises.

## Table des Matières

1. [Architecture Générale](#architecture-générale)
2. [Configuration de l'Environnement](#configuration-de-lenvironnement)
3. [Authentification](#authentification)
4. [Gestion des Utilisateurs](#gestion-des-utilisateurs)
5. [Gestion des Listes](#gestion-des-listes)
6. [Gestion des Personnes](#gestion-des-personnes)
7. [Création et Gestion des Groupes](#création-et-gestion-des-groupes)
8. [Bonnes Pratiques](#bonnes-pratiques)
9. [Exemples de Code](#exemples-de-code)

## Architecture Générale

L'application EasyGroup suit une architecture client-serveur classique :

- **Backend** : API REST développée avec Spring Boot
- **Base de données** : PostgreSQL
- **Frontend** : À implémenter selon les besoins (React, Angular, Vue.js, etc.)

Le backend expose des endpoints REST qui permettent de manipuler les ressources suivantes :
- Utilisateurs
- Listes de personnes
- Personnes
- Tirages
- Groupes

## Configuration de l'Environnement

### URL de Base de l'API

En développement local, l'API est accessible à l'adresse :
```
http://localhost:8080
```

Pour les environnements de production ou de pré-production, consultez la documentation spécifique à l'environnement.

### CORS

Le backend est configuré pour accepter les requêtes CORS depuis les origines suivantes :
- `http://localhost:3000` (développement frontend local)
- `https://easygroup.example.com` (production)

Si vous avez besoin d'ajouter d'autres origines, contactez l'équipe backend.

## Authentification

### Mécanisme d'Authentification

EasyGroup utilise une authentification basée sur des cookies HTTP-only contenant des tokens JWT. Cette approche offre une meilleure protection contre les attaques XSS par rapport aux tokens stockés dans le localStorage.

### Flux d'Authentification

#### Inscription

```javascript
// Exemple avec fetch API
async function register(userData) {
  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
      credentials: 'include', // Important pour les cookies
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de l\'inscription');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}

// Exemple d'utilisation
const userData = {
  email: 'utilisateur@exemple.com',
  password: 'motdepasse123',
  firstName: 'Prénom',
  lastName: 'Nom'
};

register(userData)
  .then(user => {
    console.log('Utilisateur inscrit:', user);
    // Rediriger vers le tableau de bord
  })
  .catch(error => {
    // Afficher un message d'erreur
  });
```

#### Connexion

```javascript
async function login(credentials) {
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
      credentials: 'include', // Important pour les cookies
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la connexion');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}

// Exemple d'utilisation
const credentials = {
  email: 'utilisateur@exemple.com',
  password: 'motdepasse123'
};

login(credentials)
  .then(user => {
    console.log('Utilisateur connecté:', user);
    // Rediriger vers le tableau de bord
  })
  .catch(error => {
    // Afficher un message d'erreur
  });
```

#### Déconnexion

```javascript
async function logout() {
  try {
    const response = await fetch('http://localhost:8080/api/auth/logout', {
      method: 'POST',
      credentials: 'include', // Important pour les cookies
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la déconnexion');
    }
    
    // Rediriger vers la page d'accueil
    window.location.href = '/';
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

### Protection CSRF

Le backend utilise une protection CSRF. Pour les requêtes non-GET, vous devez inclure un token CSRF dans l'en-tête `X-CSRF-TOKEN`. Ce token est disponible dans un cookie nommé `XSRF-TOKEN`.

```javascript
// Fonction utilitaire pour récupérer le token CSRF
function getCsrfToken() {
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
    const [name, value] = cookie.trim().split('=');
    if (name === 'XSRF-TOKEN') {
      return value;
    }
  }
  return null;
}

// Exemple d'utilisation avec une requête POST
async function createList(listData) {
  const csrfToken = getCsrfToken();
  
  try {
    const response = await fetch('http://localhost:8080/api/lists', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken,
      },
      body: JSON.stringify(listData),
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la création de la liste');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

## Gestion des Utilisateurs

### Récupération du Profil Utilisateur

```javascript
async function getUserProfile() {
  try {
    const response = await fetch('http://localhost:8080/api/users/me', {
      method: 'GET',
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la récupération du profil');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

### Mise à Jour du Profil Utilisateur

```javascript
async function updateUserProfile(profileData) {
  const csrfToken = getCsrfToken();
  
  try {
    const response = await fetch('http://localhost:8080/api/users/me', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken,
      },
      body: JSON.stringify(profileData),
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la mise à jour du profil');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

## Gestion des Listes

### Création d'une Liste

```javascript
async function createList(listData) {
  const csrfToken = getCsrfToken();
  
  try {
    const response = await fetch('http://localhost:8080/api/lists', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken,
      },
      body: JSON.stringify(listData),
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la création de la liste');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

### Récupération des Listes

```javascript
async function getLists() {
  try {
    const response = await fetch('http://localhost:8080/api/lists', {
      method: 'GET',
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la récupération des listes');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

## Gestion des Personnes

### Ajout d'une Personne à une Liste

```javascript
async function addPersonToList(listId, personData) {
  const csrfToken = getCsrfToken();
  const userId = 'current'; // Utilisateur courant
  
  try {
    const response = await fetch(`http://localhost:8080/api/users/${userId}/lists/${listId}/persons`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken,
      },
      body: JSON.stringify(personData),
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de l\'ajout de la personne');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

### Récupération des Personnes d'une Liste

```javascript
async function getPersonsFromList(listId) {
  const userId = 'current'; // Utilisateur courant
  
  try {
    const response = await fetch(`http://localhost:8080/api/users/${userId}/lists/${listId}/persons`, {
      method: 'GET',
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la récupération des personnes');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

## Création et Gestion des Groupes

### Création d'un Tirage

```javascript
async function createDraw(listId, drawData) {
  const csrfToken = getCsrfToken();
  const userId = 'current'; // Utilisateur courant
  
  try {
    const response = await fetch(`http://localhost:8080/api/users/${userId}/lists/${listId}/draws`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-TOKEN': csrfToken,
      },
      body: JSON.stringify(drawData),
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la création du tirage');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

### Récupération des Groupes d'un Tirage

```javascript
async function getGroupsFromDraw(drawId) {
  try {
    const response = await fetch(`http://localhost:8080/api/draws/${drawId}/groups`, {
      method: 'GET',
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Erreur lors de la récupération des groupes');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    throw error;
  }
}
```

## Bonnes Pratiques

### Gestion des Erreurs

Toujours gérer les erreurs de manière appropriée :

```javascript
async function apiCall() {
  try {
    const response = await fetch('http://localhost:8080/api/endpoint', {
      method: 'GET',
      credentials: 'include',
    });
    
    if (!response.ok) {
      // Analyser le corps de la réponse pour obtenir des détails sur l'erreur
      const errorData = await response.json();
      throw new Error(errorData.message || 'Une erreur est survenue');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Erreur:', error);
    // Afficher un message d'erreur à l'utilisateur
    // Éventuellement, enregistrer l'erreur dans un service de monitoring
    throw error;
  }
}
```

### Validation des Données

Validez toujours les données côté client avant de les envoyer au serveur :

```javascript
function validatePersonData(personData) {
  const errors = {};
  
  if (!personData.name || personData.name.trim() === '') {
    errors.name = 'Le nom est requis';
  }
  
  if (!personData.age || personData.age < 1) {
    errors.age = 'L\'âge doit être un nombre positif';
  }
  
  if (!['FEMALE', 'MALE', 'OTHER'].includes(personData.gender)) {
    errors.gender = 'Le genre doit être FEMALE, MALE ou OTHER';
  }
  
  if (![1, 2, 3, 4, 5].includes(personData.frenchLevel)) {
    errors.frenchLevel = 'Le niveau de français doit être entre 1 et 5';
  }
  
  if (![1, 2, 3, 4, 5].includes(personData.techLevel)) {
    errors.techLevel = 'Le niveau technique doit être entre 1 et 5';
  }
  
  if (!['A_LAISE', 'RESERVE', 'TIMIDE'].includes(personData.profile)) {
    errors.profile = 'Le profil doit être A_LAISE, RESERVE ou TIMIDE';
  }
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
}
```

### Gestion de l'État d'Authentification

Créez un service ou un hook pour gérer l'état d'authentification :

```javascript
// Exemple avec React et Context API
import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    // Vérifier si l'utilisateur est déjà connecté
    async function checkAuth() {
      try {
        const response = await fetch('http://localhost:8080/api/users/me', {
          method: 'GET',
          credentials: 'include',
        });
        
        if (response.ok) {
          const userData = await response.json();
          setUser(userData);
        }
      } catch (error) {
        console.error('Erreur lors de la vérification de l\'authentification:', error);
      } finally {
        setLoading(false);
      }
    }
    
    checkAuth();
  }, []);
  
  const login = async (credentials) => {
    // Implémentation de la connexion
  };
  
  const logout = async () => {
    // Implémentation de la déconnexion
  };
  
  const register = async (userData) => {
    // Implémentation de l'inscription
  };
  
  return (
    <AuthContext.Provider value={{ user, loading, login, logout, register }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
```

## Exemples de Code

### Exemple de Composant de Connexion (React)

```jsx
import React, { useState } from 'react';
import { useAuth } from './AuthContext';

function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      await login({ email, password });
      // Redirection gérée dans le service d'authentification
    } catch (error) {
      setError('Email ou mot de passe incorrect');
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <h2>Connexion</h2>
      
      {error && <div className="error">{error}</div>}
      
      <div>
        <label htmlFor="email">Email:</label>
        <input
          type="email"
          id="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </div>
      
      <div>
        <label htmlFor="password">Mot de passe:</label>
        <input
          type="password"
          id="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>
      
      <button type="submit">Se connecter</button>
    </form>
  );
}

export default LoginForm;
```

### Exemple de Composant de Liste de Personnes (React)

```jsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

function PersonList() {
  const [persons, setPersons] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { listId } = useParams();
  
  useEffect(() => {
    async function fetchPersons() {
      try {
        const userId = 'current'; // Utilisateur courant
        const response = await fetch(`http://localhost:8080/api/users/${userId}/lists/${listId}/persons`, {
          method: 'GET',
          credentials: 'include',
        });
        
        if (!response.ok) {
          throw new Error('Erreur lors de la récupération des personnes');
        }
        
        const data = await response.json();
        setPersons(data);
      } catch (error) {
        console.error('Erreur:', error);
        setError('Impossible de charger les personnes. Veuillez réessayer plus tard.');
      } finally {
        setLoading(false);
      }
    }
    
    fetchPersons();
  }, [listId]);
  
  if (loading) {
    return <div>Chargement...</div>;
  }
  
  if (error) {
    return <div className="error">{error}</div>;
  }
  
  return (
    <div>
      <h2>Personnes dans la liste</h2>
      
      {persons.length === 0 ? (
        <p>Aucune personne dans cette liste.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Nom</th>
              <th>Genre</th>
              <th>Âge</th>
              <th>Niveau de français</th>
              <th>Ancien DWWM</th>
              <th>Niveau technique</th>
              <th>Profil</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {persons.map((person) => (
              <tr key={person.id}>
                <td>{person.name}</td>
                <td>{person.gender}</td>
                <td>{person.age}</td>
                <td>{person.frenchLevel}</td>
                <td>{person.oldDwwm ? 'Oui' : 'Non'}</td>
                <td>{person.techLevel}</td>
                <td>{person.profile}</td>
                <td>
                  <button>Modifier</button>
                  <button>Supprimer</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      
      <button>Ajouter une personne</button>
    </div>
  );
}

export default PersonList;
```

Pour plus d'informations sur les endpoints disponibles et les formats de données, consultez la [documentation de l'API](./api-documentation.md).