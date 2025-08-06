# NEXAPAY API: Fintech MVP with Clojure

Welcome to the NEXAPAY API MVP! This project is a basic fintech application prototype, built with **Clojure** and adhering to **Hexagonal Architecture (Ports and Adapters)** and **Test-Driven Development (TDD)** principles.

---

## 🚀 Implemented Features

Currently, this MVP includes the following functionalities:

* **User Registration:** Allows new users to register securely, including password hashing and automatic creation of an associated account with a zero initial balance.

* **User Login:** Enables users to authenticate with their credentials.

* **Fund Transfer:** Implements the business logic for transferring money between two user accounts. This operation is **atomic** (ensuring all operations complete or none do) and is protected against insufficient funds and non-existent accounts.

* **H2 Database:** Uses an in-memory database (H2) for data persistence, ideal for development and testing.

* **Unit and Integration Tests:** All key functionalities are covered by robust tests, following the TDD methodology, to ensure system correctness and reliability.

---

## 🛠️ Technologies Used

* **Clojure:** The functional programming language on the JVM.

* **Leiningen:** Project automation tool for Clojure.

* **Pedestal:** Web framework for building RESTful APIs.

* **H2 Database:** Lightweight in-memory relational database.

* **clojure.java.jdbc:** Clojure interface for JDBC (Java Database Connectivity).

* **Honeysql:** Library for programmatically building SQL queries.

* **Buddy Auth & Hashers:** Libraries for authentication, authorization, and password hashing.

---

## ⚙️ Project Setup

Follow these steps to get the project up and running on your local machine.

### Prerequisites

Make sure you have the following installed:

* **Java Development Kit (JDK) 11 or higher:** Clojure runs on the JVM.

* **Leiningen:** You can find installation instructions at [leiningen.org](https://leiningen.org/).

### Clone the Repository

```bash
git clone <YOUR_REPO_URL> # Replace with your GitHub repo URL
cd nexapay-api
```

### Install Dependencies

Once in the project's root directory, install the dependencies:

```bash
lein deps
```

## ▶️ Run the Application

To start the API server:

```bash
lein run
```

You will see a series of Jetty and Pedestal log messages. Once the server is ready, you will see messages like `Creating database tables...` and `Starting server...`. The server will be listening on `http://localhost:8080`.

## ✅ Run Tests

To run all unit and integration tests for the project:

```bash
lein test
```

All tests should pass, confirming the correctness of the business logic.

## 🧪 Test the API with Postman (or cURL)

Once the server is running, you can use Postman, Insomnia, or `curl` to interact with the endpoints.

### 1. Register a User (`POST /api/v1/register`)

* **URL:** `http://localhost:8080/api/v1/register`

* **Method:** `POST`

* **Headers:** `Content-Type: application/json`

* **Body (raw JSON):**

    ```bash
    {
        "username": "your_username",
        "password": "your_secure_password"
    }

    ```

* **Expected Response (Success):** `201 Created`, `"Usuario registrado exitosamente."`

* **Expected Response (Conflict):** `409 Conflict`, `"El nombre de usuario ya existe."`

### 2. Log In (`POST /api/v1/login`)

* **URL:** `http://localhost:8080/api/v1/login`

* **Method:** `POST`

* **Headers:** `Content-Type: application/json`

* **Body (raw JSON):**

    ```bash
    {
        "username": "your_username",
        "password": "your_secure_password"
    }

    ```

* **Expected Response (Success):** `200 OK`, `{"token": "your_username"}` (in a real system, this would be a JWT).

* **Expected Response (Invalid Credentials):** `401 Unauthorized`, `"Credenciales inválidas."`

### 3. Transfer Funds (`POST /api/v1/transfer`)

**Before testing:** You will need account IDs. Access the H2 console (see next section) and run `SELECT ID, USER_ID, BALANCE FROM ACCOUNTS;` to get the account IDs of the users you have registered.

* **URL:** `http://localhost:8080/api/v1/transfer`

* **Method:** `POST`

* **Headers:** `Content-Type: application/json`

* **Body (raw JSON):**

    ```bash
    {
        "from-account": 1,  // Replace with the source account ID
        "to-account": 2,    // Replace with the destination account ID
        "amount": 50.00     // Amount to transfer (ensure source account has sufficient balance)
    }

    ```

* **Expected Response (Success):** `200 OK`, `"Transferencia exitosa."`

* **Expected Response (Insufficient Funds):** `400 Bad Request`, `"Error: Insufficient funds."`

* **Expected Response (Non-existent Accounts):** `400 Bad Request`, `"Error: One or both accounts do not exist."`

