# 🏐 Déploiement Kubernetes - Plateforme Volley-ball

Ce dossier contient tous les fichiers Kubernetes nécessaires pour déployer votre plateforme de volley-ball sur un cluster Kubernetes.

## 📁 Structure des fichiers

```
kubernetes/
├── namespace.yaml          # Namespaces de l'application
├── configmap.yaml          # Configuration des services
├── secrets.yaml            # Secrets et clés sensibles
├── storage.yaml            # PersistentVolumeClaims pour les bases de données
├── services.yaml           # Services Kubernetes
├── databases.yaml          # Déploiements des bases de données PostgreSQL
├── microservices.yaml      # Déploiements des microservices Java
├── frontend.yaml           # Déploiement du frontend Angular
├── ingress.yaml            # Configuration Ingress pour l'accès externe
├── deploy.sh               # Script de déploiement automatique
├── cleanup.sh              # Script de nettoyage
└── README.md               # Cette documentation
```

## 🚀 Déploiement rapide

### Prérequis

1. **Cluster Kubernetes** fonctionnel
2. **kubectl** installé et configuré
3. **Images Docker** construites et disponibles
4. **NGINX Ingress Controller** installé (optionnel)

### Déploiement automatique

```bash
# Rendre les scripts exécutables
chmod +x deploy.sh cleanup.sh

# Déployer l'application
./deploy.sh

# Ou spécifier un namespace personnalisé
./deploy.sh mon-namespace
```

### Déploiement manuel

```bash
# 1. Créer le namespace
kubectl apply -f namespace.yaml

# 2. Appliquer les secrets
kubectl apply -f secrets.yaml

# 3. Appliquer le stockage
kubectl apply -f storage.yaml

# 4. Appliquer la configuration
kubectl apply -f configmap.yaml

# 5. Appliquer les services
kubectl apply -f services.yaml

# 6. Déployer les bases de données
kubectl apply -f databases.yaml

# 7. Déployer les microservices
kubectl apply -f microservices.yaml

# 8. Déployer le frontend
kubectl apply -f frontend.yaml

# 9. Configurer l'ingress
kubectl apply -f ingress.yaml
```

## 🔧 Configuration

### Variables d'environnement

Les secrets sont configurés dans `secrets.yaml` :

- **JWT_SECRET** : Clé secrète pour les tokens JWT
- **DB_PASSWORD** : Mot de passe des bases de données
- **GROQ_API_KEY** : Clé API Groq pour le chatbot
- **MAIL_USERNAME** : Nom d'utilisateur SMTP
- **MAIL_PASSWORD** : Mot de passe SMTP

### Ressources

Chaque service est configuré avec des limites de ressources :

- **Bases de données** : 256Mi-512Mi RAM, 250m-500m CPU
- **Microservices** : 512Mi-1Gi RAM, 500m-1000m CPU
- **Frontend** : 256Mi-512Mi RAM, 250m-500m CPU

### Réplicas

- **Discovery Service** : 1 réplica (singleton)
- **Bases de données** : 1 réplica chacune
- **Microservices** : 2 réplicas chacun
- **Frontend** : 3 réplicas

## 🌐 Accès à l'application

### Via Ingress (recommandé)

- **Frontend** : http://volleyball.local
- **API** : http://api.volleyball.local
- **Eureka** : http://eureka.volleyball.local

### Via NodePort

- **Frontend** : http://localhost:30000
- **Gateway** : http://localhost:30002
- **Discovery** : http://localhost:30001

### Via LoadBalancer

Le service `volleyball-loadbalancer` expose tous les ports nécessaires.

## 📊 Monitoring et logs

### Vérifier le statut

```bash
# Voir tous les pods
kubectl get pods -n volleyball-platform

# Voir tous les services
kubectl get services -n volleyball-platform

# Voir l'ingress
kubectl get ingress -n volleyball-platform
```

### Consulter les logs

```bash
# Logs d'un service spécifique
kubectl logs -f deployment/auth-service -n volleyball-platform

# Logs d'un pod spécifique
kubectl logs -f [pod-name] -n volleyball-platform
```

### Accès aux pods

```bash
# Accès bash à un pod
kubectl exec -it [pod-name] -n volleyball-platform -- /bin/bash

# Accès aux logs en temps réel
kubectl logs -f [pod-name] -n volleyball-platform
```

