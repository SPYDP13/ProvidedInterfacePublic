# Bluent Interfaces Module

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](http://148.230.116.99:8081/repository/NebryonPublicModules/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)

Module Spring Boot/Kotlin fournissant des interfaces génériques pour le provisionnement automatique de routes REST standardisées avec support de pagination, synchronisation de données et gestion de permissions.

## 📋 Table des matières

- [Description](#-description)
- [Fonctionnalités](#-fonctionnalités)
- [Installation](#-installation)
- [Guide de démarrage rapide](#-guide-de-démarrage-rapide)
- [Plugin IntelliJ - Génération automatique des couches](#-plugin-intellij---génération-automatique-des-couches)
- [Routes REST disponibles](#-routes-rest-disponibles)
- [Exemples d'utilisation](#-exemples-dutilisation)
- [Configuration](#-configuration)
- [Architecture](#-architecture)
- [Gestion des permissions](#-gestion-des-permissions)
- [Publication](#-publication)

## 📋 Description

Ce module est une bibliothèque réutilisable qui permet de créer rapidement des API REST CRUD complètes avec des fonctionnalités avancées. Il fournit des interfaces génériques pour les contrôleurs, services et repositories basés sur Spring Data JPA, réduisant considérablement le code boilerplate nécessaire pour créer des APIs REST standardisées.

## ✨ Fonctionnalités

- ✅ **Routes REST automatiques** - Génération automatique des endpoints CRUD
- 📄 **Pagination intégrée** - Support natif de la pagination avec `PagingRequest`
- 🗑️ **Soft Delete** - Suppression logique des entités
- 🔄 **Synchronisation Online/Offline** - Mécanismes de sync pour applications mobiles
- 🔐 **Gestion des permissions** - Contrôle d'accès via annotations
- 📦 **Création en batch** - Support de la création multiple avec gestion d'erreurs individuelles
- 🏗️ **Architecture générique** - Interfaces réutilisables pour tous vos modèles

## 🚀 Installation

### Prérequis

- Java 21+
- Kotlin 1.9.25+
- Spring Boot 3.4.5+
- Gradle

### Ajouter la dépendance

Ajoutez le repository Nexus dans votre `build.gradle.kts` :

```kotlin
repositories {
    maven {
        name = "nexus"
        url = uri("http://148.230.116.99:8081/repository/NebryonPublicModules/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    implementation("com.nebryon.modules:interfaces-module:version")
}
```

## 🚀 Guide de démarrage rapide

### 1. Créer votre modèle

Votre entité doit implémenter `BluentGenericModel` :

```kotlin
@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var id: UUID? = null,
    
    var name: String,
    var price: Double,
    
    override var isDeleted: Boolean = false,
    override var createdAt: LocalDateTime = LocalDateTime.now(),
    override var updatedAt: LocalDateTime? = null,
    override var deletedAt: LocalDateTime? = null
) : BluentGenericModel<UUID, ProductDTO, ProductResponse> {
    
    override fun toDTO(): ProductDTO = ProductDTO(id, name, price)
    
    override fun toResponse(): ProductResponse = ProductResponse(
        id = id!!,
        name = name,
        price = price,
        createdAt = createdAt
    )
}
```

### 2. Créer votre DTO et Response

```kotlin
data class ProductDTO(
    val id: UUID? = null,
    val name: String,
    val price: Double
)

data class ProductResponse(
    val id: UUID,
    val name: String,
    val price: Double,
    val createdAt: LocalDateTime
)
```

### 3. Créer votre repository

```kotlin
interface ProductRepository : BluentGenericRepository<Product, UUID>
```

### 4. Créer votre service

```kotlin
@Service
class ProductServiceImpl : BluentGenericService<ProductDTO, ProductResponse, UUID, Product, ProductRepository> {
    
    override fun create(dto: ProductDTO): ProductResponse {
        val product = Product(
            name = dto.name,
            price = dto.price
        )
        return repo.save(product).toResponse()
    }
    
    override fun update(dto: ProductDTO): ProductResponse {
        val product = repo.findByIdAndIsDeleted(dto.id!!)
            ?: throw IllegalArgumentException("Product not found")
        
        product.name = dto.name
        product.price = dto.price
        product.updatedAt = LocalDateTime.now()
        
        return repo.save(product).toResponse()
    }
}
```

### 5. Créer votre contrôleur

```kotlin
@RestController
@RequestMapping("/api/products")
class ProductController(
    override var service: ProductServiceImpl
) : BluentGenericController<ProductDTO, ProductResponse, UUID, Product, ProductRepository, ProductServiceImpl>
```

**C'est tout !** Votre API REST complète est maintenant prête avec toutes les routes CRUD.

## 🔌 Plugin IntelliJ - Génération automatique des couches

Un plugin IntelliJ est disponible pour générer automatiquement les couches (Repository, Service, Controller) à partir de vos modèles de classe. Ce plugin simplifie considérablement la création des différentes couches de votre architecture.

### Installation du plugin IntelliJ

1. **Télécharger le plugin**
   - Le plugin est disponible sous forme de fichier ZIP : `generateNebryonOperations.zip`

2. **Installer le plugin dans IntelliJ IDEA**
   - Ouvrez IntelliJ IDEA
   - Allez dans **Settings/Preferences** (⌘, sur Mac ou Ctrl+Alt+S sur Windows/Linux)
   - Naviguez vers **Plugins**
   - Cliquez sur l'icône ⚙️ (engrenage) à côté de l'onglet "Installed"
   - Sélectionnez **"Install Plugin from Disk..."**
   - Choisissez le fichier `generateNebryonOperations.zip`
   - Redémarrez IntelliJ IDEA si nécessaire

3. **Configurer la tâche Gradle**

   Le plugin utilise une tâche Gradle pour effectuer la génération. Vous devez configurer votre projet Gradle pour y avoir accès.

   **a) Ajouter le plugin dans `build.gradle.kts` :**

   ```kotlin
   plugins {
       // ... vos autres plugins ...
       id("com.nebryon.generic-generator") version "1.0.12"
   }
   ```

   **b) Configurer le repository dans `settings.gradle.kts` :**

   ```kotlin
   pluginManagement {
       repositories {
           maven {
               url = uri("http://148.230.116.99:8081/repository/NebryonModule/")
               isAllowInsecureProtocol = true
           }
           gradlePluginPortal() // fallback
       }
   }
   ```

   > **Note :** Le code source de la tâche Gradle est disponible sur GitHub : [NebryonGenericOperationsGeneratorGradleTask](https://github.com/SPYDP13/NebryonGenericOperationsGeneratorGradleTask.git)

### Utilisation du plugin

Une fois le plugin installé et la tâche Gradle configurée :

1. **Créer votre modèle de classe**
   - Créez votre entité implémentant `BluentGenericModel`

2. **Générer les couches automatiquement**
   - Faites un **clic droit** sur votre fichier de modèle dans l'explorateur de projet
   - Dans le menu contextuel, sélectionnez **"Generate Nebryon Generic Operation"** (en bas du menu)
   - Le plugin va automatiquement générer :
     - Le **Repository** (interface étendant `BluentGenericRepository`)
     - L'**interface Service** (implémentant `BluentGenericService`)
     - L'**implémentation du Service** (classe concrète)
     - Le **Controller** (classe étendant `BluentGenericController`)

### Configuration des chemins de génération

Pour personnaliser les chemins de génération des couches, vous pouvez configurer les propriétés suivantes dans votre fichier `application.properties` :

```properties
app.basePackage=com.packageDeVotreProjet.votreProjet
app.repoPackage=com.packageDeVotreProjet.votreProjet.repository
app.servicePackage=com.packageDeVotreProjet.votreProjet.service
app.controllerPackage=com.packageDeVotreProjet.votreProjet.controller
```

### Comportement par défaut

Si ces propriétés ne sont pas précisées dans `application.properties`, le plugin utilisera le comportement par défaut suivant :
- Les couches seront créées dans le répertoire du projet selon la structure : `projetdirectory/nomCouches`
  - Exemple : `projetdirectory/repository`, `projetdirectory/service`, `projetdirectory/controller`

Le plugin respectera les chemins configurés dans `application.properties` ou utilisera la structure par défaut si aucune configuration n'est fournie.

## 📡 Routes REST disponibles

Le module `BluentGenericController` provisionne automatiquement les routes suivantes :

| Méthode | Route | Description | Permission |
|---------|-------|-------------|------------|
| POST | `/create` | Créer une entité | `create` |
| POST | `/createMulti` | Créer plusieurs entités en batch | `create` |
| POST | `/update` | Mettre à jour une entité | `update` |
| GET | `/getAll` | Récupérer toutes les entités | `read` |
| GET | `/getById/{id}` | Récupérer une entité par son ID | `read` |
| POST | `/getAllWithPaging` | Récupérer les entités avec pagination | `read` |
| DELETE | `/delete/{id}` | Supprimer une entité (soft delete) | `delete` |
| DELETE | `/deleteAll` | Supprimer toutes les entités | `delete` |
| POST | `/syncOnline` | Synchronisation online des données | `update` |
| POST | `/syncOffline` | Synchronisation offline des données | `update` |

## 💡 Exemples d'utilisation

### Créer un produit

```bash
POST /api/products/create
Content-Type: application/json

{
  "name": "Laptop",
  "price": 999.99
}
```

### Créer plusieurs produits

```bash
POST /api/products/createMulti
Content-Type: application/json

{
  "data": [
    { "name": "Laptop", "price": 999.99 },
    { "name": "Mouse", "price": 29.99 },
    { "name": "Keyboard", "price": 79.99 }
  ]
}
```

### Récupérer avec pagination

```bash
POST /api/products/getAllWithPaging
Content-Type: application/json

{
  "pageNumber": 0,
  "pageSize": 20
}
```

### Mettre à jour un produit

```bash
POST /api/products/update
Content-Type: application/json

{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Gaming Laptop",
  "price": 1299.99
}
```

### Synchronisation online

Envoyez les données créées/modifiées localement pour synchronisation :

```bash
POST /api/products/syncOnline
Content-Type: application/json

{
  "data": [
    {
      "id": "temp-id-1",
      "name": "New Product",
      "price": 49.99
    }
  ]
}
```

### Synchronisation offline

Récupérez toutes les modifications depuis une date :

```bash
POST /api/products/syncOffline
Content-Type: application/json

{
  "date": "2026-01-15T00:00:00"
}
```

Retourne toutes les entités créées ou modifiées après la date spécifiée.

### Supprimer un produit

```bash
DELETE /api/products/delete/123e4567-e89b-12d3-a456-426614174000
```

## 🔧 Configuration

Le module ne nécessite pas de configuration Spring Boot spécifique. Les applications consommatrices doivent configurer :

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Documentation API (optionnel)
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
```

## 🏗️ Architecture

Le module suit une architecture en couches :

```
Controller (REST API)
        ↓
Service (Logique métier)
        ↓
Repository (Accès données)
        ↓
Model/Entity (Entités JPA)
```

### Composants principaux

- **BluentGenericController** - Interface générique pour les contrôleurs REST
- **BluentGenericService** - Interface générique pour la logique métier
- **BluentGenericRepository** - Interface générique pour l'accès aux données
- **BluentGenericModel** - Interface pour les entités avec conversion DTO/Response
- **PagingRequest** - Modèle pour les requêtes de pagination
- **MultiCreateResponse** - Modèle pour les réponses de création multiple
- **DataSyncDTO** - Modèle pour la synchronisation de données

## 🔐 Gestion des permissions

Le module utilise un système de permissions basé sur AOP (Aspect-Oriented Programming) pour protéger automatiquement toutes les routes REST. Le système combine deux annotations pour construire dynamiquement les permissions.

### Architecture du système de permissions

Le système de permissions fonctionne en deux étapes :

1. **Activation des permissions sur un controller** : Utilisation de l'annotation `@EnableBluentAutoCheckPermission`
2. **Protection des méthodes** : Utilisation de l'annotation `@BluentCheckPermission` sur chaque méthode

### Annotations disponibles

#### `@EnableBluentAutoCheckPermission`

Annotation à placer **au niveau de la classe** du controller pour activer la gestion automatique des permissions.

```kotlin
@RestController
@RequestMapping("/api/products")
@EnableBluentAutoCheckPermission(name = "product")
class ProductController(
    override var service: ProductServiceImpl
) : BluentGenericController<ProductDTO, ProductResponse, UUID, Product, ProductRepository, ProductServiceImpl>
```

**Paramètres :**
- `name: String` - Le nom de la ressource (ex: "product", "user", "order")

> **Important :** Sans cette annotation sur le controller, les vérifications de permissions ne seront pas effectuées, même si `@BluentCheckPermission` est présent sur les méthodes.

#### `@BluentCheckPermission`

Annotation à placer **au niveau des méthodes** pour spécifier l'action à vérifier.

```kotlin
@BluentCheckPermission("create")
@PostMapping("create")
fun create(@RequestBody dto: ProductDTO): ProductResponse = service.create(dto)
```

**Paramètres :**
- `action: String` - L'action à vérifier (ex: "create", "read", "update", "delete")
- `exhaustive: Boolean = false` - Mode de construction de la permission (voir ci-dessous)

### Construction des permissions

Le système construit la permission finale selon deux modes :

#### Mode non-exhaustif (par défaut, `exhaustive = false`)

La permission est construite en combinant l'action et le nom de la ressource :
- Format : `ACTION_UPPERCASE_RESOURCENAME_UPPERCASE`
- Exemple : `CREATE_PRODUCT`, `READ_PRODUCT`, `UPDATE_PRODUCT`, `DELETE_PRODUCT`

```kotlin
@BluentCheckPermission("create")  // exhaustive = false par défaut
@PostMapping("create")
fun create(@RequestBody dto: ProductDTO): ProductResponse = service.create(dto)
// Permission vérifiée : "CREATE_PRODUCT"
```

#### Mode exhaustif (`exhaustive = true`)

La permission utilisée est exactement l'action fournie, sans combinaison avec le nom de la ressource :

```kotlin
@BluentCheckPermission("create", exhaustive = true)
@PostMapping("create")
fun create(@RequestBody dto: ProductDTO): ProductResponse = service.create(dto)
// Permission vérifiée : "create"
```

### Actions disponibles

Les actions standard utilisées dans `BluentGenericController` sont :

- **`create`** - Permission de création
- **`read`** - Permission de lecture
- **`update`** - Permission de modification
- **`delete`** - Permission de suppression

### Fonctionnement technique

Le système utilise un aspect AOP (`CheckPermissionAspect`) qui :

1. Intercepte toutes les méthodes annotées avec `@BluentCheckPermission`
2. Récupère l'action depuis l'annotation
3. Récupère le nom de la ressource depuis `@EnableBluentAutoCheckPermission` sur le controller
4. Construit la permission finale selon le mode (exhaustif ou non)
5. Appelle `CheckPermissionService.hasPermission(permission)` pour vérifier
6. Lance une `AccessDeniedException` si la permission n'est pas accordée

### Personnalisation de la logique de vérification

Pour modifier la logique de vérification des permissions, vous pouvez créer votre propre service qui implémente l'interface `CheckPermissionService` et surcharger la méthode `hasPermission`.

**Interface à implémenter :**

```kotlin
interface CheckPermissionService {
    fun hasPermission(permission: String): Boolean
}
```

**Exemple d'implémentation personnalisée :**

```kotlin
@Service
@Primary
class CustomCheckPermissionServiceImpl : CheckPermissionService {
    val log = LoggerFactory.getLogger(this.javaClass)

    override fun hasPermission(permission: String): Boolean {
        // Votre logique personnalisée ici
        val auth = SecurityContextHolder.getContext().authentication
        log.info("Vérification permission: $permission pour ${auth.name}")
        log.info("Autorités: ${auth.authorities.map { it.authority }}")
        return auth?.authorities?.any { it.authority == permission } == true
    }
}
```

> **Important :** N'oubliez pas d'ajouter l'annotation `@Primary` à votre service personnalisé pour qu'il remplace l'implémentation par défaut.

**Implémentation par défaut :**

Le module fournit une implémentation par défaut `CheckPermissionServiceImpl` qui vérifie les permissions via Spring Security :

```kotlin
@Service
@Primary
class CheckPermissionServiceImpl : CheckPermissionService {
    val log = LoggerFactory.getLogger(this.javaClass)

    override fun hasPermission(permission: String): Boolean {
        val auth = SecurityContextHolder.getContext().authentication
        log.info("Auth: ${auth.name} ${auth.authorities.map { it.authority }}")
        return auth?.authorities?.any { it.authority == permission } == true
    }
}
```

Cette implémentation par défaut vérifie que l'utilisateur authentifié possède l'autorité correspondant à la permission demandée dans le contexte de sécurité Spring.

### Exemple complet

```kotlin
@RestController
@RequestMapping("/api/products")
@EnableBluentAutoCheckPermission(name = "product")
class ProductController(
    override var service: ProductServiceImpl
) : BluentGenericController<ProductDTO, ProductResponse, UUID, Product, ProductRepository, ProductServiceImpl> {
    
    // Les méthodes héritées de BluentGenericController sont déjà protégées
    // avec @BluentCheckPermission("create"), @BluentCheckPermission("read"), etc.
    // Les permissions vérifiées seront : CREATE_PRODUCT, READ_PRODUCT, UPDATE_PRODUCT, DELETE_PRODUCT
}
```

Dans cet exemple, l'utilisateur doit posséder les autorités suivantes dans Spring Security :
- `CREATE_PRODUCT` pour créer un produit
- `READ_PRODUCT` pour lire les produits
- `UPDATE_PRODUCT` pour modifier un produit
- `DELETE_PRODUCT` pour supprimer un produit

## 📊 Modèle de données

### Champs obligatoires

Toute entité utilisant ce module doit implémenter :

| Champ | Type | Description |
|-------|------|-------------|
| `id` | Generic (UUID, Long, etc.) | Identifiant unique |
| `createdAt` | LocalDateTime | Date de création |
| `updatedAt` | LocalDateTime? | Date de dernière modification |
| `deletedAt` | LocalDateTime? | Date de suppression (soft delete) |
| `isDeleted` | Boolean | Flag de suppression logique |

### Méthodes obligatoires

- `toDTO()` - Conversion vers DTO
- `toResponse()` - Conversion vers Response

## 📦 Dépendances principales

- Spring Boot Starter Data JPA 3.4.5
- Spring Boot Starter Web 3.4.5
- Kotlin Reflection
- MySQL Connector 8.2.0
- SpringDoc OpenAPI 2.8.5
- Jackson Module Kotlin 2.18.3

## 📄 Licence

Projet interne Nebryon/Bluent

## 👥 Auteurs

Équipe Bluent/Nebryon

## 📞 Support

Pour toute question ou problème, veuillez contacter l'équipe de développement.

---

**Développé avec ❤️ par l'équipe Bluent/Nebryon**