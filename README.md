
# 📇 Smart Contact Manager (Cloud-Native)

A full-stack, cloud-deployed Spring Boot web application designed to help users securely manage, store, and organize their personal contacts in the cloud. 

This project demonstrates a production-ready **Single Node Deployment Architecture** utilizing Docker containerization, AWS Cloud Services, and OAuth2 security protocols.

---

## 🚀 Tech Stack & Tools

* **Backend:** Java 17, Spring Boot, Spring Data JPA, Hibernate
* **Security:** Spring Security, OAuth2.0 (Google & GitHub Integration)
* **Database:** MySQL (Local) / **AWS RDS** (Production)
* **Cloud Storage:** Cloudinary (for fast & secure image uploads)
* **DevOps & Deployment:** Docker, Docker Compose, AWS ECR, AWS EC2, Linux (Ubuntu)

---

## 🏗️ Cloud Architecture & Deployment Flow

This application is fully containerized and deployed on Amazon Web Services (AWS) using the following architecture:

1.  **Containerization:** The Spring Boot application is containerized using **Docker**, ensuring consistency across development and production environments.
2.  **Container Registry:** Docker images are tagged and pushed securely to **AWS ECR (Elastic Container Registry)**.
3.  **Compute Machine:** An **AWS EC2 (Ubuntu)** instance acts as the host server. It uses `docker-compose` to pull the latest image from ECR and run the application in a detached background environment.
4.  **Database Layer:** Business and user data are strictly isolated from the application container and stored securely in a fully managed **AWS RDS (Relational Database Service) MySQL** instance. The EC2 instance communicates with RDS via internal VPC routing and secure environment variables.
5.  **Media Management:** Multipart file uploads (user avatars, contact images) are processed and directly uploaded to **Cloudinary** via REST APIs.

---

## ✨ Key Features

* **Secure Authentication:** Multi-layered security supporting standard form-based login and **OAuth2.0** (Sign in with Google / GitHub).
* **Contact Management:** Complete CRUD (Create, Read, Update, Delete) operations for user contacts.
* **Media Uploads:** Seamless image uploading with dynamic URLs fetched from Cloudinary.
* **Data Validation:** Backend data validation and clean exception handling.
* **Responsive UI:** User-friendly interface accessible across desktop and mobile devices.

---

## 🛠️ Local Setup & Installation

To run this project locally on your machine, follow these steps:

### Prerequisites
* Java 17 or higher
* Maven
* Docker & Docker Compose (optional, for containerized DB)
* MySQL Server

### 1. Clone the Repository
```bash
git clone https://github.com/akhilSharma25/Smart-Contact-Manager.git
cd Smart-Contact-Manager
```

### 2. Setup Environment Variables
Create an `application.properties` or `.env` file in your root directory and configure the following variables:

```properties
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=scmdb
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# OAuth2 Credentials
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_SECRET_KEY=your_secret_key
```

### 3. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

The application will start running on `http://localhost:8080`.

---

## 👨‍💻 Author

**Akhil Sharma**

Full Stack Java Developer

[GitHub Profile](https://github.com/akhilSharma25)
