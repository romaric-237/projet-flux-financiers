# 💰 Application Gestion des Flux Financiers

**Version :** 2.1 FINAL  
**Auteur :** Kenfack Romaric  
**Superviseur :** Didier Servaye  
**Période :** Janvier - Mars 2026 (Stage 11 semaines)

---

## 📋 Description

Application web complète de gestion des flux financiers (recettes et dépenses) avec :
- **Gestion des clients** et leurs versements
- **Gestion des employés** et paiements personnel (salaires, primes)
- **Gestion des charges** avec référentiel de 19 charges réelles
- **Tableau de bord** avec graphiques et statistiques
- **Export Excel** complet
- **Traçabilité** complète (qui a créé quoi et quand)

---

## 🏗️ Architecture Technique

### Stack Technologique

```
┌─────────────────────────────────────────┐
│         DOCKER COMPOSE                  │
├─────────────────────────────────────────┤
│                                         │
│  ┌──────────┐  ┌──────────┐  ┌───────┐│
│  │  MySQL   │  │  Spring  │  │ Vue.js││
│  │    8.0   │  │  Boot 3  │  │   3   ││
│  │(Port 3307)│ │ (Port    │  │(Port  ││
│  │          │  │  8080)   │  │ 5173) ││
│  └──────────┘  └──────────┘  └───────┘│
│                                         │
└─────────────────────────────────────────┘
```

### Technologies Utilisées

**Backend :**
- ☕ Spring Boot 3.2.2 (Java 21)
- 🗄️ Spring Data JPA / Hibernate
- 🔐 Spring Security + JWT
- 📊 Apache POI (export Excel)
- 🛠️ Lombok + MapStruct

**Frontend :**
- 🖼️ Vue.js 3.4 (Composition API)
- 🚀 Vite 5
- 🗂️ Pinia (state management)
- 📡 Axios (HTTP client)
- 📈 Chart.js + vue-chartjs
- 🎨 CSS moderne (responsive)

**Base de données :**
- 🐬 MySQL 8.0
- 7 tables
- 5 énumérations
- 13 relations
- Vues SQL pour reporting

**DevOps :**
- 🐳 Docker & Docker Compose
- 📦 Multi-stage builds
- 🔄 Hot reload (dev mode)
- 💾 Volumes persistants

---

## 🚀 Installation et Démarrage

### Prérequis

