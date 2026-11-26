# Database Documentation — GestionCommerciale

## 1. Présentation

La base de données **GestionCommerciale** constitue le système central de gestion des informations de l'application.

Elle permet de gérer les utilisateurs, les rôles, les clients, les fournisseurs, les produits, les stocks, les ventes, les achats, les paiements et la facturation.

La base de données est conçue pour garantir la cohérence des informations, assurer la traçabilité des opérations commerciales et faciliter les échanges avec le backend Spring Boot.

## 2. Technologies utilisées

| Technologie | Utilisation |
|---|---|
| PostgreSQL 18 | Système de gestion de base de données relationnelle |
| SQL | Création et manipulation des données |
| Spring Boot | Communication entre le backend et la base de données |
| Spring Data JPA | Gestion de la persistance |
| Hibernate | Mapping objet-relationnel (ORM) |

## 3. Architecture de la base de données

La base de données est organisée autour de plusieurs domaines fonctionnels :

- **Gestion des utilisateurs et des accès :** utilisateurs et rôles.
- **Gestion des tiers :** clients et fournisseurs.
- **Gestion du catalogue :** produits, catégories, unités de mesure et taxes.
- **Gestion des stocks :** entrepôts, quantités disponibles et mouvements de stock.
- **Gestion commerciale :** ventes, achats et leurs lignes détaillées.
- **Gestion financière :** factures et paiements.

L'architecture relationnelle repose sur des clés primaires, des clés étrangères et des contraintes d'intégrité afin de maintenir la cohérence des données.

## 4. Organisation des tables

La base de données comporte 19 tables réparties selon les domaines fonctionnels suivants.

### 4.1. Gestion des utilisateurs et des rôles

| Table | Description |
|---|---|
| `utilisateur` | Stocke les informations des utilisateurs de l'application. |
| `role` | Contient les différents rôles disponibles. |
| `utilisateur_role` | Assure la relation entre les utilisateurs et leurs rôles. |

Rôles principaux :
- ADMIN
- GESTIONNAIRE
- VENDEUR

### 4.2. Gestion des tiers

| Table | Description |
|---|---|
| `tiers` | Centralise les informations des clients et des fournisseurs. |
| `tiers_role` | Définit les rôles attribués aux tiers. |

Cette organisation permet à un même tiers d'être associé à plusieurs fonctions commerciales.

### 4.3. Gestion des produits et du catalogue

| Table | Description |
|---|---|
| `produit` | Stocke les informations des produits commercialisés. |
| `categorie` | Organise les produits par catégories. |
| `unite_mesure` | Définit les unités utilisées pour quantifier les produits. |
| `taxe` | Contient les informations relatives aux taxes applicables. |

### 4.4. Gestion des stocks

| Table | Description |
|---|---|
| `entrepot` | Représente les lieux de stockage. |
| `stock` | Enregistre les quantités de produits disponibles par entrepôt. |
| `mouvement_stock` | Assure la traçabilité des entrées, sorties et ajustements de stock. |

### 4.5. Gestion des ventes et des achats

| Table | Description |
|---|---|
| `vente` | Enregistre les opérations de vente. |
| `ligne_vente` | Contient le détail des produits associés à chaque vente. |
| `achat` | Enregistre les opérations d'achat. |
| `ligne_achat` | Contient le détail des produits associés à chaque achat. |

### 4.6. Gestion financière et facturation

| Table | Description |
|---|---|
| `facture` | Stocke les informations relatives aux factures. |
| `ligne_facture` | Contient le détail des articles facturés. |
| `paiement` | Enregistre l'historique des paiements. |

## 5. Contraintes et intégrité des données

La base de données utilise plusieurs mécanismes pour garantir la fiabilité des informations :

- **Primary Key (PK) :** identifie chaque enregistrement de manière unique.
- **Foreign Key (FK) :** garantit la cohérence des relations entre les tables.
- **UNIQUE :** empêche les doublons sur les champs concernés.
- **NOT NULL :** impose la présence des valeurs obligatoires.
- **CHECK :** contrôle les valeurs autorisées selon les règles définies.
- **INDEX :** améliore les performances des recherches et des jointures.

