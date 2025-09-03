#!/bin/bash

# Script de déploiement Kubernetes pour la plateforme Volley-ball
# Usage: ./deploy.sh [namespace]

set -e

NAMESPACE=${1:-volleyball-platform}
echo "🚀 Déploiement sur le namespace: $NAMESPACE"

# Vérifier que kubectl est installé
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl n'est pas installé. Veuillez l'installer d'abord."
    exit 1
fi

# Vérifier la connexion au cluster
echo "🔍 Vérification de la connexion au cluster..."
kubectl cluster-info

# Créer le namespace s'il n'existe pas
echo "📁 Création du namespace..."
kubectl apply -f namespace.yaml

# Attendre que le namespace soit créé
kubectl wait --for=condition=Active namespace/$NAMESPACE --timeout=60s

# Appliquer les secrets
echo "🔐 Application des secrets..."
kubectl apply -f secrets.yaml

# Appliquer le stockage
echo "💾 Application du stockage..."
kubectl apply -f storage.yaml

# Appliquer la configuration
echo "⚙️ Application de la configuration..."
kubectl apply -f configmap.yaml

# Appliquer les services
echo "🔌 Application des services..."
kubectl apply -f services.yaml

# Appliquer les bases de données
echo "🗄️ Déploiement des bases de données..."
kubectl apply -f databases.yaml

# Attendre que les bases de données soient prêtes
echo "⏳ Attente que les bases de données soient prêtes..."
kubectl wait --for=condition=available --timeout=300s deployment/auth-db -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/medical-db -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/performance-db -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/planning-db -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/finance-db -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/admin-db -n $NAMESPACE

# Appliquer les microservices
echo "🔧 Déploiement des microservices..."
kubectl apply -f microservices.yaml

# Attendre que le service de découverte soit prêt
echo "⏳ Attente que le service de découverte soit prêt..."
kubectl wait --for=condition=available --timeout=300s deployment/discovery-service -n $NAMESPACE

# Appliquer le frontend
echo "🎨 Déploiement du frontend..."
kubectl apply -f frontend.yaml

# Appliquer l'ingress
echo "🌐 Configuration de l'ingress..."
kubectl apply -f ingress.yaml

# Attendre que tous les services soient prêts
echo "⏳ Attente que tous les services soient prêts..."
kubectl wait --for=condition=available --timeout=600s deployment/frontend -n $NAMESPACE
kubectl wait --for=condition=available --timeout=600s deployment/gateway-service -n $NAMESPACE
kubectl wait --for=condition=available --timeout=600s deployment/auth-service -n $NAMESPACE

# Afficher le statut
echo "📊 Statut du déploiement:"
kubectl get pods -n $NAMESPACE
kubectl get services -n $NAMESPACE
kubectl get ingress -n $NAMESPACE

echo "🎉 Déploiement terminé avec succès!"
echo ""
echo "🌐 Accès à l'application:"
echo "   - Frontend: http://volleyball.local (ou via LoadBalancer)"
echo "   - API: http://api.volleyball.local"
echo "   - Eureka: http://eureka.volleyball.local"
echo ""
echo "📋 Commandes utiles:"
echo "   - Voir les pods: kubectl get pods -n $NAMESPACE"
echo "   - Voir les logs: kubectl logs -f deployment/[service-name] -n $NAMESPACE"
echo "   - Accès au pod: kubectl exec -it [pod-name] -n $NAMESPACE -- /bin/bash"
echo "   - Supprimer tout: kubectl delete namespace $NAMESPACE"
