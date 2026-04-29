# 💰 Application Gestion des Flux Financiers

**Version :** 2.1 FINAL
**Auteur :** Kenfack Romaric
**Superviseur :** Didier Servaye

---

## 📋 Description

Application web complète de gestion des flux financiers (recettes et dépenses) pour l'ASBL Terra Sana :
- **Gestion des clients** et leurs versements
- **Gestion des employés** et paiements personnel (salaires, primes)
- **Gestion des charges** avec référentiel de 19 charges réelles
- **Tableau de bord** avec graphiques et statistiques
- **Export Excel & CSV** des données
- **Traçabilité** complète (qui a créé quoi et quand)

---

## 🏗️ Architecture Technique

### Stack Technologique

```
┌─────────────────────────────────────────┐
│         DOCKER COMPOSE                  │
├─────────────────────────────────────────┤
│                                         │
│  ┌──────────┐  ┌──────────┐  ┌───────┐ │
│  │  MySQL   │  │  Spring  │  │Vue.js │ │
│  │   8.0    │  │  Boot 3  │  │   3   │ │
│  │(Port 3307)│ │(Port 8081)│ │(Port  │ │
│  │          │  │          │  │ 5173) │ │
│  └──────────┘  └──────────┘  └───────┘ │
│                                         │
└─────────────────────────────────────────┘
```

### Technologies Utilisées

**Backend :**
- ☕ Spring Boot 3.2.2 (Java 21)
- 🗄️ Spring Data JPA / Hibernate
- 🔐 Spring Security + JWT (stateless)
- 📊 Apache POI (export Excel)
- 🛠️ Lombok

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
- ✅ Ports libres : 3307 (MySQL), **8081** (Backend), 5173 (Frontend)

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
| **Frontend (Vue.js)** | http://localhost:5173 | username: `admin` / password: `admin123` |
| **Backend API** | http://localhost:8081/api | JWT via login |
| **MySQL** | localhost:3307 | user: `flux_user` / password: `flux_password` / db: `flux_financiers` |

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

# Reconstruire le backend (après modification du code Java)
docker-compose build --no-cache backend
docker-compose up -d

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
# Tester l'API
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Frontend

```bash
# Développer en dehors de Docker
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
5. **paiement_employe** - Salaires & primes
6. **charge** - Référentiel des charges avec traçabilité
7. **paiement_charge** - Paiements des charges

### Relations

- User → Client (1:N, created_by)
- User → Employe (1:N, created_by)
- User → Charge (1:N, created_by)
- Client → Versement (1:N)
- Employe → PaiementEmploye (1:N)
- Charge → PaiementCharge (1:N)

### Énumérations

- **RoleUser** : GESTIONNAIRE
- **StatutEmploye** : ACTIF, INACTIF
- **TypePaiementEmploye** : SALAIRE, PRIME
- **ModePaiement** : ESPECES, VIREMENT, CHEQUE, CARTE_BANCAIRE
- **TypeCharge** : VEHICULES, INFRASTRUCTURE, FISCALES_SOCIALES, SERVICES_EXTERNES

---

## 🔒 Sécurité

- 🔐 Authentification JWT (stateless — CSRF désactivé volontairement)
- 🔑 Mots de passe hashés avec BCrypt
- 🛡️ Spring Security
- 🚫 CORS configuré (localhost en développement)
- ✅ Validation des données (Bean Validation : @NotNull, @PastOrPresent, @DecimalMin)

---

## 📈 Fonctionnalités

### Module Clients
- ✅ CRUD clients avec traçabilité
- ✅ Enregistrement des versements (recettes)
- ✅ Historique par client
- ✅ Suppression bloquée si le client a des versements associés

### Module Employés
- ✅ CRUD employés avec traçabilité
- ✅ Gestion du statut (ACTIF/INACTIF)
- ✅ Enregistrement salaires & primes
- ✅ Seuls les employés ACTIFS peuvent recevoir un paiement
- ✅ Suppression bloquée si l'employé a des paiements associés
- ✅ Double classification (type + mode de paiement)

### Module Charges
- ✅ Référentiel de 19 charges réelles
- ✅ Catégorisation en 4 types métier
- ✅ Enregistrement des paiements de charges
- ✅ Traçabilité complète

### Règles de gestion
- ✅ Date de paiement : ne peut pas être dans le futur (frontend + backend)
- ✅ Mode de paiement : obligatoire pour tous les types de transaction
- ✅ Montant : doit être supérieur à 0

### Tableau de Bord
- 📊 4 KPIs : Total Recettes, Total Dépenses, Solde, Variation vs mois précédent
- 📊 Graphique Doughnut : répartition Personnel vs Charges
- 📊 Graphique Line : évolution recettes & dépenses sur 6 mois
- 📋 Dernières transactions filtrables (Ce mois / Trimestre / Année / Tout)
- ⚡ Actions rapides : nouveau versement, paiement charge, paiement personnel

### Historique & Exports
- 🔍 3 onglets : Recettes / Dépenses / Global avec solde calculé
- 🔍 Filtres avancés multi-critères (date, mode, montant min/max, type)
- 📥 Export Excel (.xlsx) : versements, paiements charges, paiements employés
- 📄 Export CSV (.csv) : mêmes données, format alternatif
- 🗓️ Filtrage par période optionnel sur les exports

---

## 🐛 Dépannage

### Les conteneurs ne démarrent pas

```bash
# Vérifier les logs
docker-compose logs

# Vérifier que les ports ne sont pas utilisés
netstat -an | grep 3307
netstat -an | grep 8081
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

### Problème de permissions (Linux/Mac)

```bash
sudo chown -R $USER:$USER .
```

---

## 📝 Notes de Développement

### Mode Développement

- ✅ **Backend** : Modifications Java nécessitent rebuild (`docker-compose build --no-cache backend && docker-compose up -d`)
- ✅ **Frontend** : Hot reload automatique (modifications visibles instantanément)
- ✅ **Base de données** : Données persistantes dans volume Docker

### Mode Production

```bash
# 1. Changer les secrets dans docker-compose.yml
#    - JWT_SECRET (valeur longue et aléatoire)
#    - MYSQL_ROOT_PASSWORD
#    - MYSQL_PASSWORD

# 2. Restreindre le CORS à votre domaine
# 3. Activer HTTPS
# 4. Changer le mot de passe admin par défaut
```

---

## 📚 Documentation Complète

- 📄 [Cahier des Charges v2.1](docs/Cahier_Charges_Flux_Financiers_v2.1.docx)
- 📐 [Diagramme de Classes UML](docs/Diagramme_Classes_v2.1_FINAL_COMPLET.puml)
- 🗄️ [MCD/MLD Complet](docs/MCD_MLD_v2.1_FINAL.txt)

---

## 🎓 Informations Stage

**Stagiaire :** Kenfack Romaric
**Entreprise :** ASBL Terra Sana
**Superviseur :** Didier Servaye
**Période :** Janvier - Mars 2026 (11 semaines)
**Objectif :** Développement complet d'une application de gestion des flux financiers

---

## 📞 Support

Pour toute question :
- 📧 Email : [votre.email@exemple.com]

---

## 🙏 Remerciements

Merci à Didier Servaye pour l'encadrement et les retours constructifs tout au long du stage.

---

## 📄 Licence

© 2026 - Projet de stage - Tous droits réservés

---

**🚀 Bon développement !**
