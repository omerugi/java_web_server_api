# Phone Book API

A robust RESTful API for managing contacts, built with Java 17, Spring Boot 3, Maven, and PostgreSQL.

## Key Features

- RESTful API design
- Swagger API documentation
- Database connection pooling
- Caching for improved performance
- Containerized with Docker for easy deployment
- Pagination support for efficient data retrieval (limited to max 10 per page)
- Using Google's libphonenumber to validate phone numbers.

## Technologies

- Java 17
- Spring Boot 3
- Maven
- PostgreSQL
- Docker

## Contact Entity

The Contact entity represents the core data structure for storing contact information. Each contact has the following fields:

| Field       | Type            | Description                                           |
|-------------|-----------------|-------------------------------------------------------|
| id          | Long            | Unique identifier for the contact (auto-generated)    |
| firstName   | String          | First name of the contact                             |
| lastName    | String          | Last name of the contact                              |
| phone       | String          | Phone number of the contact                           |
| countryCode | CountryCode     | Enum representing the country code of the phone number|
| createdAt   | LocalDateTime   | Timestamp when the contact was created                |
| updatedAt   | LocalDateTime   | Timestamp when the contact was last updated           |


### Notes:
- The `id` field is automatically generated and serves as the primary key in the database.
- `firstName` and `lastName` are required fields and cannot be null or empty.
- `phone` must follow a valid phone number format.
- `countryCode` is an enum that represents the country code of the phone number. The default value is 'IL' (Israel).
- `createdAt` is automatically set when a new contact is created.
- `updatedAt` is automatically updated whenever the contact information is modified.

### Phone Number Validation

Phone number validation is performed using Google's libphonenumber library, which is part of the i18n (internationalization) project. This ensures that:

1. The phone number format is valid for the specified country code.
2. The phone number contains the correct number of digits for the country.
3. The phone number follows the expected pattern for the country (e.g., area code, local number).

This validation helps maintain data integrity and ensures that stored phone numbers are in a standardized, internationally recognized format.

When interacting with the API, these fields will be represented in JSON format. For example:
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "countryCode": "IL",
  "createdAt": "2023-07-27T10:30:00",
  "updatedAt": "2023-07-27T10:30:00"
}
```

when creating or updating a contact, you don't need to provide id **IN THE PAYLOAD**, createdAt, or updatedAt fields, as these are managed automatically by the system.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
- Git
- Docker
- PostgreSQL (running locally or in a container)

### Setup and Running the Application
1. Clone the repository: </br>
``` 
git clone https://github.com/your-username/phonebook-api.git
```
```
cd phonebook-api
```
2. Build the Docker image:
```
docker build -t phonebook-api:latest .
```
3. Run the Docker container:
```
docker run -p 8080:8080
-e DB_URL=jdbc:postgresql://host.docker.internal:5432/your_database_name
-e DB_USERNAME=your_database_username
-e DB_PASSWORD=your_database_password
phonebook-api:latest
```

Replace `your_database_name`, `your_database_username`, and `your_database_password` with your actual PostgreSQL database details. Note: `host.docker.internal` is used to connect to the host machine's localhost from within the Docker container. If you're using Linux, you might need to use your machine's IP address instead.

4. Access the application:
   The application should now be running and accessible at `http://localhost:8080`

### API Documentation
Once the application is running, you can view the API documentation at:
`http://localhost:8080/swagger-ui.html`

## Caching

The application implements caching to improve performance for frequently accessed data. Cached data includes:

- Individual contacts (by ID)
- List of contacts (paginated results)

## Database Connection Pooling

HikariCP is used for efficient database connection pooling, improving the application's performance and scalability.
