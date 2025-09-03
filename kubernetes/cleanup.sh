#!/bin/bash

# Script de nettoyage Kubernetes pour la plateforme Volley-ball
# Usage: ./cleanup.sh [namespace]

set -e

NAMESPACE=${1:-volleyball-platform}
echo "🧹 Nettoyage du namespace: $NAMESPACE"

# Vérifier que kubectl est installé
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl n'est pas installé. Veuillez l'installer d'abord."
    exit 1
fi

# Vérifier que le namespace existe
if ! kubectl get namespace $NAMESPACE &> /dev/null; then
    echo "⚠️ Le namespace $NAMESPACE n'existe pas."
    exit 0
fi

# Supprimer l'ingress en premier
echo "🌐 Suppression de l'ingress..."
kubectl delete -f ingress.yaml --ignore-not-found=true

# Supprimer le frontend
echo "🎨 Suppression du frontend..."
kubectl delete -f frontend.yaml --ignore-not-found=true

# Supprimer les microservices
echo "🔧 Suppression des microservices..."
kubectl delete -f microservices.yaml --ignore-not-found=true

# Supprimer les bases de données
echo "🗄️ Suppression des bases de données..."
kubectl delete -f databases.yaml --ignore-not-found=true

# Supprimer les services
echo "🔌 Suppression des services..."
kubectl delete -f services.yaml --ignore-not-found=true

# Supprimer la configuration
echo "⚙️ Suppression de la configuration..."
kubectl delete -f configmap.yaml --ignore-not-found=true

# Supprimer le stockage
echo "💾 Suppression du stockage..."
kubectl delete -f storage.yaml --ignore-not-found=true

# Supprimer les secrets
echo "🔐 Suppression des secrets..."
kubectl delete -f secrets.yaml --ignore-not-found=true

# Supprimer le namespace complet
echo "📁 Suppression du namespace $NAMESPACE..."
kubectl delete namespace $NAMESPACE --ignore-not-found=true

# Nettoyer les ressources orphelines
echo "🧹 Nettoyage des ressources orphelines..."
kubectl get pods --all-namespaces | grep $NAMESPACE | awk '{print $2}' | xargs -r kubectl delete pod --all-namespaces --ignore-not-found=true
kubectl get services --all-namespaces | grep $NAMESPACE | awk '{print $2}' | xargs -r kubectl delete service --all-namespaces --ignore-not-found=true
kubectl get deployments --all-namespaces | grep $NAMESPACE | awk '{print $2}' | xargs -r kubectl delete deployment --all-namespaces --ignore-not-found=true

echo "✅ Nettoyage terminé avec succès!"
echo ""
echo "📋 Vérification:"
echo "   - Namespace supprimé: kubectl get namespace | grep $NAMESPACE"
echo "   - Ressources restantes: kubectl get all --all-namespaces | grep $NAMESPACE"
