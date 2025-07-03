---
title: Résolution de Problèmes
description: Guide de résolution des problèmes courants avec l'API EasyGroup
---

# Guide de Résolution de Problèmes

Ce guide vous aide à résoudre les problèmes courants que vous pourriez rencontrer lors de l'utilisation de l'API EasyGroup.

## Problèmes d'Installation

### Erreur de connexion à la base de données

**Symptôme** : L'application ne démarre pas et affiche une erreur de connexion à la base de données.

**Solutions possibles** :
1. Vérifiez que PostgreSQL est en cours d'exécution sur votre système
2. Vérifiez les informations de connexion dans votre fichier `.env`
3. Assurez-vous que la base de données existe :
   ```sql
   CREATE DATABASE easygroup;
   ```
4. Vérifiez que l'utilisateur a les permissions nécessaires sur la base de données

### Port déjà utilisé

**Symptôme** : L'application ne démarre pas et affiche une erreur indiquant que le port est déjà utilisé.

**Solutions possibles** :
1. Modifiez la valeur de `APP_PORT` dans le fichier `.env`
2. Arrêtez l'application qui utilise ce port
3. Utilisez la commande suivante pour identifier l'application qui utilise le port :
   ```bash
   lsof -i :<port>
   ```

## Problèmes d'Authentification

### Impossible de s'inscrire

**Symptôme** : L'inscription échoue avec une erreur 409 Conflict.

**Solutions possibles** :
1. Vérifiez que l'email n'est pas déjà utilisé par un autre compte
2. Assurez-vous que le mot de passe respecte les critères de sécurité (au moins 8 caractères, incluant des lettres majuscules, minuscules, chiffres et caractères spéciaux)
3. Vérifiez que tous les champs obligatoires sont remplis

### Email de vérification non reçu

**Symptôme** : Vous ne recevez pas l'email de vérification après l'inscription.

**Solutions possibles** :
1. Vérifiez votre dossier de spam
2. Vérifiez que l'adresse email fournie est correcte
3. Vérifiez la configuration SMTP dans le fichier `.env`
4. Contactez l'administrateur système pour vérifier les logs d'envoi d'emails

### Impossible de se connecter

**Symptôme** : La connexion échoue avec une erreur 401 Unauthorized.

**Solutions possibles** :
1. Vérifiez que vous avez activé votre compte en cliquant sur le lien dans l'email de vérification
2. Vérifiez que l'email et le mot de passe sont corrects
3. Assurez-vous que votre compte n'a pas été désactivé par un administrateur

## Problèmes de Gestion des Listes

### Impossible de créer une liste

**Symptôme** : La création de liste échoue avec une erreur.

**Solutions possibles** :
1. Vérifiez que vous êtes bien authentifié
2. Assurez-vous que le nom de la liste est unique pour votre compte
3. Vérifiez que le nom de la liste respecte les contraintes (non vide, longueur maximale)

### Impossible de partager une liste

**Symptôme** : Le partage de liste échoue.

**Solutions possibles** :
1. Vérifiez que l'email du destinataire correspond à un utilisateur existant
2. Assurez-vous que vous êtes le propriétaire de la liste
3. Vérifiez que la liste n'est pas déjà partagée avec cet utilisateur

## Problèmes de Gestion des Personnes

### Impossible d'ajouter une personne

**Symptôme** : L'ajout d'une personne à une liste échoue.

**Solutions possibles** :
1. Vérifiez que vous avez les droits d'accès à la liste
2. Assurez-vous que tous les champs obligatoires sont remplis
3. Vérifiez que les valeurs des champs respectent les contraintes (par exemple, l'âge doit être un nombre positif)

### Erreur lors de la modification d'une personne

**Symptôme** : La modification d'une personne échoue.

**Solutions possibles** :
1. Vérifiez que la personne existe dans la liste spécifiée
2. Assurez-vous que vous avez les droits d'accès à la liste
3. Vérifiez que les nouvelles valeurs respectent les contraintes

## Problèmes de Génération de Groupes

### Les groupes générés ne sont pas équilibrés

**Symptôme** : Les groupes générés ne semblent pas équilibrés selon les critères souhaités.

**Solutions possibles** :
1. Vérifiez les critères d'équilibrage spécifiés dans la requête
2. Assurez-vous que les personnes dans la liste ont des valeurs différentes pour les critères d'équilibrage
3. Essayez de générer les groupes avec un nombre différent de personnes par groupe
4. Utilisez les fonctionnalités de modification manuelle des groupes pour ajuster les résultats

### Erreur lors de la génération de groupes

**Symptôme** : La génération de groupes échoue avec une erreur.

**Solutions possibles** :
1. Vérifiez que la liste contient suffisamment de personnes (au moins 2)
2. Assurez-vous que le nombre de personnes par groupe est valide
3. Vérifiez que vous avez les droits d'accès à la liste

## Problèmes Techniques

### Erreur 500 Internal Server Error

**Symptôme** : Une requête échoue avec une erreur 500.

**Solutions possibles** :
1. Vérifiez les logs du serveur pour plus de détails sur l'erreur
2. Assurez-vous que la base de données est accessible
3. Vérifiez que les paramètres de la requête sont valides
4. Contactez l'administrateur système si le problème persiste

### Performances lentes

**Symptôme** : L'API répond lentement aux requêtes.

**Solutions possibles** :
1. Vérifiez la charge du serveur
2. Assurez-vous que la base de données est correctement indexée
3. Limitez le nombre d'éléments demandés par requête en utilisant la pagination
4. Vérifiez la connexion réseau entre le client et le serveur

## Contacter le Support

Si vous ne parvenez pas à résoudre votre problème avec ce guide, vous pouvez contacter notre équipe de support :

- **Email** : support@easygroup.com
- **GitHub** : [Signaler un problème](https://github.com/0x485254/brief-26)

Veuillez fournir autant de détails que possible sur votre problème, y compris :
- La version de l'API que vous utilisez
- Les étapes pour reproduire le problème
- Les messages d'erreur exacts
- Les logs pertinents