## 🔍 Health Checks

Tous les services sont configurés avec des probes de santé :

- **Liveness Probe** : Vérifie que le service est vivant
- **Readiness Probe** : Vérifie que le service est prêt à recevoir du trafic

### Endpoints de santé

- **Discovery Service** : `/actuator/health`
- **Gateway Service** : `/actuator/health`
- **Microservices** : `/actuator/health`
- **Frontend** : `/`

## 🗄️ Bases de données

### Configuration

- **PostgreSQL 16 Alpine** pour toutes les bases
- **PersistentVolumeClaims** pour la persistance des données
- **1Gi** de stockage par base de données
- **Port 5432** pour toutes les bases

### Bases de données

1. **auth_db** : Authentification et autorisation
2. **medical_db** : Données médicales des joueurs
3. **performance_db** : Statistiques et performances
4. **planning_db** : Planification et événements
5. **finance_db** : Gestion financière
6. **admin_db** : Demandes administratives

## 🔐 Sécurité

### Secrets

- Tous les secrets sont stockés dans des `Secret` Kubernetes
- Les mots de passe sont encodés en base64
- Aucune information sensible n'est exposée dans les logs

### RBAC (optionnel)

Pour une sécurité renforcée, vous pouvez ajouter des rôles et permissions :

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: volleyball-platform
  name: volleyball-role
rules:
- apiGroups: [""]
  resources: ["pods", "services"]
  verbs: ["get", "list", "watch"]
```

## 🧹 Nettoyage

### Nettoyage automatique

```bash
./cleanup.sh
```

### Nettoyage manuel

```bash
# Supprimer le namespace complet
kubectl delete namespace volleyball-platform

# Ou supprimer fichier par fichier
kubectl delete -f ingress.yaml
kubectl delete -f frontend.yaml
kubectl delete -f microservices.yaml
kubectl delete -f databases.yaml
kubectl delete -f services.yaml
kubectl delete -f configmap.yaml
kubectl delete -f storage.yaml
kubectl delete -f secrets.yaml
kubectl delete -f namespace.yaml
```

## 🚨 Dépannage

### Problèmes courants

1. **Pods en état Pending**
   - Vérifier les ressources disponibles
   - Vérifier les PersistentVolumeClaims

2. **Services non accessibles**
   - Vérifier que les pods sont Running
   - Vérifier les selectors des services

3. **Bases de données non connectées**
   - Vérifier que les PVC sont Bound
   - Vérifier les variables d'environnement

4. **Ingress non fonctionnel**
   - Vérifier que NGINX Ingress Controller est installé
   - Vérifier la classe d'ingress

### Commandes de diagnostic

```bash
# Vérifier les événements
kubectl get events -n volleyball-platform

# Décrire un pod
kubectl describe pod [pod-name] -n volleyball-platform

# Vérifier les logs d'événements
kubectl get events -n volleyball-platform --sort-by='.lastTimestamp'
```

## 📈 Scaling

### Scaling horizontal

```bash
# Augmenter le nombre de réplicas d'un service
kubectl scale deployment auth-service --replicas=5 -n volleyball-platform

# Scaling automatique (HPA)
kubectl autoscale deployment auth-service --cpu-percent=80 --min=2 --max=10 -n volleyball-platform
```

### Scaling vertical

Modifiez les ressources dans les fichiers de déploiement et appliquez :

```bash
kubectl apply -f microservices.yaml
```

## 🔄 Mise à jour

### Rolling update

```bash
# Mettre à jour une image
kubectl set image deployment/auth-service auth-service=vb/auth-service:v2.0.0 -n volleyball-platform

# Vérifier le statut de la mise à jour
kubectl rollout status deployment/auth-service -n volleyball-platform
```

### Rollback

```bash
# Annuler la dernière mise à jour
kubectl rollout undo deployment/auth-service -n volleyball-platform

# Voir l'historique des déploiements
kubectl rollout history deployment/auth-service -n volleyball-platform
```

## 📚 Ressources additionnelles

- [Documentation Kubernetes officielle](https://kubernetes.io/docs/)
- [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/)
- [Spring Boot sur Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [PostgreSQL sur Kubernetes](https://kubernetes.io/docs/tutorials/stateful-application/postgresql/)

---

**🎯 Votre plateforme de volley-ball est maintenant prête pour la production sur Kubernetes !**
