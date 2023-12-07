# Projet_BDDA (Mini SGBD)

* [Introdution](#introduction)
* [Installation](#installation)
* [Execution](#execution)
* [Utilisation](#utilisation)
* [Structure](#structure)
* [Licence](#licence)
* [Contributeurs](#contributeurs)

## Introduction

Le projet du Mini Système de Gestion de Base de Données (SGBD) développé en Java 
vise à fournir une implémentation simplifiée mais fonctionnelle des principaux composants d'un SGBD, 
offrant des fonctionnalités de gestion du stockage, des tampons mémoire et des commandes SQL de base.

### Objectif du projet

L'objectif principal de ce SGBD est d'offrir une base solide pour comprendre les concepts fondamentaux de la gestion de base de données.
Il comprend des composants tels que le gestionnaire de tampon, le gestionnaire de disque, le gestionnaire de fichiers et des commandes SQL essentielles.

### Fonctionnalités principales

1. Commande [CREATE TABLE](#commande-create-table) : La commande `CREATE TABLE` permet la création de nouvelles tables dans la base de données.
2. Commande [RESETDB](#commande-resetdb) : La commande `RESETDB` réinitialise la base de données, supprimant toutes les données existantes et rétablissant un état initial.
3. Commande [INSERT](#commande-insert) : La commande `INSERT` permet l'ajout de données dans une table existante.
4. Commande [SELECT](#commande-select) : La commande `SELECT` récupère des données d'une table en fonction de certaines conditions.
5. Commande [IMPORT](#commande-import) : La commande `IMPORT` permet l'insertion de données en masse dans une table (insertion par lot).
6. Commande [DELETE](#commande-delete) : La commande `DELETE` supprime des enregistrements d'une table en fonction de certaines conditions.
7. Commande [CREATEINDEX](#commande-createindex) : La commande `CREATEINDEX` crée un index sur une table pour améliorer les performances des opérations de recherche.
8. Commande [SELECTINDEX](#commande-selectindex) : La commande `SELECTINDEX` effectue une sélection en utilisant un index.
9. Commande [Jointure](#commande-jointure) : La commande de jointure permet de combiner les données de deux tables en fonction de certaines conditions.

## Installation

Pour installer et exécuter le SGBD suivez les étapes ci-dessous :

### Prérequis

* Git : Le système de contrôle de version Git est utilisé pour cloner le dépôt. Vous pouvez installer Git à partir de [git-scm.com](https://git-scm.com/downloads).
* Python : Si vous souhaitez utiliser le script Python, assurez-vous d'avoir Python installé. Vous pouvez télécharger Python sur [python.org](https://www.python.org/downloads/).

### Téléchargement du code source

Clonez le dépôt GitHub du projet en utilisant la commande suivante dans votre terminal :

```bash
git clone https://github.com/jbhochet/Projet_BDDA.git
```

## Execution

Pour exécuter le projet, utilisez le script Python pour compiler et exécuter le programme (sur tous les systèmes d'exploitation) : 

```commandline
python manager.py
```

## Utilisation

Voici comment utiliser les commandes dans le Mini SGBD. 
Assurez-vous de suivre le format général spécifié pour chaque type de commande.

### Commande CREATE TABLE

```sql
CREATE TABLE NomRelation (NomCol_1:TypeCol_1, NomCol_2:TypeCol_2, ..., NomCol_NbCol:TypeCol_NbCol)'
```

### Commande RESETDB

```sql
RESETDB
```

### Commande INSERT

```sql
INSERT INTO nomRelation VALUES (val1, val2, ..., valn)
```

### Commande SELECT

```sql
SELECT * FROM nomRelation WHERE condition1 AND condition2 AND ... AND conditionN
```

### Commande IMPORT

```sql
IMPORT INTO nomRelation nomFichier.csv
```

### Commande DELETE

```sql
DELETE FROM nomRelation WHERE condition1 AND condition2 AND ... AND conditionN
```

### Commande CREATEINDEX

```sql
CREATEINDEX ON nomRelation KEY=nomColonne ORDER=ordre
```

### Commande SELECTINDEX

```sql
SELECTINDEX * FROM nomRelation WHERE nomColonne=valeur
```

### Commande Jointure

```sql
SELECT * FROM nomRel1, nomRel2 WHERE condition1 AND condition2 AND ... AND conditionN
```

## Structure

Le projet est organisé en plusieurs répertoires et fichiers :

### [Code](https://github.com/jbhochet/Projet_BDDA/tree/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE) : Racine du projet

| Répertoire / Fichier                                                                                                            | Description                                                  |
|---------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| [src](https://github.com/jbhochet/Projet_BDDA/tree/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src)               | Contient le code source du projet                            |
| [manager.py](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/manager.py) | Contient le script Python pour la compilation et l'exécution |

### [src](https://github.com/jbhochet/Projet_BDDA/tree/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src) : Code source du projet

| Répertoire                                                                                                              | Description                                             |
|-------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| [main](https://github.com/jbhochet/Projet_BDDA/tree/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main) | Contient les classes principales du projet              |
| [test](https://github.com/jbhochet/Projet_BDDA/tree/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/test) | Contient les tests pour évaluer les différentes classes |

### [main](https://github.com/jbhochet/Projet_BDDA/tree/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main) : Classes principales du projet

| Fichier                                                                                                                                                            | Description                                                |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------|
| [BufferManager.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/BufferManager.java)           | Gestion du tampon mémoire                                  |
| [ColInfo.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/ColInfo.java)                       | Information sur les colonnes d'une table                   |
| [Condition.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/Condition.java)                   | Représentation des conditions utilisées dans les commandes |
| [ConditionUtil.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/ConditionUtil.java)           | Utilitaire pour évaluer les conditions                     |
| [CreateIndexCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/CreateIndexCommand.java) | Commande de création d'index                               |
| [CreateTableCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/CreateTableCommand.java) | Commande de création de table                              |
| [DatabaseInfo.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/DatabaseInfo.java)             | Informations sur la base de données                        |
| [DatabaseManager.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/DatabaseManager.java)       | Gestionnaire de base de données                            |
| [DataType.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/DataType.java)                     | Types de données pris en charge                            |
| [DBParams.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/DBParams.java)                     | Paramètres de la base de données                           |
| [DeleteCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/DeleteCommand.java)           | Commande de suppression d'enregistrements                  |
| [DiskManager.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/DiskManager.java)               | Gestion du disque                                          |
| [FileManager.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/FileManager.java)               | Gestionnaire de fichiers                                   |
| [Frame.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/Frame.java)                           | Représentation d'une page dans le tampon mémoire           |
| [ICommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/ICommand.java)                     | Interface pour les commandes                               |
| [ImportCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/ImportCommand.java)           | Commande d'importation de données                          |
| [InsertIntoCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/InsertIntoCommand.java)   | Commande d'insertion de données                            |
| [Main.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/Main.java)                             | Classe principale avec la fonction `main`                  |
| [PageId.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/PageId.java)                         | Identifiant de page                                        |
| [Record.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/Record.java)                         | Représentation d'un enregistrement                         |
| [RecordId.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/RecordId.java)                     | Identifiant d'enregistrement                               |
| [RecordIterator.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/RecordIterator.java)         | Itérateur sur les enregistrements                          |
| [ResetDBCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/ResetDBCommand.java)         | Commande de réinitialisation de la base de données         |
| [SelectCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/SelectCommand.java)           | Commande de sélection de données                           |
| [SelectIndexCommand.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/SelectIndexCommand.java) | Commande de sélection avec utilisation d'index             |
| [TableInfo.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/main/TableInfo.java)                   | Informations sur une table                                 |

### [test](https://github.com/jbhochet/Projet_BDDA/tree/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/test) : Tests pour évaluer les différentes classes

| Fichier                                                                                                                                                          | Description                                                             |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| [TestBufferManager.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/test/TestBufferManager.java) | Test pour évaluer le fonctionnement du gestionnaire de tampon mémoire   |
| [TestDatabaseInfo.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/test/TestDatabaseInfo.java)   | Test pour évaluer le fonctionnement des informations de base de données |
| [TestDiskManager.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/test/TestDiskManager.java)     | Test pour évaluer le fonctionnement du gestionnaire de disque           |
| [TestFileManager.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/test/TestFileManager.java)     | Test pour évaluer le fonctionnement du gestionnaire de fichiers         |
| [TestRecord.java](https://github.com/jbhochet/Projet_BDDA/blob/main/PROJET_BDDA_FARZOLLAHI_HANNACHIAN_ALLAIN_HOCHET/CODE/src/test/TestRecord.java)               | Test pour évaluer le fonctionnement des enregistrements                 |

## Licence

Projet_BDDA (Mini SGBD) est sous licence [MIT](https://github.com/jbhochet/Projet_BDDA/blob/main/LICENSE).

## Contributeurs

* [Jean-Baptiste Hochet](https://github.com/jbhochet)
* [Yanis Allain](https://github.com/Kemoory)
* [Sepanta Farzollahi](https://github.com/sepanta007)
* [Hagop Hannachian](https://github.com/hagop-h)