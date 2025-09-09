# 🏗️ Gestion Matériel - Application Cliente Swing

## 📋 Description

Application desktop moderne en Java Swing pour la gestion de matériel et réservations. Interface utilisateur élégante avec FlatLaf, support drag & drop, et connectivité Backend API / Mock JSON.

### 🎯 Fonctionnalités Principales

- **Planning hebdomadaire interactif** avec drag & drop des ressources
- **Vue calendrier jour avec timeline** pour visualiser les interventions d'une journée
- **Gestion complète des devis** avec workflow Devis → Commande → BL → Facture
- **Interface moderne** avec FlatLaf Light et palette de couleurs cohérente
- **Mode dual** : Backend API Spring Boot ou Mock JSON persisté
- **Raccourcis clavier** : Suppr (supprimer), Ctrl+D (dupliquer)
- **Menu contextuel** complet sur toutes les entités
- **Server-Sent Events** pour les notifications temps réel
- **Cartes interventions** enrichies avec statuts colorés
- **Filtrage des ressources par type** et ajustement visuel des horaires


## 🛠️ Technologies

- **Java 17**
- **Swing** avec **FlatLaf 3.2.5** (Look & Feel moderne)
- **Jackson** pour la sérialisation JSON
- **HTTP Client JDK** pour les appels API
- **Maven** pour la gestion des dépendances
- **JUnit 5** pour les tests

## 🚀 Installation et Démarrage

### Prérequis

- JDK 17 ou supérieur
- Maven 3.8+
- Backend Spring Boot (optionnel, mode Mock disponible)

### 1. Compilation

```bash
git clone [repository-url]
cd gestion-materiel-client
mvn clean compile
```

### 2. Exécution en mode développement

```bash
mvn exec:java -Dexec.mainClass="com.materiel.client.GestionMaterielApp"
```

### 3. Création du JAR exécutable

```bash
mvn clean package
java -jar target/gestion-materiel-client.jar
```

### 4. Création d'un installateur natif (optionnel)

```bash
mvn clean package jpackage:jpackage
# L'installateur sera dans target/dist/
```

## 🎨 Interface Utilisateur

### Palette de Couleurs

- **Bleu Principal** : `#3B82F6` (Éléments primaires)
- **Orange Secondaire** : `#F97316` (Accent et warnings)  
- **Vert Succès** : `#10B981` (Validations et disponibilité)
- **Violet Accent** : `#8B5CF6` (Ressources spécialisées)
- **Gris Neutre** : `#64748B` (Textes secondaires)

### Navigation

**Menu Latéral :**
- 📅 **Planning** - Vue hebdomadaire des interventions
- 📋 **Devis** - Gestion des devis clients
- 📦 **Commandes** - Suivi des bons de commande
- 🚚 **Bons de livraison** - Gestion des livraisons
- 🧾 **Factures** - Facturation clients
- 👥 **Clients** - Base clients
- 🏗️ **Ressources** - Matériel et personnel

## 📅 Planning - Guide d'utilisation

### Fonctionnalités Principales

1. **Navigation temporelle**
   - Boutons "Semaine précédente/suivante"
   - Bouton "Aujourd'hui" pour retour rapide
   - Affichage de la période courante

2. **Gestion des ressources**
   - Liste des ressources disponibles à gauche
   - Cartes colorées par type (Grue, Camion, Chauffeur, etc.)
   - Indicateur de disponibilité (🟢 Disponible / 🔴 Indisponible)

3. **Drag & Drop d'interventions**
   - Glisser une ressource vers une case jour/ressource
   - Création automatique d'intervention
   - Détection de conflits (erreur 409)

4. **Cartes d'intervention**
   - Pastille de statut colorée
   - Informations client et heures
   - Notes et adresse d'intervention
   - Double-clic pour détails complets

### Raccourcis Clavier

- **Suppr** : Supprimer l'intervention sélectionnée
- **Ctrl+D** : Dupliquer l'intervention
- **Entrée** : Afficher les détails
- **Clic droit** : Menu contextuel complet

### Gestes UI

- **Ctrl+Molette** : zoom horaire (5/15/30/60 min)
- **Poignées haut/bas** : redimensionner un créneau
- **Alt+Drag** : dupliquer une intervention
- **Shift+Click / Glisser** : sélection multiple

### Menu Contextuel

- ✏️ **Modifier** - Édition de l'intervention
- 📋 **Dupliquer** - Copie avec nouveaux créneaux
- ➡️ **Transformer en devis** - Génération automatique
- 🗑️ **Supprimer** - Avec confirmation

## 📋 Devis - Fonctionnalités

### Liste des Devis

- **Table interactive** avec tri par colonnes
- **Recherche en temps réel** par numéro/client
- **Filtrage par statut** et période
- **Calculs automatiques** HT/TVA/TTC

### Statuts des Devis

- 🔵 **Brouillon** - En cours de rédaction
- 🟠 **Envoyé** - Transmis au client
- 🟢 **Accepté** - Validé par le client
- 🔴 **Refusé** - Rejeté par le client
- ⚫ **Expiré** - Date de validité dépassée

### Workflow Documentaire

```
📋 Devis (Accepté) 
    ↓
📦 Bon de Commande
    ↓
🚚 Bon de Livraison
    ↓
🧾 Facture
```

## ⚙️ Configuration - Modes de Fonctionnement

### 🌐 Mode Backend API

**Connexion au serveur Spring Boot**
- Authentification JWT automatique
- Synchronisation temps réel
- Gestion des conflits centralisée
- SSE pour les notifications

**Configuration :**
```bash
# URL du backend (par défaut)
api.base.url=http://localhost:8080

# Authentification
jwt.auto.refresh=true
```

### 📁 Mode Mock JSON

