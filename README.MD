# Ilmpay Scholarship Platform 🚀

![Ilmpay Hero Section](src/main/resources/static/images/hero-screenshot.png)

A modern scholarship management platform connecting students with educational funding opportunities. Built with Spring Boot and ❤️

## Features ✨
- 🌍 **Multi-language Support** (EN, RU, UZ) with dynamic content switching
- 📱 **Mobile-First Design** with responsive layouts and touch optimization
- 👑 **Admin Dashboard** with real-time statistics and user management
- 🎨 **Support Logo System** with AWS S3 integration and FilePond uploads
- 🔐 **Secure Authentication** with role-based access control
- 📊 **Dynamic Content** powered by Thymeleaf templates
- 🐳 **Docker-Ready** deployment with PostgreSQL integration

## Tech Stack 💻
| Category       | Technologies                                                                 |
|----------------|-----------------------------------------------------------------------------|
| **Backend**    | Java 23, Spring Boot 3.4.2, Spring Security, Spring Data JPA, PostgreSQL    |
| **Frontend**   | Bootstrap 5, HTMX, FilePond, SweetAlert2, Font Awesome, Custom CSS          |
| **Cloud**      | AWS S3 for file storage and CDN delivery                                    |
| **DevOps**     | Docker, Maven, Spring Boot Docker Compose support                           |
| **I18n**       | Thymeleaf i18n with message properties for EN/RU/UZ                        |

## Installation 🛠️

### Prerequisites
- Java 23 or higher
- Maven 3.8+
- Docker and Docker Compose (optional)
- PostgreSQL 15+ (if running without Docker)
- AWS Account with S3 bucket (for file storage)

### Local Development Setup
1. Clone the repository:
```bash
git clone https://github.com/neKamita/ilmpay.git
cd ilmpay
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

### Docker Setup
1. Build and run with Docker Compose:
```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`

## Configuration ⚙️
1. Create `application-dev.yml` in `src/main/resources/` for local development:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ilmpay
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    
server:
  port: 8080

ilmpay:
  admin:
    email: admin@ilmpay.uz
  security:
    jwt:
      secret: your_jwt_secret
      expiration: 86400000
  aws:
    access-key: your_aws_access_key
    secret-key: your_aws_secret_key
    bucket-name: your_bucket_name
    region: your_aws_region
```

2. Set active profile in `application.yml`:
```yaml
spring:
  profiles:
    active: dev
```

## Project Structure 📁
```
src/
├── main/
│   ├── java/uz/pdp/ilmpay/
│   │   ├── config/          # Security, AWS and Web configurations
│   │   ├── controller/      # MVC Controllers (Web/API endpoints)
│   │   ├── model/          # Domain models and entities
│   │   ├── repository/     # Data access layer (JPA repositories)
│   │   ├── service/       # Business logic and AWS S3 services
│   │   ├── security/      # Security configurations and JWT utils
│   │   └── util/          # Utility classes and helpers
│   └── resources/
│       ├── messages/       # i18n properties files
│       ├── static/        # CSS/JS/Images
│       │   ├── js/       # JavaScript modules and utilities
│       │   ├── css/      # Stylesheets and animations
│       │   └── images/   # Static images and icons
│       └── templates/    # Thymeleaf template files
```

## Latest Updates 🆕
- Added Support Logo Management System with AWS S3 integration
- Enhanced file upload experience with FilePond
- Improved admin dashboard with new management features
- Better error handling and user feedback
- Centralized configuration for file uploads

## Contributing 🤝
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License 📝
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments 🙏
- Spring Boot team for the amazing framework
- FilePond for the beautiful file upload experience
- AWS for reliable cloud storage solutions
- All contributors who have helped shape this project
