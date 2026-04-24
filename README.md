## 🚀 Key Technical Features

* **Strategy Design Pattern:** Eliminated complex conditional logic (if/else) by isolating payment rules (PIX, Billet, Credit) into independent, maintainable classes.
* **Asynchronous Batch Processing:** Implemented `@Async` with custom `ThreadPoolTaskExecutor` to handle large transaction volumes without blocking the main API thread.
* **Resilience & Error Handling:** * **Global Exception Handler:** Centralized management of API errors for consistent client responses.
    * **Fault Tolerance:** Batch processing continues even if individual items fail, ensuring high availability.
* **Unit Testing Excellence:** High coverage using **JUnit 5** and **Mockito**, focusing on behavior verification and edge cases (Happy & Sad Paths).
* **Clean Code & Observability:** Utilized **Lombok** for boilerplate reduction and **SLF4J** for structured, professional logging.

## 🛠 Tech Stack
* Java 17 / Spring Boot 3
* Spring Data JPA / PostgreSQL
* JUnit 5 / Mockito
* Maven
