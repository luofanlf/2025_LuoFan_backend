# Coin Change Calculator API

A RESTful API service built with Dropwizard framework that provides dynamic programming-based coin change calculation.

## Quick Start

### Local Development

1. **Build the application**
   ```bash
   mvn clean install
   ```

2. **Start the application**
   ```bash
   java -jar target/tech-challenge-1.0-SNAPSHOT.jar server config.yml
   ```

3. **Verify the application is running**
   - Application: `http://localhost:8080`
   

### Docker Container

#### Build Docker Image
```bash
docker build -t tech-challenge .
```

#### Run Container
```bash
docker run -d \
  --name tech-challenge-app \
  -p 8080:8080 \
  -p 8081:8081 \
  tech-challenge
```

#### Container Management
```bash
# View container status
docker ps

# View container logs
docker logs tech-challenge-app

# Stop container
docker stop tech-challenge-app

# Remove container
docker rm tech-challenge-app

# Remove image
docker rmi tech-challenge
```

## API Documentation

### Coin Change Endpoint
- **Endpoint**: `POST /api/coins`
- **Description**: Calculate optimal change for a given amount

#### Request Example
```bash
curl -X POST http://localhost:8080/api/coins \
  -H "Content-Type: application/json" \
  -d '{
    "totalAmount": 11.67,
    "denominations": [500, 200, 100, 50, 20, 10, 5, 2, 1]
  }'
```

#### Response Example
```json
{
  "totalAmount": 11.67,
  "solution": {
    "500": 2,
    "100": 1,
    "50": 1,
    "10": 1,
    "5": 1,
    "2": 1
  },
  "totalCoins": 6
}
```


## CI/CD Pipeline

This project uses GitHub Actions for automated testing and deployment.

### Workflow
1. **Code Push** - Triggers CI/CD pipeline
2. **Test Stage** - Runs unit tests with JUnit 5
3. **Deploy Stage** - Automatically deploys to AWS EC2
4. **Verification** - Health checks and API testing

### Deployment Environment
- **Platform**: AWS EC2 (Ubuntu)
- **Containerization**: Docker
- **Ports**: 80 (API), 8081 (Health Check)
- **URL**: `http://47.129.211.124`

### Trigger Conditions
- Push to `main` branch triggers deployment
- Pull requests trigger test execution

### Manual Deployment
```bash
git push origin main
```

## Testing

### Run Unit Tests
```bash
mvn test
```

### Test Coverage
- Normal change calculation scenarios
- Edge cases and boundary conditions
- No solution scenarios
- Algorithm optimality verification
- Performance testing

## Technology Stack

- **Framework**: Dropwizard
- **Language**: Java 21
- **Build Tool**: Maven
- **Containerization**: Docker
- **Testing**: JUnit 5, AssertJ, Mockito
- **CI/CD**: GitHub Actions
- **Deployment**: AWS EC2
- **Algorithm**: Dynamic Programming

## Project Structure

```
src/main/java/com/oracle/
├── api/                    # REST API controllers
│   └── CoinResource.java
├── service/                # Business logic layer
│   └── CoinService.java
├── dto/                    # Data transfer objects
│   ├── request/
│   │   └── CoinRequest.java
│   └── response/
│       └── CoinResponse.java
├── OracleTechChallengeApplication.java
└── OracleTechChallengeConfiguration.java
```
