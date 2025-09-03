Write-Host "========================================" -ForegroundColor Green
Write-Host "Configuration des variables d'environnement" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "1. Configuration de l'API Groq" -ForegroundColor Yellow
Write-Host "------------------------------" -ForegroundColor Yellow
Write-Host "Allez sur https://console.groq.com/" -ForegroundColor Cyan
Write-Host "Connectez-vous et créez une nouvelle clé API" -ForegroundColor Cyan
Write-Host "Copiez la clé qui commence par 'sk_'" -ForegroundColor Cyan
Write-Host ""
$GROQ_API_KEY = Read-Host "Entrez votre clé API Groq"

Write-Host ""
Write-Host "2. Configuration du secret JWT" -ForegroundColor Yellow
Write-Host "------------------------------" -ForegroundColor Yellow
Write-Host "Le secret JWT doit être une chaîne aléatoire sécurisée" -ForegroundColor Cyan
Write-Host ""
$JWT_SECRET = Read-Host "Entrez votre secret JWT (ou appuyez sur Entrée pour générer)"

if ([string]::IsNullOrEmpty($JWT_SECRET)) {
    Write-Host "Génération d'un secret JWT sécurisé..." -ForegroundColor Green
    $random = Get-Random -Minimum 1000 -Maximum 9999
    $JWT_SECRET = "volleyball_jwt_secret_$random" + (Get-Random -Minimum 1000 -Maximum 9999) + "_" + (Get-Random -Minimum 1000 -Maximum 9999)
}

Write-Host ""
Write-Host "3. Configuration du mot de passe de la base de données" -ForegroundColor Yellow
Write-Host "-----------------------------------------------------" -ForegroundColor Yellow
Write-Host "Ce mot de passe sera utilisé pour toutes les bases de données" -ForegroundColor Cyan
Write-Host ""
$DB_PASSWORD = Read-Host "Entrez le mot de passe de la base de données" -AsSecureString
$DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($DB_PASSWORD))

Write-Host ""
Write-Host "4. Configuration SMTP (optionnel)" -ForegroundColor Yellow
Write-Host "--------------------------------" -ForegroundColor Yellow
Write-Host "Si vous voulez configurer l'envoi d'emails" -ForegroundColor Cyan
Write-Host ""
$MAIL_USERNAME = Read-Host "Entrez votre email Gmail (ou appuyez sur Entrée pour ignorer)"

$MAIL_PASSWORD = ""
if (-not [string]::IsNullOrEmpty($MAIL_USERNAME)) {
    $MAIL_PASSWORD = Read-Host "Entrez votre mot de passe d'application Gmail" -AsSecureString
    $MAIL_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($MAIL_PASSWORD))
}

Write-Host ""
Write-Host "5. Création du fichier .env" -ForegroundColor Yellow
Write-Host "---------------------------" -ForegroundColor Yellow
Write-Host "Création du fichier .env avec vos configurations..." -ForegroundColor Green

$envContent = @"
# Configuration des API externes
GROQ_API_KEY=$GROQ_API_KEY

# Configuration JWT
JWT_SECRET=$JWT_SECRET

# Configuration de la base de données
DB_PASSWORD=$DB_PASSWORD

# Configuration SMTP pour l'envoi d'emails
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=$MAIL_USERNAME
MAIL_PASSWORD=$MAIL_PASSWORD
"@

$envContent | Out-File -FilePath ".env" -Encoding UTF8

Write-Host ""
Write-Host "Fichier .env créé avec succès !" -ForegroundColor Green
Write-Host ""
Write-Host "Récapitulatif de votre configuration:" -ForegroundColor Yellow
Write-Host "- GROQ_API_KEY: $GROQ_API_KEY" -ForegroundColor Cyan
Write-Host "- JWT_SECRET: $JWT_SECRET" -ForegroundColor Cyan
Write-Host "- DB_PASSWORD: $DB_PASSWORD" -ForegroundColor Cyan
Write-Host "- MAIL_USERNAME: $MAIL_USERNAME" -ForegroundColor Cyan
Write-Host ""
Write-Host "Vous pouvez maintenant lancer votre application avec:" -ForegroundColor Green
Write-Host "docker-compose up -d" -ForegroundColor Cyan
Write-Host ""
Read-Host "Appuyez sur Entrée pour continuer"