**Données locales persistées**
- Stockage dans `~/.gestion-materiel/data/`
- Fichiers JSON : `resources.json`, `interventions.json`, `clients.json`
- Sauvegarde automatique à chaque modification
- Données d'exemple créées au premier lancement

**Avantages :**
- ✅ Fonctionne hors ligne
- ✅ Développement et démonstration
- ✅ Données persistées entre sessions
- ✅ Performance optimale

## 🗂️ Structure du Projet

```
src/
├── main/
│   ├── java/
│   │   └── com/materiel/client/
│   │       ├── config/           # Configuration application
│   │       ├── controller/       # EventBus et événements
│   │       ├── mock/             # Gestionnaire données JSON
│   │       ├── model/            # DTOs et modèles métier
│   │       ├── service/          # Services API/Mock
│   │       ├── util/             # Utilitaires Swing
│   │       └── view/             # Interface utilisateur
│   │           ├── components/   # Composants réutilisables
│   │           ├── devis/        # Panels devis
│   │           └── planning/     # Planning hebdomadaire
│   └── resources/
│       ├── icons/                # Icônes de l'application
│       ├── logback.xml           # Configuration logging
│       └── application.properties
└── test/
    └── java/                     # Tests unitaires et UI
```

## 🧪 Tests

### Exécution des Tests

```bash
# Tests unitaires
mvn test

# Tests avec couverture
mvn test jacoco:report
# Rapport dans target/site/jacoco/index.html

# Tests UI Swing (AssertJ-Swing)
mvn test -Dtest="*UITest"
```

### Types de Tests

- **Tests unitaires** : Logique métier et services
- **Tests d'intégration** : API et persistance JSON
- **Tests UI** : Interface Swing avec AssertJ-Swing
- **Tests de performance** : Chargement de grandes listes

## 📦 Déploiement

### JAR Autonome

```bash
mvn clean package
java -jar target/gestion-materiel-client.jar
```

### Installateur Windows

```bash
mvn clean package jpackage:jpackage -Pwindows
# Génère un .msi dans target/dist/
```

### Installateur macOS

```bash
mvn clean package jpackage:jpackage -Pmacos
# Génère un .dmg dans target/dist/
```

### Installateur Linux

```bash
mvn clean package jpackage:jpackage -Plinux
# Génère un .deb/.rpm dans target/dist/
```

## 🔧 Configuration Avancée

### Variables d'Environnement

```bash
# Mode de données par défaut
MATERIEL_DATA_MODE=BACKEND_API|MOCK_JSON

# URL du backend
MATERIEL_API_URL=http://localhost:8080

# Niveau de log
LOG_LEVEL=DEBUG|INFO|WARN|ERROR

# Répertoire de données Mock
MATERIEL_DATA_DIR=~/.gestion-materiel/data
```

### Fichier de Configuration

```properties
# application.properties

# Interface
ui.theme=FlatLaf Light
ui.language=fr_FR
ui.startup.maximized=true

# Données
data.auto.save=true
data.backup.enabled=true
data.backup.retention.days=30

# Performance  
ui.table.lazy.loading=true
ui.planning.cache.size=1000
```

## 🚀 Fonctionnalités Avancées

### 📊 Indicateurs de Performance

- **Taux d'occupation** des ressources
- **Chiffre d'affaires** par période
- **Délais moyens** de livraison
- **Statistiques client** détaillées

### 🔔 Notifications

- **SSE en temps réel** (mode Backend)
- **Alertes visuelles** dans l'interface
- **Son système** pour événements critiques
- **Log des actions** utilisateur

### 📈 Vues Métier

- **Vue Gantt** pour le suivi projet
- **Calendrier mensuel** avec synthèse
- **Tableau de bord** exécutif
- **Rapports PDF** intégrés

### ⌨️ Raccourcis Avancés

- **Ctrl+N** : Nouvelle intervention
- **Ctrl+S** : Sauvegarde rapide  
- **Ctrl+F** : Recherche globale
- **Ctrl+R** : Actualiser les données
- **F5** : Rafraîchir la vue courante
- **Ctrl+Q** : Quitter l'application

## 🐛 Dépannage

### Problèmes Courants

**L'application ne démarre pas :**
```bash
# Vérifier Java 17+
java -version

# Vérifier les permissions
ls -la ~/.gestion-materiel/

# Mode debug
java -Dlog.level=DEBUG -jar gestion-materiel-client.jar
```

**Erreur de connexion Backend :**
```bash
# Tester la connectivité
curl -I http://localhost:8080/api/system/ping

# Vérifier les logs
tail -f ~/.gestion-materiel/logs/application.log
```

**Données Mock corrompues :**
```bash
# Réinitialiser les données
rm -rf ~/.gestion-materiel/data/
# Redémarrer l'application pour recréer les données par défaut
```

### Logs et Diagnostic

```bash
# Répertoire des logs
~/.gestion-materiel/logs/
├── application.log      # Log principal
├── error.log           # Erreurs uniquement  
├── performance.log     # Métriques de performance
└── ui-events.log       # Événements interface
```

## 📞 Support

### Ressources d'Aide

- **Documentation complète** : `/docs/`
- **Exemples d'utilisation** : `/examples/`
- **FAQ** : `/docs/faq.md`
- **Guide de migration** : `/docs/migration.md`

### Contribution

```bash
# Cloner le projet
git clone [repository-url]

# Créer une branche feature
git checkout -b feature/nouvelle-fonctionnalite

# Tests avant commit
mvn clean test

# Commit avec convention
git commit -m "feat: ajout nouvelle fonctionnalité"
```

---

**Version :** 1.0.0  
**Équipe :** Chef de Projet, UI Designer, Développeur Swing Senior  
**Licence :** Propriétaire  
**Contact :** support@gestion-materiel.com 
