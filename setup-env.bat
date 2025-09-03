@echo off
echo ========================================
echo Configuration des variables d'environnement
echo ========================================
echo.

echo 1. Configuration de l'API Groq
echo ------------------------------
echo Allez sur https://console.groq.com/
echo Connectez-vous et créez une nouvelle clé API
echo Copiez la clé qui commence par "sk_"
echo.
set /p GROQ_API_KEY="Entrez votre clé API Groq: "

echo.
echo 2. Configuration du secret JWT
echo ------------------------------
echo Le secret JWT doit être une chaîne aléatoire sécurisée
echo.
set /p JWT_SECRET="Entrez votre secret JWT (ou appuyez sur Entrée pour générer): "

if "%JWT_SECRET%"=="" (
    echo Génération d'un secret JWT sécurisé...
    set JWT_SECRET=volleyball_jwt_secret_%RANDOM%_%RANDOM%_%RANDOM%
)

echo.
echo 3. Configuration du mot de passe de la base de données
echo -----------------------------------------------------
echo Ce mot de passe sera utilisé pour toutes les bases de données
echo.
set /p DB_PASSWORD="Entrez le mot de passe de la base de données: "

echo.
echo 4. Configuration SMTP (optionnel)
echo --------------------------------
echo Si vous voulez configurer l'envoi d'emails
echo.
set /p MAIL_USERNAME="Entrez votre email Gmail (ou appuyez sur Entrée pour ignorer): "

if not "%MAIL_USERNAME%"=="" (
    set /p MAIL_PASSWORD="Entrez votre mot de passe d'application Gmail: "
)

echo.
echo 5. Création du fichier .env
echo ---------------------------
echo Création du fichier .env avec vos configurations...

(
echo # Configuration des API externes
echo GROQ_API_KEY=%GROQ_API_KEY%
echo.
echo # Configuration JWT
echo JWT_SECRET=%JWT_SECRET%
echo.
echo # Configuration de la base de données
echo DB_PASSWORD=%DB_PASSWORD%
echo.
echo # Configuration SMTP pour l'envoi d'emails
echo MAIL_HOST=smtp.gmail.com
echo MAIL_PORT=587
echo MAIL_USERNAME=%MAIL_USERNAME%
echo MAIL_PASSWORD=%MAIL_PASSWORD%
) > .env

echo.
echo Fichier .env créé avec succès !
echo.
echo Récapitulatif de votre configuration:
echo - GROQ_API_KEY: %GROQ_API_KEY%
echo - JWT_SECRET: %JWT_SECRET%
echo - DB_PASSWORD: %DB_PASSWORD%
echo - MAIL_USERNAME: %MAIL_USERNAME%
echo.
echo Vous pouvez maintenant lancer votre application avec:
echo docker-compose up -d
echo.
pause
