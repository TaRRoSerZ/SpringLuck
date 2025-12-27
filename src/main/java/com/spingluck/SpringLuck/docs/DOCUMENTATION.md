# Documentation SpringLuck - Casino en Ligne

## 📋 Vue d'ensemble

**SpringLuck** est une application backend de casino en ligne développée avec Spring Boot 3.5.6 et Java 21. Le projet suit une **architecture hexagonale** (ports & adapters) pour assurer une séparation claire entre la logique métier et les détails techniques.

### Technologies principales

- **Spring Boot** : Framework principal
- **PostgreSQL** : Base de données
- **Flyway** : Gestion des migrations de base de données
- **Keycloak** : Authentification et autorisation OAuth2/JWT
- **Stripe** : Gestion des paiements
- **JdbcTemplate** : Accès aux données sans ORM
- **Lombok** : Réduction du code boilerplate

---

## 🏗️ Architecture Hexagonale

Le projet est organisé selon l'architecture hexagonale qui divise le code en 3 couches:

### 1. **Domain (Cœur métier)** - `application/domain/`

Le cœur de l'application, indépendant de toute technologie externe.

- **Models** : Entités métier (User, Bet, Transaction)
- **Services** : Logique métier pure

### 2. **Ports (Interfaces)** - `application/port/`

Contrats définissant comment interagir avec le domaine.