- ✅ Docker installé ([Docker Desktop](https://www.docker.com/products/docker-desktop/))
- ✅ Docker Compose installé (inclus dans Docker Desktop)
- ✅ Au minimum 4GB RAM disponible
- ✅ Ports libres : 3307 (MySQL), 8080 (Backend), 5173 (Frontend)

### Démarrage Rapide (3 commandes)

```bash
# 1. Cloner ou télécharger le projet
cd projet-flux-financiers

# 2. Lancer TOUTE l'application avec Docker Compose
docker-compose up -d

# 3. Vérifier que tout est démarré
docker-compose ps
```

**C'est tout ! 🎉**

### Accès aux services

Une fois démarré :

| Service | URL | Credentials |
|---------|-----|-------------|
| **Frontend (Vue.js)** | http://localhost:5173 | username: `admin`<br>password: `admin123` |
| **Backend API** | http://localhost:8080/api | JWT via login |
| **MySQL** | localhost:3307 | user: `flux_user`<br>password: `flux_password`<br>database: `flux_financiers` |

### Données Initiales

L'application démarre avec :
- ✅ 1 utilisateur : `admin` / `admin123`
- ✅ 19 charges dans le référentiel :
  - 11 VEHICULES (Essence Fiat/Ford, Assurances, etc.)
  - 3 INFRASTRUCTURE (Entrepôt, Téléphone, GPS)
  - 4 FISCALES_SOCIALES (TVA, Afsca, Congé Payé, Assurance Perso)
  - 1 SERVICES_EXTERNES (Comptable)

---

## 📂 Structure du Projet

```
projet-flux-financiers/
│
├── docker-compose.yml          # Orchestration des 3 services
│
├── database/
│   └── init.sql               # Script d'initialisation MySQL
│
├── backend/                   # Spring Boot
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/fluxfinanciers/
│       │   │   ├── entity/    # Entités JPA
│       │   │   ├── repository/
│       │   │   ├── service/
│       │   │   ├── controller/
│       │   │   ├── security/  # JWT + Spring Security
│       │   │   ├── dto/
│       │   │   └── config/
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│
└── frontend/                  # Vue.js 3
    ├── Dockerfile
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── views/            # Pages
        ├── components/       # Composants réutilisables
        ├── stores/           # Pinia stores
        ├── services/         # API services
        ├── router/
        └── assets/
```

---

## 🛠️ Commandes Utiles

### Gestion Docker

```bash
# Démarrer tous les services
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Voir les logs en temps réel
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql

# Reconstruire les images (après modification du code)
docker-compose up -d --build

# Arrêter et supprimer TOUT (y compris les volumes)
docker-compose down -v

# Voir l'état des conteneurs
docker-compose ps

# Entrer dans un conteneur
docker exec -it flux-financiers-backend sh
docker exec -it flux-financiers-mysql bash
```

### Accès MySQL en ligne de commande

```bash
# Se connecter à MySQL
docker exec -it flux-financiers-mysql mysql -u flux_user -pflux_password flux_financiers

# Une fois connecté :
SHOW TABLES;
SELECT * FROM charge;
SELECT * FROM user;
```

### Backend

```bash
# Tester l'API (exemples)
curl http://localhost:8080/api/health
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Frontend

```bash
# Si vous voulez développer en dehors de Docker
cd frontend
npm install
npm run dev
```

---

## 📊 Modèle de Données

### Tables

1. **user** - Utilisateurs (gestionnaires)
2. **client** - Clients avec traçabilité
3. **versement** - Recettes clients
4. **employe** - Employés avec traçabilité
5. **paiement_personnel** - Salaires & primes
6. **charge** - Référentiel des charges avec traçabilité
7. **paiement_charge** - Paiements des charges

### Relations

- User → Client (1:N, created_by)
- User → Employe (1:N, created_by)
- User → Charge (1:N, created_by)
- Client → Versement (1:N)
- Employe → PaiementPersonnel (1:N)
- Charge → PaiementCharge (1:N)

### Énumérations

- **RoleUser** : GESTIONNAIRE
- **StatutEmploye** : ACTIF, INACTIF
- **TypePaiementPersonnel** : SALAIRE, PRIME
- **ModePaiement** : ESPECES, VIREMENT, CHEQUE, CARTE_BANCAIRE
- **TypeCharge** : VEHICULES, INFRASTRUCTURE, FISCALES_SOCIALES, SERVICES_EXTERNES

---

## 🔒 Sécurité

- 🔐 Authentification JWT
- 🔑 Mots de passe hashés avec BCrypt
- 🛡️ Spring Security
- 🚫 CORS configuré
- 🔒 Protection CSRF
- ✅ Validation des données (Bean Validation)

---

## 📈 Fonctionnalités

### Module Clients
- ✅ CRUD clients avec traçabilité
- ✅ Enregistrement des versements (recettes)
- ✅ Historique par client

### Module Employés
- ✅ CRUD employés avec traçabilité
- ✅ Gestion du statut (ACTIF/INACTIF)
- ✅ Enregistrement salaires & primes
- ✅ Double classification (type + mode de paiement)

### Module Charges
- ✅ Référentiel de 19 charges réelles
- ✅ Catégorisation en 4 types métier
- ✅ Enregistrement des paiements de charges
- ✅ Traçabilité complète

### Tableau de Bord
- 📊 Graphique évolution temporelle (6 mois)
- 📊 Répartition dépenses Personnel vs Charges
- 📊 Répartition charges par catégorie
- 📊 Top 5 charges les plus coûteuses
- 💰 Synthèse financière globale

### Historique & Exports
- 🔍 Filtres avancés multi-critères
- 📥 Export Excel complet (5 feuilles)
- 📋 Rapport de synthèse

---

## 🐛 Dépannage

### Les conteneurs ne démarrent pas

```bash
# Vérifier les logs
docker-compose logs

# Vérifier que les ports ne sont pas utilisés
netstat -an | grep 3307
netstat -an | grep 8080
netstat -an | grep 5173

# Nettoyer et redémarrer
docker-compose down -v
docker-compose up -d --build
```

### Le backend ne se connecte pas à MySQL

```bash
# Vérifier que MySQL est prêt
docker-compose logs mysql

# Attendre quelques secondes et relancer le backend
docker-compose restart backend
```

### Problème de permissions

```bash
# Sur Linux/Mac, ajuster les permissions
sudo chown -R $USER:$USER .
```

---

## 📝 Notes de Développement

### Mode Développement

Les fichiers sont montés en volumes, donc :
- ✅ **Backend** : Modifications Java nécessitent rebuild (`docker-compose up -d --build backend`)
- ✅ **Frontend** : Hot reload automatique (modifications visibles instantanément)
- ✅ **Base de données** : Données persistantes dans volume Docker

### Mode Production

Pour déployer en production :

```bash
# 1. Changer les secrets dans docker-compose.yml
#    - JWT_SECRET
#    - MYSQL_ROOT_PASSWORD
#    - MYSQL_PASSWORD

# 2. Utiliser les Dockerfiles de production (build frontend)
# 3. Désactiver CORS ou le restreindre
# 4. Activer HTTPS
```

---

## 📚 Documentation Complète

- 📄 [Cahier des Charges v2.1](docs/Cahier_Charges_Flux_Financiers_v2.1.docx)
- 📐 [Diagramme de Classes UML](docs/Diagramme_Classes_v2.1_FINAL_COMPLET.puml)
- 🗄️ [MCD/MLD Complet](docs/MCD_MLD_v2.1_FINAL.txt)
- 📋 [Résumé des Modifications](docs/Resume_v2.1_FINAL.txt)

---

## 🎓 Informations Stage

**Stagiaire :** Kenfack Romaric  
**Entreprise :** [Nom de l'entreprise]  
**Superviseur :** Didier Servaye  
**Période :** Janvier - Mars 2026 (11 semaines)  
**Objectif :** Développement complet d'une application de gestion des flux financiers

---

## 📞 Support

Pour toute question :
- 📧 Email : [votre.email@exemple.com]
- 💬 Issues GitHub : [lien si applicable]

---

## 🙏 Remerciements

Merci à Didier Servaye pour l'encadrement et les retours constructifs tout au long du stage.

---

## 📄 Licence

© 2026 - Projet de stage - Tous droits réservés

---

**🚀 Bon développement !**
