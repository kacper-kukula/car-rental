[Unique Functionalities](#unique-functionalities) • [Database Structure](#database-structure) • [How To Use](#getting-started) • [Obstacles and Strategies](#obstacles-and-strategies) • [Conclusion](#conclusion)


# Car Rental Management System

Welcome to the Car Rental Management System! This project offers a seamless web-based solution for managing car sharing inventory, handling car rentals, managing customers, processing payments via Stripe, and providing notifications via Telegram Bot. Whether you're a manager overseeing the fleet or a customer renting a car, this system caters to all your needs.

## Inspiration

The inspiration for this project arose from the necessity for a modern and efficient system to manage car rental operations. By harnessing the capabilities of Java Spring Boot and related technologies, the aim was to develop a robust and user-friendly platform that streamlines car rental processes.

## Technologies and Tools

<p align="center">
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117201156-9a724800-adec-11eb-9a9d-3cd0f67da4bc.png" alt="Java" title="Java"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117207242-07d5a700-adf4-11eb-975e-be04e62b984b.png" alt="Maven" title="Maven"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183891303-41f257f8-6b3d-487c-aa56-c497b880d0fb.png" alt="Spring Boot" title="Spring Boot"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117207493-49665200-adf4-11eb-808e-a9c0fcc2a0a0.png" alt="Hibernate" title="Hibernate"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192107858-fe19f043-c502-4009-8c47-476fc89718ad.png" alt="REST" title="REST"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117533873-484d4480-afef-11eb-9fad-67c8605e3592.png" alt="JUnit" title="JUnit"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183892181-ad32b69e-3603-418c-b8e7-99e976c2a784.png" alt="mockito" title="mockito"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183891673-32824908-bc5d-44f8-8f72-f0415822404a.png" alt="Liquibase" title="Liquibase"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/183896128-ec99105a-ec1a-4d85-b08b-1aa1620b2046.png" alt="MySQL" title="MySQL"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/117207330-263ba280-adf4-11eb-9b97-0ac5b40bc3be.png" alt="Docker" title="Docker"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/186711335-a3729606-5a78-4496-9a36-06efcc74f800.png" alt="Swagger" title="Swagger"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192108372-f71d70ac-7ae6-4c0d-8395-51d8870c2ef0.png" alt="Git" title="Git"/></code>
	<code><img width="50" src="https://user-images.githubusercontent.com/25181517/192108890-200809d1-439c-4e23-90d3-b090cf9a4eea.png" alt="IntelliJ" title="IntelliJ"/></code>
</p>
<br />

- **As well as**: JWT, Jackson, Lombok, MapStruct, Spring Security, Spring Data JPA, Telegram API, Stripe API

## Unique Functionalities

### Authentication Controller

- **[public] `POST /register`**: Register a new user
- **[public] `POST /login`**: Obtain JWT tokens for authentication

### HealthCheck Controller

- **[public] `GET /health`**: Check the health of the app

### User Controller

- **[manager] `PUT /users/{id}/role`**: Update user role
- **[user] `GET /users/me`**: Get current user's profile info
- **[user] `PATCH /users/me`**: Update current user's profile info

### Car Controller

- **[manager] `POST /cars`**: Add a new car to the inventory
- **[public] `GET /cars`**: Get a list of available cars
- **[public] `GET /cars/{id}`**: Get detailed information about a specific car
- **[manager] `PATCH /cars/{id}`**: Update car details, including inventory
- **[manager] `DELETE /cars/{id}`**: Remove a car from the inventory

### Rental Controller

- **[user] `POST /rentals`**: Add a new rental (decrease car inventory by 1)
- **[user] `GET /rentals`**: Retrieve rental history
- **[user] `GET /rentals/?user_id=...&is_active=...`**: Filter rentals by user ID and active status
- **[user] `POST /rentals/{rentalId}/return`**: Set actual return date (increase car inventory by 1)

### Payment Controller (Stripe)

- **[user] `POST /payments`**: Create a payment session for a rental
- **[user] `GET /payments/?user_id=...`**: Retrieve payment history
- **[public] `GET /payments/success/`**: Check successful Stripe payments
- **[public] `GET /payments/cancel/`**: Return payment paused message

## Database Structure

I've included a diagram illustrating the structure of the MySQL database which can be found below.
<p align="center">
<img src="https://i.imgur.com/z7oqqrd.png" alt="Database Diagram"/>
</p>

## Getting Started

1. Make sure to install [IDE](https://www.jetbrains.com/idea/), [Maven](https://maven.apache.org/download.cgi), [Docker](https://www.docker.com/products/docker-desktop/), [JDK 17+](https://www.oracle.com/pl/java/technologies/downloads/)
2. Clone the repository.
3. Configure the .env file with your database credentials and ports and add it to root project path. Example:
```
MYSQLDB_USER=root
MYSQLDB_ROOT_PASSWORD=root
MYSQLDB_DATABASE=car_rental

MYSQLDB_LOCAL_PORT=3307
MYSQLDB_DOCKER_PORT=3306

SPRING_LOCAL_PORT=8081
SPRING_DOCKER_PORT=8080
DEBUG_PORT=5005

JWT_SECRET_STRING=superLoooong1234554321AndStrong1112345SecretAndSecureString
TELEGRAM_BOT_TOKEN=<Your Own Telegram Bot Token>
TELEGRAM_CHAT_ID=<Your Own Telegram Chat Id>
STRIPE_SECRET_KEY=<Your Own Stripe Secret Key>

# This file is essential for the app to work. Make sure it's configured properly.
# Follow Telegram and Stripe API instructions to generate your own keys.
```
4. Ensure Docker Desktop is running.
5. Build and run the application using Docker: `docker-compose up --build`
6. Access the API documentation at Swagger UI: `http://localhost:[SPRING_LOCAL_PORT]/api/swagger-ui/index.html`

You can now access the endpoints using `Swagger` or `Postman`. To access the functionality, you can register as customer or use one of the pre-defined credentials:
```
{
    "email": "manager@example.com",
    "password": "safePassword"
}

{
    "email": "customer@example.com",
    "password": "safePassword"
}

```
After logging in, you receive a `Bearer Token` which you must then provide as authorization to access the endpoints.

## Obstacles and Strategies

Throughout the development of the car rental application, I encountered challenges such as managing environment variables securely and implementing the Stripe API for payment processing. Ensuring that environmental variables were correctly configured and accessed across different environments was crucial.

To address this, I utilized the dotenv library, which allowed me to load environment variables from a .env file into the application. This approach ensured that sensitive information, such as API keys and database credentials, remained secure and easily configurable across development, testing, and production environments.

Implementing the Stripe API for payment processing required careful attention, especially in handling the success and cancel endpoints. By thoroughly studying the Stripe documentation and experimenting with different configurations, I set up secure and reliable payment sessions. This ensured a smooth user experience during the payment process.

Leveraging Spring Boot's built-in features and seeking guidance from the developer community helped refine my solutions and enhance the overall performance and security of the car rental application.

## Conclusion

Thank you for exploring the Car Rental Management System! This project represents a culmination of efforts to create a modern and efficient solution for managing car rentals. While the current functionality meets the outlined requirements, there's always room for improvement and expansion.

As I move forward, potential enhancements include refining the user interface, implementing additional payment options, and optimizing system performance. Your feedback and suggestions are invaluable as I continue to evolve and enhance the system.

I hope you find the Car Rental Management System intuitive and effective for your car rental needs. Should you have any questions, feedback, or ideas, please don't hesitate to reach out. Your input drives my continuous improvement efforts.

Happy renting!