- **Ports IN** : UseCase interfaces (ce que l'application offre)
- **Ports OUT** : Interfaces pour accéder aux données externes

### 3. **Adapters (Implémentations)** - `adapter/`

Connexions concrètes au monde extérieur.

- **Adapters IN** : Controllers REST (web)
- **Adapters OUT** : Repositories SQL (persistence)

---

## 📁 Structure Détaillée

### 🔹 Racine du projet

#### `pom.xml`

Fichier de configuration Maven définissant:

- Les dépendances (Spring Boot, PostgreSQL, Stripe, Keycloak, etc.)
- Version Java (21)
- Configuration de build

#### `mvnw` / `mvnw.cmd`

Scripts Maven Wrapper pour exécuter Maven sans installation préalable.

---

### 🔹 `src/main/java/com/spingluck/SpringLuck/`

#### **SpringLuckApplication.java**

Point d'entrée de l'application Spring Boot.

```java
@SpringBootApplication
public class SpringLuckApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringLuckApplication.class, args);
    }
}
```

#### **SpringLuckApplicationConfiguration.java**

Configuration Spring qui instancie manuellement les services (beans).

- Crée les services (BetService, UserService, TransactionService)
- Injecte les dépendances entre services
- Configure CORS pour autoriser les requêtes front-end

**Rôle**: Wire les ports IN et OUT avec les services du domaine.

#### **StripeConfig.java**

Configure l'API Stripe au démarrage de l'application.

- Charge la clé secrète depuis `application.properties`
- Initialise `Stripe.apiKey` avec `@PostConstruct`

---

### 🔹 `adapter/in/web/` - Contrôleurs REST

Ces classes gèrent les requêtes HTTP entrantes et délèguent au domaine.

#### **BetController.java**

Endpoints pour la gestion des paris.

| Méthode | Endpoint      | Action                  |
| ------- | ------------- | ----------------------- |
| GET     | `/bets`       | Récupère tous les paris |
| GET     | `/bets/{id}`  | Récupère un pari par ID |
| POST    | `/bets/place` | Place un nouveau pari   |

**Dépendance**: Utilise `BetUseCase` (port IN).

#### **UserController.java**

Endpoints pour la gestion des utilisateurs.

| Méthode | Endpoint             | Action                                |
| ------- | -------------------- | ------------------------------------- |
| GET     | `/users`             | Liste tous les utilisateurs (ADMIN)   |
| GET     | `/users/{email}`     | Récupère un utilisateur par email     |
| POST    | `/users/sync`        | Synchronise/crée un utilisateur       |
| POST    | `/users/transaction` | Applique une transaction à la balance |

**Dépendance**: Utilise `UserUseCase`.

#### **TransactionController.java**

Endpoints pour gérer les transactions.

| Méthode | Endpoint                      | Action                                |
| ------- | ----------------------------- | ------------------------------------- |
| GET     | `/transactions`               | Liste toutes les transactions (ADMIN) |
| GET     | `/transactions/{id}`          | Récupère une transaction              |
| GET     | `/transactions/user/{userId}` | Transactions d'un utilisateur         |
| POST    | `/transactions/create`        | Crée une transaction                  |

**Dépendance**: Utilise `TransactionUseCase`.

#### **StripeController.java**

Gestion des paiements Stripe.

**POST `/stripe/create-payment-intent`**

- Crée un PaymentIntent Stripe
- Enregistre une transaction PENDING
- Retourne le clientSecret pour le front-end

**POST `/stripe/webhook`** (public)

- Reçoit les événements de Stripe
- Confirme le paiement quand `payment_intent.succeeded`
- Met à jour la balance utilisateur et le statut de transaction

---

### 🔹 `application/domain/model/` - Modèles métier

#### **User.java**

Représente un utilisateur du casino.

```java
- UUID id
- String email
- Double balance (solde actuel)
- boolean active
- Instant createdAt, updatedAt
```

#### **Bet.java**

Représente un pari placé par un utilisateur.

```java
- UUID id
- UUID userId (référence au joueur)
- Double amount (montant parié)
- Date date
- Boolean isWinningBet (pari gagnant ou perdant)
```

#### **Transaction.java**

Enregistre tous les mouvements d'argent.

```java
- UUID id
- Double amount
- UUID betId (lié à un pari, optionnel)
- UUID userId
- String stripeIntentId (ID Stripe, optionnel)
- TransactionType type (DEPOSIT, WITHDRAWAL, etc.)
- TransactionStatus status (PENDING, CONFIRMED, FAILED)
- Date date
```

#### **TransactionType.java** (Enum)

- `DEPOSIT` : Dépôt d'argent
- `WITHDRAWAL` : Retrait
- `BET_PLACED` : Pari placé (débit)
- `BET_WIN` : Gain de pari (crédit)
- `BET_LOSS` : Perte de pari

#### **TransactionStatus.java** (Enum)

- `PENDING` : En attente de confirmation
- `CONFIRMED` : Validée
- `FAILED` : Échec

---

### 🔹 `application/domain/service/` - Logique métier

#### **UserService.java**

Implémente `UserUseCase`.

**Méthodes principales**:

- `syncUser(User)` : Crée un utilisateur s'il n'existe pas, sinon retourne l'existant
- `getAllUsers()` : Liste tous les utilisateurs
- `getUserByEmail(email)` : Recherche par email
- `applyTransaction(user, type, amount)` : Applique une transaction et met à jour la balance

**Logique**: Calcule le delta selon le type de transaction (+/- amount), met à jour la balance et crée une transaction.

**Dépendances**: `UserPort`, `TransactionUseCase`.

#### **TransactionService.java**

Implémente `TransactionUseCase`.

**Méthodes**:

- `createTransaction(Transaction)` : Enregistre une nouvelle transaction
- `getAllTransactions()` : Liste toutes les transactions
- `getAllUserTransaction(userId)` : Transactions d'un utilisateur
- `getTransactionById(id)` : Recherche par ID
- `confirmPayment(intentId, userEmail)` : Confirme un paiement Stripe
  - Trouve la transaction par `stripeIntentId`
  - Met à jour la balance utilisateur
  - Change le statut à CONFIRMED

**Dépendances**: `TransactionPort`, `UserPort`.

#### **BetService.java**

Implémente `BetUseCase`.

**Méthodes**:

- `placeBet(Bet)` : Enregistre un pari
- `getAllBets()` : Liste tous les paris
- `getBetById(id)` : Recherche par ID

**Dépendance**: `BetPort`.

---

### 🔹 `application/port/in/` - Ports d'entrée (Use Cases)

Interfaces définissant les actions métier disponibles.

#### **UserUseCase.java**

```java
- syncUser(User)
- getAllUsers()
- getUserByEmail(String)
- applyTransaction(User, TransactionType, Double)
```

#### **TransactionUseCase.java**

```java
- createTransaction(Transaction)
- getAllTransactions()
- getAllUserTransaction(UUID)
- getTransactionById(UUID)
- confirmPayment(String, String)
```

#### **BetUseCase.java**

```java
- placeBet(Bet)
- getAllBets()
- getBetById(UUID)
```

**Rôle**: Contrat que le domaine expose au monde extérieur (controllers).

---

### 🔹 `application/port/out/` - Ports de sortie

Interfaces pour accéder aux données externes.

#### **UserPort.java**

```java
- saveUser(User)
- findAllUsers()
- findUserByEmail(String)
- updateBalance(String email, Double amount)
```

#### **TransactionPort.java**

```java
- save(Transaction)
- findAll()
- findTransactionsByUserId(UUID)
- findById(UUID)
- findByStripeIntentId(String)
- updateStatusByStripeId(String, TransactionStatus)
```

#### **BetPort.java**

```java
- save(Bet)
- findAll()
- findById(UUID)
```

**Rôle**: Contrat que les adapters SQL doivent implémenter.

---

### 🔹 `adapter/out/persistence/` - Adapters SQL

Implémentent les ports OUT avec `JdbcTemplate` (SQL pur).

#### **UserSQLAdapter.java**

Implémente `UserPort`.

- Exécute des requêtes SQL INSERT, SELECT, UPDATE
- Utilise `UserRowMapper` pour convertir ResultSet en objets User

#### **TransactionSQLAdapter.java**

Implémente `TransactionPort`.

- Gère les CRUD pour transactions
- Recherche par `stripeIntentId`
- Met à jour les statuts

#### **BetSQLAdapter.java**

Implémente `BetPort`.

- Gère les CRUD pour les paris

#### **UserRowMapper.java**, **TransactionRowMapper.java**, **BetRowMapper.java**

Classes utilitaires pour mapper les lignes SQL vers les objets Java.

```java
implements RowMapper<User> {
    User mapRow(ResultSet rs, int rowNum) {
        // Extrait les colonnes et crée l'objet
    }
}
```

---

### 🔹 `config/` - Configuration

#### **SecurityConfig.java**

Configure la sécurité Spring Security avec OAuth2.

**Fonctionnalités**:

- Désactive CSRF (API REST)
- Active CORS
- Configure les autorisations:
  - `/stripe/webhook` : Public (pas d'authentification)
  - `/users`, `/transactions`, `/bets` : Réservé aux ADMIN
  - Autres routes : Authentification requise
- Configure JWT avec Keycloak :
  - Vérifie les tokens JWT
  - Extrait les rôles avec `KeycloakRoleConverter`

#### **KeycloakRoleConverter.java**

Convertisseur personnalisé pour extraire les rôles Keycloak du JWT.

**Processus**:

1. Lit `realm_access.roles` dans le token JWT
2. Convertit chaque rôle en `ROLE_<nom>` (format Spring Security)
3. Retourne la liste des autorités

**Exemple JWT**:

```json
{
	"realm_access": {
		"roles": ["ADMIN", "USER"]
	}
}
```

Devient: `ROLE_ADMIN`, `ROLE_USER`.

---

### 🔹 `src/main/resources/`

#### **application.properties**

Configuration de l'application:

```properties
# Serveur
server.port=8083

# Base de données PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/springluckDB
spring.datasource.username=springluck_user
spring.datasource.password=springluck

# Flyway (migrations)
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migrations

# OAuth2 / Keycloak
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/springluck

# Stripe
stripe.secret.key=${STRIPE_SECRET_KEY}
stripe.public.key=${STRIPE_PUBLIC_KEY}
```

#### **db/migrations/**

Scripts SQL Flyway exécutés dans l'ordre (V1, V2, V3...).

**V1\_\_createBetTable.sql** : Crée la table `bets`
**V2\_\_insertBets.sql** : Insère des données de test
**V3\_\_createTransactionTable.sql** : Crée la table `transactions`
**V4\_\_insertTransactions.sql** : Données de test
**V5\_\_createUserTable.sql** : Crée la table `users`
**V6\_\_insertUsers.sql** : Utilisateurs de test
**V7\_\_alterTableTransactions.sql** : Modifications de schéma

**Flyway** : Au démarrage, exécute automatiquement les migrations non appliquées.

---

### 🔹 `src/test/java/` - Tests

Tests unitaires et d'intégration avec JUnit 5 et Testcontainers.

#### **BetControllerTest.java** / **UserControllerTest.java** / **TransactionControllerTest.java** / **StripeControllerTest.java**

Tests des endpoints REST (contrôleurs).

#### **BetServiceTest.java** / **UserServiceTest.java** / **TransactionServiceTest.java**

Tests de la logique métier (services).

#### **BetSQLAdapterTest.java** / **UserSQLAdapterTest.java** / **TransactionSQLAdapterTest.java**

Tests des adapters SQL avec Testcontainers (base PostgreSQL en Docker).

---

## 🔗 Flux de données

### Exemple : Créer un paiement

1. **Front-end** → POST `/stripe/create-payment-intent` avec montant et userId
2. **StripeController** :
   - Appelle l'API Stripe pour créer un PaymentIntent
   - Crée une Transaction (status=PENDING) via `TransactionUseCase`
   - Retourne clientSecret au front
3. **Front-end** : Affiche le formulaire de paiement Stripe
4. **Utilisateur** : Entre ses coordonnées bancaires
5. **Stripe** : Traite le paiement et envoie webhook à `/stripe/webhook`
6. **StripeController.handleStripeWebhook()** :
   - Vérifie l'événement `payment_intent.succeeded`
   - Appelle `transactionService.confirmPayment()`
7. **TransactionService** :
   - Trouve la transaction par `stripeIntentId`
   - Met à jour la balance utilisateur (+montant)
   - Change le statut à CONFIRMED

### Exemple : Placer un pari

1. **Front-end** → POST `/bets/place` avec Bet (userId, amount, etc.)
2. **BetController** → `betUseCase.placeBet(bet)`
3. **BetService** → `betPort.save(bet)`
4. **BetSQLAdapter** : INSERT dans la table `bets`
5. **(Parallèlement)** Le front-end devrait appeler `/users/transaction` pour débiter le montant

---

## 🔐 Sécurité

### Authentification OAuth2/JWT avec Keycloak

1. **Keycloak** : Serveur d'authentification externe (port 9090)
2. L'utilisateur se connecte via Keycloak et obtient un JWT
3. Le front-end envoie ce JWT dans l'en-tête `Authorization: Bearer <token>`
4. **Spring Security** :
   - Valide le token avec la clé publique de Keycloak
   - Extrait les rôles avec `KeycloakRoleConverter`
   - Autorise/refuse l'accès selon les règles (ADMIN pour `/users`, etc.)

### Autorisations

- **Public** : `/stripe/webhook`
- **Authentifié** : Toutes les routes par défaut
- **ADMIN uniquement** : `/users`, `/transactions`, `/bets`

---

## 📊 Schéma de base de données

### Table `users`

```sql
id UUID PRIMARY KEY
email VARCHAR(255) UNIQUE
balance NUMERIC(12,2)
is_active BOOLEAN
created_at TIMESTAMP
updated_at TIMESTAMP
```

### Table `transactions`

```sql
id UUID PRIMARY KEY
amount NUMERIC(10,2)
bet_id UUID (nullable)
user_id UUID
stripe_intent_id VARCHAR(255) (nullable)
type VARCHAR(50) (enum)
status VARCHAR(50) (enum)
date TIMESTAMP
```

### Table `bets`

```sql
id UUID PRIMARY KEY
user_id UUID
amount NUMERIC(10,2)
date TIMESTAMP
isWinningBet BOOLEAN
```

---

## 🔄 Relations entre composants

```
┌─────────────────┐
│  Controllers    │ ← Adapters IN (Web)
│  (REST API)     │
└────────┬────────┘
         │ utilise
         ▼
┌─────────────────┐
│   Use Cases     │ ← Ports IN (Interfaces)
│  (Interfaces)   │
└────────┬────────┘
         │ implémenté par
         ▼
┌─────────────────┐
│    Services     │ ← Domain (Logique métier)
│  (BetService,   │
│   UserService)  │
└────────┬────────┘
         │ utilise
         ▼
┌─────────────────┐
│  Data Ports     │ ← Ports OUT (Interfaces)
│  (Interfaces)   │
└────────┬────────┘
         │ implémenté par
         ▼
┌─────────────────┐
│  SQL Adapters   │ ← Adapters OUT (Persistence)
│  (JDBC)         │
└────────┬────────┘
         │
         ▼
    PostgreSQL
```

**Principe**: Le domaine ne dépend de rien. Tout le monde dépend du domaine.

---

## 🚀 Démarrage du projet

### Prérequis

- Java 21
- Maven
- PostgreSQL (localhost:5432)
- Keycloak (localhost:9090)
- Variables d'environnement : `STRIPE_SECRET_KEY`, `STRIPE_PUBLIC_KEY`

### Lancer l'application

```powershell
# Compiler et démarrer
./mvnw spring-boot:run

# Ou avec Maven installé
mvn spring-boot:run
```

L'application démarre sur **http://localhost:8083**

### Arrêter l'application

- `Ctrl+C` dans le terminal

### Exécuter les tests

```powershell
./mvnw test
```

---

## 📝 Points clés

1. **Architecture hexagonale** : Domaine isolé, facilite les tests et la maintenabilité
2. **Pas d'ORM** : Utilisation de JdbcTemplate pour un contrôle SQL direct
3. **Flyway** : Gestion versionnée des migrations de base de données
4. **Sécurité OAuth2** : Délégation de l'authentification à Keycloak
5. **Stripe** : Paiements asynchrones avec webhooks
6. **Testcontainers** : Tests d'intégration avec PostgreSQL en Docker
7. **Lombok** : Réduction du code boilerplate (@Getter, @Setter, @AllArgsConstructor)

---

**Fin de la documentation**
