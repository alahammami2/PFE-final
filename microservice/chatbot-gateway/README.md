# chatbot-gateway

Microservice Spring Boot minimal agissant comme passerelle pour un chatbot.

- Sans base de données
- Simple endpoint REST `POST /api/chat`
- CORS global autorisé (à durcir en prod)
- Actuator: `health`, `info`

## Prérequis
- Java 17
- Maven 3.9+

## Démarrer en local
```bash
mvn spring-boot:run
```

Le service démarre sur `http://localhost:8099`.

## API
- POST `/api/chat`
  - Body JSON:
    ```json
    {"message": "Bonjour", "metadata": {"userId": "123"}}
    ```
  - Réponse JSON (exemple):
    ```json
    {"id":"...","timestamp":"...","message":"Echo: Bonjour"}
    ```

## Build
```bash
mvn -DskipTests package
```

## Configuration
- Fichier: `src/main/resources/application.yml`
- Port: `8099`

## Notes
- Ce projet n'intègre pas de persistance, sécurité ou Eureka. On peut l'ajouter plus tard si besoin.
