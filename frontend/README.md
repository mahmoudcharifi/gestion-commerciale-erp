# GestionCommerciale ERP – Frontend

A desktop application for commercial management, developed with **Java and JavaFX**. The project aims to provide a centralized interface for managing products, customers, suppliers, sales, purchases, inventory, and invoices.

## Overview

GestionCommerciale ERP is a desktop ERP application designed to simplify daily commercial operations through a structured and user-friendly interface.

This repository contains the frontend application. The backend and database integration are planned for future development.

## Technologies

| Technology | Purpose |
|---|---|
| Java 27 | Programming language |
| JavaFX 27 | Desktop user interface |
| FXML | Interface structure |
| CSS | Styling and UI design |
| Maven | Dependency and build management |
| Scene Builder | Visual interface design |
| OpenPDF | PDF document generation |

## Features

The application currently includes the following modules:

- **Dashboard** – Overview of key business indicators and recent sales activity.
- **Products Management** – Product information and inventory quantities.
- **Categories Management** – Product category organization.
- **Clients Management** – Customer information management.
- **Suppliers Management** – Supplier information management.
- **Sales Management** – Sales operations.
- **Purchases Management** – Purchase operations.
- **Stock Management** – Inventory monitoring and stock movements.
- **Invoices Management** – Invoice management and PDF generation.

*Note: Features and data persistence are still being developed and tested.*

## Project Structure

```
frontend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/gestion/
│       │       ├── controller/
│       │       ├── model/
│       │       ├── navigation/
│       │       └── service/
│       └── resources/
│           └── com/gestion/
│               ├── views/
│               └── css/
├── pom.xml
└── README.md
```

## Prerequisites

Before running the application, make sure you have installed:

- JDK 27
- Apache Maven
- JavaFX dependencies configured through Maven

Verify your installations:

```bash
java -version
mvn -version
```

## Installation and Execution

**1. Clone the repository**

```bash
git clone https://github.com/mahmoudcharifi/gestion-commerciale-erp.git
```

**2. Navigate to the frontend directory**

```bash
cd gestion-commerciale-erp/frontend
```

**3. Run the application**

```bash
mvn clean javafx:run
```

## Current Development Status

The JavaFX frontend is under active development.

| Component | Status |
|---|---|
| JavaFX application structure | Implemented |
| Dashboard | Implemented |
| Main navigation | Implemented |
| Management modules | Under development and testing |
| PDF invoice generation | Implemented |
| Responsive UI improvements | Planned |
| Spring Boot backend | Planned |
| PostgreSQL integration | Planned |
| JPA / Hibernate persistence | Planned |

## Future Improvements

- Improve responsive layouts and user experience.
- Develop a REST API using Spring Boot.
- Integrate PostgreSQL for persistent data storage.
- Implement JPA / Hibernate.
- Connect the JavaFX frontend to the backend.
- Add authentication and role-based access control.
- Improve automated testing and application security.

## Author

**Mahmoud Charifi**

GitHub: [@mahmoudcharifi](https://github.com/mahmoudcharifi)

---

*GestionCommerciale ERP – Desktop Commercial Management Application.*