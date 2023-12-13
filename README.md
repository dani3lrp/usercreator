# User Creator

User Creator es una aplicación Spring Boot (versión '2.7.12') que proporciona una API RESTful para la creación y gestión de usuarios. Permite registrar nuevos usuarios a través de un endpoint POST y realizar el inicio de sesión mediante un endpoint GET, utilizando tokens JWT para la autenticación.

## Endpoints

### 1. Registro de Usuarios

- **Método:** POST
- **URL:** `http://localhost:8080/users/registro`
- **Ejemplo de Uso:**
  - Enviar una solicitud POST a la URL mencionada con el siguiente cuerpo JSON:

    ```json
    {
      "name": "Juan Rodriguez",
      "email": "juan@rodriguez.org",
      "password": "Hunter22",
      "phones": [
        {
          "number": "1234567",
          "citycode": "1",
          "countrycode": "57"
        },
        {
          "number": "9876543",
          "citycode": "2",
          "countrycode": "58"
        }
      ]
    }
    ```

- **Respuesta Exitosa:**

  ```json
  {
      "user": {
          "id": "0ea47099-1bcf-4eaf-aba3-8d27a551478e",
          "name": "Juan Rodriguez",
          "email": "juan@rodriguez.org",
          "password": "$2a$10$F6wW9dq0eK1C64fGvmUqWeJfFeJIroLkvFE39el7SWpmOE6D0.b5W",
          "createAt": "2023-12-13T12:15:21.728346",
          "lastLogin": "2023-12-13T12:15:21.72833",
          "active": true
      },
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQHJvZHJpZ3UuY29tIiwiZXhwIjoxNjM4ODU5NjI0LCJpYXQiOjE2Mzg4NTkwMjQsInJvbGUiOiJST0xFX1VTRVIifX0.4Rfrs8-pu3iQQiBwIM78Ymrv1ldsj9X4_4kYjLPVJ5A"
  }

### 2. Inicio de Sesión

- **Método:** GET
- **URL:** `http://localhost:8080/users/login`
- **Requerimientos:**
  - En el encabezado de la solicitud (`Authorization`), incluir el token obtenido durante el registro.

- **Ejemplo de Uso para GET `http://localhost:8080/users/login`:**
  - Realiza una solicitud GET a la URL mencionada, asegurándote de incluir el encabezado `Authorization` con el valor del token obtenido durante el registro.

# Configuración del servidor

server.port=8080
spring.application.name=User-Creator

# Configuración de la base de datos H2

spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# Configuración JWT

security.jwt.secret=DEVELOP
security.jwt.ttlMillis=3600000

# Configuración Actuator

management.endpoints.enabled-by-default=false
management.endpoint.health.enabled=true
management.endpoint.health.show-details=always
management.health.db.enabled=false
management.health.diskspace.enabled=true

# Configuración del servidor

server.port=8080
spring.application.name=User-Creator

# Configuración de la base de datos H2

spring.datasource.url=jdbc:h2:mem:userdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# Configuración JWT

security.jwt.secret=DEVELOP
security.jwt.ttlMillis=3600000

# Configuración Actuator

management.endpoints.enabled-by-default=false
management.endpoint.health.enabled=true
management.endpoint.health.show-details=always
management.health.db.enabled=false
management.health.diskspace.enabled=true
  