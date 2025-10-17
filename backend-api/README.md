# RepeatWise Backend API

Backend API for RepeatWise - Flashcard + Spaced Repetition System

## Technology Stack

- **Java**: 17
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven 3.8+
- **Mapping**: MapStruct
- **File Processing**: Apache POI, OpenCSV

## Design Patterns

### Layered Architecture
- **Controller Layer**: HTTP request/response handling, validation
- **Service Layer**: Business logic, transaction management
- **Repository Layer**: Database access via Spring Data JPA

### Design Patterns Used
1. **Composite Pattern**: Folder tree structure
2. **Strategy Pattern**: Review order, Forgotten card actions
3. **Visitor Pattern**: Folder statistics calculation
4. **Repository Pattern**: Data access abstraction
5. **DTO Pattern**: Data transfer with MapStruct
6. **Domain Events**: Async operations

## Setup

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Application starts on `http://localhost:8080`
