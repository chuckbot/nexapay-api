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