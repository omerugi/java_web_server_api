# Phone Book API

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