Les contraintes et les index sont regroupés dans le script `02_constraints_indexes.sql`.

## 6. Règles de gestion principales

La conception de la base de données prend en compte les règles métier suivantes :

1. Un utilisateur peut posséder un ou plusieurs rôles.
2. Un tiers peut être associé à plusieurs fonctions, notamment client et fournisseur.
3. Les produits sont associés à des catégories, des unités de mesure et des taxes.
4. Les quantités en stock sont suivies par produit et par entrepôt.
5. Les mouvements de stock permettent de conserver un historique des opérations.
6. Les ventes et les achats sont détaillés à travers leurs lignes respectives.
7. Les paiements sont enregistrés afin de conserver leur historique.
8. Les opérations de vente doivent respecter la disponibilité du stock.
9. Les opérations commerciales doivent préserver la cohérence des données en cas d'erreur.

## 7. Organisation des scripts SQL

Les scripts SQL sont placés dans le dossier `scripts/`.

```text
database/
│
├── README.md
│
└── scripts/
    ├── 01_create_tables.sql
    ├── 02_constraints_indexes.sql
    └── 03_seed_data.sql
```

| Script | Description |
|---|---|
| `01_create_tables.sql` | Création des tables de la base de données. |
| `02_constraints_indexes.sql` | Ajout des contraintes d'intégrité et des index. |
| `03_seed_data.sql` | Insertion des données initiales nécessaires au fonctionnement de l'application. |

**Important :** le script de données initiales est destiné à l'initialisation de la base. Il ne doit pas être exécuté plusieurs fois sans vérifier les éventuels doublons.

## 8. Installation et configuration

### Prérequis

- PostgreSQL installé.
- Un accès à PostgreSQL avec les permissions nécessaires.
- Un client SQL comme pgAdmin ou psql.

### Étape 1 — Créer la base de données

```sql
CREATE DATABASE gestion_commerciale;
```

### Étape 2 — Se connecter à la base

Avec PostgreSQL :

```bash
psql -U postgres -d gestion_commerciale
```

### Étape 3 — Exécuter les scripts

Depuis la racine du projet :

```bash
psql -U postgres -d gestion_commerciale -f database/scripts/01_create_tables.sql

psql -U postgres -d gestion_commerciale -f database/scripts/02_constraints_indexes.sql

psql -U postgres -d gestion_commerciale -f database/scripts/03_seed_data.sql
```

L'ordre d'exécution doit être respecté afin de créer les tables avant leurs contraintes et d'insérer ensuite les données initiales.

## 9. Intégration avec le backend

Le backend de GestionCommerciale utilise Spring Boot et Spring Data JPA pour communiquer avec PostgreSQL.

La configuration de connexion est définie dans le fichier `application.properties`.

Les informations sensibles, notamment les identifiants de connexion, doivent être configurées à travers des variables d'environnement et ne doivent pas être publiées sur GitHub.

Hibernate utilise la validation du schéma (`ddl-auto=validate`) afin de vérifier la compatibilité entre les entités Java et la structure existante de la base de données.

## 10. Bonnes pratiques

- Respecter l'ordre d'exécution des scripts SQL.
- Ne pas modifier directement les données de production sans précaution.
- Effectuer régulièrement des sauvegardes.
- Conserver l'historique des mouvements de stock et des paiements.
- Protéger les informations d'authentification.
- Vérifier les contraintes avant toute modification du schéma.
- Tester les modifications sur un environnement de développement avant leur mise en production.

## 11. Sauvegarde et restauration

### Sauvegarder la base de données

```bash
pg_dump -U postgres -d gestion_commerciale -F c -f gestion_commerciale_backup.dump
```

### Restaurer une sauvegarde

```bash
pg_restore -U postgres -d gestion_commerciale gestion_commerciale_backup.dump
```

## 12. État du projet

La structure initiale de la base de données comprend les 19 tables décrites dans cette documentation.

Les différents modules applicatifs sont développés progressivement et intégrés au backend Spring Boot.

---

**Projet :** GestionCommerciale  
**Database Engine :** PostgreSQL 18  
**Backend :** Spring Boot  
**ORM :** Hibernate / Spring Data JPA