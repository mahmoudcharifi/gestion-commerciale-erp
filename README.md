# GestionCommerciale ERP

A desktop Enterprise Resource Planning (ERP) application designed to support day-to-day commercial operations, including product management, customer records and inventory tracking.

The project is being developed with **JavaFX** and **Java**, following a modular structure that can later be extended with a **Spring Boot REST API** and **PostgreSQL**.

## Overview

GestionCommerciale aims to bring essential business operations together in one application. The current development focus is the desktop interface and its core management modules.

The application is intended to support both retail businesses working with individual customers and businesses dealing with professional customers.

## Main Features

### Implemented

- **Dashboard** — main application workspace.
- **Product management** — product catalogue and product information.
- **Category management** — organize products into categories.
- **Customer management** — manage individual customers and companies in one centralized module.
- **Inventory management** — track stock quantities, stock entries and exits, and movement history.
- **Stock status** — identify available, low-stock and out-of-stock products.
- **Customer search and filtering** — find customers and filter by customer type.
- **Modular navigation** — navigate between application modules.

### Planned

- Supplier management.
- Sales and purchase management.
- Quotations and invoicing.
- Payment tracking.
- User authentication, roles and permissions.
- Financial reports and advanced dashboard indicators.
- Backend integration through a REST API.
- Persistent data storage with PostgreSQL.

> Features listed as planned are not yet completed. The project is actively under development.

## Technologies

### Current Desktop Application

- **Java 25**
- **JavaFX 27.0.1**
- **FXML**
- **JavaFX CSS**
- **Maven**

### Planned Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- REST API

### Planned Database

- PostgreSQL

## Project Architecture

The desktop application follows a layered and modular package structure:

```text
GestionCommerciale/
└── frontend/
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            │   └── com/gestion/
            │       ├── App.java
            │       ├── controller/
            │       ├── model/
            │       ├── navigation/
            │       └── service/
            └── resources/
                └── com/gestion/
                    ├── views/
                    └── css/
```

### Package Responsibilities

| Package | Responsibility |
|---|---|
| `controller` | Handles JavaFX events and coordinates UI interactions. |
| `model` | Contains domain models and types. |
| `service` | Encapsulates business operations and application logic. |
| `navigation` | Manages navigation between application views. |
| `views` | Contains FXML layouts. |
| `css` | Contains shared styles and themes. |

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- JDK 25 or a compatible JDK supporting the configured Java release.
- Apache Maven.
- Git.

JavaFX dependencies are declared in the Maven configuration and are resolved during the build.

### Clone the Repository

```bash
git clone https://github.com/mahmoudcharifi/gestion-commerciale-erp.git
cd gestion-commerciale-erp
```

### Build the Application

The Maven project is located in the `frontend` directory:

```bash
cd frontend
mvn clean compile
```

### Run the Application

```bash
mvn javafx:run
```

## Roadmap

- [x] Main application layout and navigation
- [x] Dashboard
- [x] Product management
- [x] Category management
- [x] Customer management (individuals and companies)
- [x] Stock management and movement history
- [ ] Supplier management
- [ ] Sales management
- [ ] Purchase management
- [ ] Quotations and invoices
- [ ] Payment management
- [ ] Spring Boot backend
- [ ] PostgreSQL database integration
- [ ] Authentication and role-based access control
- [ ] Reporting and analytics

