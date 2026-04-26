# Appointment Management System - API & Functionality Documentation

This document outlines the core responsibilities, functionalities, and API endpoints for every microservice within the Appointment Management System architecture.

---

## 1. API Gateway (`api-gateway`)
**Port:** `8080` (Default edge server entry point for all frontend apps/clients)
**Functionality:** 
Acts as the single public-facing entry point to the entire ecosystem. It intercepts all incoming HTTP client requests, queries the Eureka Server registry to locate the dynamic IP and Port of the target backend microservice, and routes the traffic seamlessly.
* **Routing Strategy**: Requests matching `/<service-name>/**` are automatically forwarded. 
* *Example:* `POST http://localhost:8080/patient-service/api/users/login` routes securely to the hidden `patient-service` without exposing its internal port.

---

## 2. Patient Service (`patient-service`)
**Port:** `8081` (Internal)
**Functionality:**  
Responsible for identity and access management. Handles patient and admin registrations, secure login, password hashing, and JWT token issuing. It stores user profiles securely in MongoDB.

### API Endpoints
| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/users/register` | Registers a new user (Patient/Doctor/Admin) | No |
| `POST` | `/api/users/login` | Authenticates user and returns JWT Token | No |
| `GET` | `/api/users/{id}` | Fetches a user's complete profile | Yes |

---

## 3. Appointment Service (`appointment-service`)
**Port:** `8082` (Internal)
**Functionality:** 
The core engine of the system. It manages the lifecycle of appointments and available time slots. When an appointment is booked, it communicates synchronously with the Patient/Doctor services (via OpenFeign) to validate entities, and publishes asynchronous events to RabbitMQ for notification processing.

### API Endpoints
**Appointments:**
| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/appointments` | Books a new appointment for a user with a doctor | Yes |
| `GET` | `/appointments/{id}` | Retrieves details of a specific appointment | Yes |
| `GET` | `/appointments/user/{userId}` | Retrieves history of a patient's appointments | Yes |
| `PUT` | `/appointments/{id}/status` | Updates status (e.g., Cancel, Complete) | Yes |

**Slots (Available Time Management):**
| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/slots` | Creates a new appointment slot | Yes (Admin) |
| `GET` | `/slots/available` | Lists all open slots available for booking | Yes |
| `PUT` | `/slots/{slotId}` | Modifies a slot's time or assigned doctor | Yes (Admin) |
| `DELETE` | `/slots/{slotId}` | Removes a slot entirely | Yes (Admin) |

---

## 4. Doctor Service (`doctor-service`)
**Port:** `8085` (Internal)
**Functionality:** 
Manages the medical staff records and doctor-specific operational schedules. Stores doctor specializations, bios, and specific daily shifts.

### API Endpoints
**Doctors:**
| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/doctors` | Registers a new doctor into the system | Yes (Admin) |
| `GET` | `/doctors` | Retrieves the public directory of all doctors | Yes |
| `GET` | `/doctors/{doctorId}` | Retrieves a specific doctor's profile | Yes |

**Schedules:**
| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/schedules` | Creates a working schedule for a doctor | Yes (Admin) |
| `GET` | `/schedules` | Lists all master schedules | Yes |
| `GET` | `/schedules/available` | Lists all unbooked schedule chunks | Yes |

---

## 5. Notification Service (`notification-service`)
**Port:** `8083` (Internal)
**Functionality:** 
A strictly event-driven microservice. It does not accept direct creation requests via HTTP. Instead, it continuously listens to the `appointment-notification-queue` on RabbitMQ. When a slot is booked or canceled, this service catches the event and writes the notification to its independent MongoDB instance for user retrieval.

### API Endpoints
| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/notifications/user/{recipientId}` | Retrieves a chronological list of all notifications (alerts/confirmations) for a specific user | Yes |

---

## 6. Interservice Communication Architecture

The microservices operate on an Event-Driven and REST-based architecture, maintaining strict boundaries while coordinating complex workflows.

### Synchronous Communication (OpenFeign)
Certain operations require immediate validation before proceeding. We use **Spring Cloud OpenFeign** for synchronous HTTP calls between internal services:
- **Appointment $\rightarrow$ Patient Service:** When a patient creates an appointment, the Appointment Service synchronously calls the Patient Service (`patient-service/api/users/{id}`) to verify the patient's identity exists before locking the slot.
- **Appointment $\rightarrow$ Doctor Service:** Similarly, the Appointment Service synchronously calls the Doctor Service to verify the requested medical schedule actually exists and belongs to the specified doctor.

### Asynchronous Communication (RabbitMQ Event Broker)
To prevent blocking threads and maintain high availability, non-critical downstream actions are handled asynchronously via RabbitMQ.
- **Event Publisher (Appointment Service):** Once an appointment is confirmed and saved to MongoDB, the Appointment Service publishes an event payload to the `appointment-exchange` with the routing key `appointment.booked`.
- **Event Subscriber (Notification Service):** The Notification Service continuously listens to the `appointment-notification-queue`. It instantly consumes the event, formats a user-friendly alert, and saves it to its own MongoDB database, terminating the flow without ever slowing down the user's original HTTP request.

---

## Infrastructure Services

### Service Registry (`eureka-server`)
**Port:** `8761`
Acts as the dynamic DNS and Address Book for the microservices. Every service securely registers its dynamic container IP and Port here on startup. This allows them to find each other by logical application names (e.g., `patient-service`) rather than hardcoded static IPs, enabling API Gateway routing and OpenFeign client-side load balancing.

### Distributed Configuration (`config-server`)
**Port:** `8888`
Centralizes deployment configurations securely. It leverages the `spring.config.import=optional:configserver:` directive woven into the Docker environment. Instead of maintaining 4 separate `application.properties` files with duplicate database strings, it cleanly feeds the MongoDB Atlas credentials, CloudAMQP URIs, JWT signing keys, and Eureka URLs to all 4 microservices the exact moment they launch.

---

## 7. Monitoring & Documentation Endpoints

To ensure the robust observability required for cloud deployments, the microservices are instrumented with automated monitoring and API documentation.

### Health Checks (Spring Boot Actuator)
**Endpoint:** `GET /actuator/health`
**Availability:** All Services
**Purpose:** Exposes a standard HTTP ping for cloud orchestrators (like Docker Compose or Render) to continually verify that the container's Java process is alive and healthy. 

### API Documentation (Swagger OpenAPI)
**Endpoints:** `GET /swagger-ui.html` (UI) and `GET /v3/api-docs` (Raw JSON)
**Availability:** Core Services & API Gateway
**Purpose:** Provides a fully interactive, autogenerated graphical interface hosted directly by each microservice. It allows developers to test HTTP operations, view required JSON payloads, and explore database schemas directly from the browser without requiring external tools like Postman.
