# Guide de Configuration des Variables d'Environnement

Ce guide vous explique comment configurer les clés API, secrets JWT et mots de passe nécessaires pour faire fonctionner votre plateforme de volley-ball.

## 🔑 Variables Requises

### 1. GROQ_API_KEY
**Description** : Clé API pour le service Groq (IA/chatbot)
**Où l'obtenir** :
1. Allez sur [https://console.groq.com/](https://console.groq.com/)
2. Créez un compte ou connectez-vous
3. Allez dans "API Keys"
4. Cliquez sur "Create API Key"
5. Copiez la clé qui commence par `sk_`

**Format** : `sk_1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef`

### 2. JWT_SECRET
**Description** : Clé secrète pour signer les tokens JWT d'authentification
**Où l'obtenir** : Vous devez la générer vous-même
**Format recommandé** : Chaîne aléatoire de 32+ caractères

**Options de génération** :
- **Automatique** : Utilisez le script `setup-env.bat` ou `setup-env.ps1`
- **Manuel** : Générez une chaîne aléatoire sécurisée
- **En ligne** : Utilisez un générateur de clés sécurisées

### 3. DB_PASSWORD
**Description** : Mot de passe pour toutes les bases de données PostgreSQL
**Où l'obtenir** : Vous devez le définir vous-même
**Format** : Mot de passe sécurisé (8+ caractères, majuscules, minuscules, chiffres, symboles)

## 📧 Variables Optionnelles

### 4. MAIL_USERNAME & MAIL_PASSWORD
**Description** : Identifiants pour l'envoi d'emails via SMTP Gmail
**Où l'obtenir** :
1. Activez l'authentification à 2 facteurs sur votre compte Gmail
2. Générez un "mot de passe d'application"
3. Utilisez ce mot de passe d'application (pas votre mot de passe principal)

## 🚀 Méthodes de Configuration

### Méthode 1 : Scripts Automatiques (Recommandée)

#### Windows (CMD)
```bash
setup-env.bat
```

#### Windows (PowerShell)
```powershell
.\setup-env.ps1
```

### Méthode 2 : Configuration Manuelle

1. **Créez un fichier `.env`** à la racine du projet
2. **Ajoutez vos variables** :

```env
# Configuration des API externes
GROQ_API_KEY=sk_votre_cle_groq_ici

# Configuration JWT
JWT_SECRET=votre_secret_jwt_ici

# Configuration de la base de données
DB_PASSWORD=votre_mot_de_passe_db_ici

# Configuration SMTP pour l'envoi d'emails
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=votre_email@gmail.com
MAIL_PASSWORD=votre_mot_de_passe_app_gmail
```

### Méthode 3 : Variables d'Environnement Système

#### Windows
```cmd
set GROQ_API_KEY=sk_votre_cle_groq_ici
set JWT_SECRET=votre_secret_jwt_ici
set DB_PASSWORD=votre_mot_de_passe_db_ici
```

#### Linux/Mac
```bash
export GROQ_API_KEY=sk_votre_cle_groq_ici
export JWT_SECRET=votre_secret_jwt_ici
export DB_PASSWORD=votre_mot_de_passe_db_ici
```

## 🔒 Sécurité

### Bonnes Pratiques
- ✅ Utilisez des mots de passe forts et uniques
- ✅ Ne partagez jamais vos clés API
- ✅ Régénérez régulièrement vos secrets JWT
- ✅ Utilisez des mots de passe d'application pour Gmail
- ✅ Ne committez jamais le fichier `.env` dans Git

### Ce qui est déjà sécurisé
- Le fichier `.env` est dans `.gitignore`
- Les scripts utilisent des entrées sécurisées (masquées)
- Les variables sensibles ne sont pas affichées dans les logs

## 🧪 Test de Configuration

Après avoir configuré vos variables :

1. **Vérifiez que le fichier `.env` existe** :
   ```bash
   dir .env
   ```

2. **Lancez l'application** :
   ```bash
   docker-compose up -d
   ```

3. **Vérifiez les logs** :
   ```bash
   docker-compose logs
   ```

## 🆘 Dépannage

### Erreur "GROQ_API_KEY not found"
- Vérifiez que la variable est définie dans `.env`
- Vérifiez l'orthographe exacte
- Redémarrez Docker Compose

### Erreur "JWT_SECRET not found"
- Vérifiez que la variable est définie dans `.env`
- Le secret JWT doit être une chaîne non vide

### Erreur de connexion à la base de données
- Vérifiez que `DB_PASSWORD` est défini
- Vérifiez que le mot de passe est correct
- Vérifiez que PostgreSQL est démarré

## 📚 Ressources Utiles

- [Documentation Groq API](https://console.groq.com/docs)
- [Guide JWT](https://jwt.io/)
- [Configuration PostgreSQL](https://www.postgresql.org/docs/)
- [Configuration SMTP Gmail](https://support.google.com/accounts/answer/185833)

## 🤝 Support

Si vous rencontrez des problèmes :
1. Vérifiez que toutes les variables sont définies
2. Consultez les logs Docker
3. Vérifiez la syntaxe du fichier `.env`
4. Ouvrez une issue sur GitHub

---

**Note** : Ce fichier `.env` contient des informations sensibles. Ne le partagez jamais et ne le committez jamais dans Git !
