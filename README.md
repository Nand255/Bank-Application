# Bank Application REST API

This project is a RESTful API for a banking application, allowing CRUD (Create, Read, Update, Delete) operations on banking resources. It is built using Spring Boot and provides features for user account management, transactions, and more.

## Features

- User account creation and management
- Credit and debit operations for accounts
- Perform and track transactions
- Download transaction records for a particular user
- Email integration for notifications (using Spring Email)
- Secure endpoints protected with Spring Security
- Persistent data storage with MySQL and Spring Data JPA

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA
- MySQL
- Spring Security
- Spring Email

## Getting Started

### Prerequisites

- Java 11 or newer
- Maven or Gradle
- MySQL Database

### Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Nand255/Bank-Application.git
   cd Bank-Application
   ```

2. **Configure your MySQL database:**
   - Create a database in MySQL (e.g., `bank_db`).
   - Update `src/main/resources/application.properties` with your database username and password:
     ```
     spring.datasource.url=jdbc:mysql://localhost:3306/bank_db
     spring.datasource.username=YOUR_USERNAME
     spring.datasource.password=YOUR_PASSWORD
     ```

3. **Build the project:**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.

### API Endpoints

| Method | Endpoint                        | Description                                 |
|--------|---------------------------------|---------------------------------------------|
| POST   | /users                          | Create a new user account                   |
| GET    | /users/{id}                     | Get user details by ID                      |
| PUT    | /users/{id}                     | Update user details                         |
| DELETE | /users/{id}                     | Delete user account                         |
| POST   | /accounts/{id}/credit           | Credit amount to user account               |
| POST   | /accounts/{id}/debit            | Debit amount from user account              |
| POST   | /accounts/{id}/transactions     | Perform a new transaction                   |
| GET    | /accounts/{id}/transactions     | Get all transactions for a user             |
| GET    | /accounts/{id}/transactions/csv | Download transaction records as CSV         |

*(Update with your actual resource names and endpoints.)*

## Security

- Endpoints are secured using Spring Security.
- Authentication required for sensitive operations.

## Email Integration

- Email notifications (e.g., for transactions) are sent using Spring Email.
- Configure your SMTP settings in `application.properties` as needed.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
