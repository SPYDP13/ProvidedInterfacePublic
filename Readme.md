# Bluent Interfaces Module

[![Version](https://img.shields.io/badge/version-1.0.5-blue.svg)](http://148.230.116.99:8081/repository/NebryonModule/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)

Module Spring Boot/Kotlin fournissant des interfaces génériques pour le provisionnement automatique de routes REST standardisées avec support de pagination, synchronisation de données et gestion de permissions.

## 📋 Table des matières

- [Description](#-description)
- [Fonctionnalités](#-fonctionnalités)
- [Installation](#-installation)
- [Guide de démarrage rapide](#-guide-de-démarrage-rapide)
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
        url = uri("http://148.230.116.99:8081/repository/NebryonModule/")
        isAllowInsecureProtocol = true
        credentials {
            username = "admin"
            password = System.getenv("NEXUS_PASSWORD")
        }
    }
}

dependencies {
    implementation("com.nebryon.modules:interfaces-module:1.0.5")
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

Chaque route est protégée par l'annotation `@BluentCheckPermission` qui vérifie les droits d'accès :

- **create** - Permission de création
- **read** - Permission de lecture
- **update** - Permission de modification
- **delete** - Permission de suppression

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