# shop.io — Amazon-like microservices demo (Java)

A minimal Spring Boot microservices demo that mimics a tiny slice of Amazon:
browse products, add to cart, pick a shipping address (user), place an order,
and see your past orders.

## Architecture

```
┌──────────────────┐     HTTP      ┌──────────────────┐
│ frontend-service │ ────────────▶ │ product-service  │   :8081
│  (Thymeleaf UI)  │ ────────────▶ │ user-service     │   :8082
│      :8080       │ ────────────▶ │ order-service    │   :8083
└──────────────────┘               └──────────────────┘
                                            │  ▲
                                            │  │ (order calls product + user)
                                            ▼  │
                                    product-service / user-service
```

All four services are independent Spring Boot apps in a multi-module Maven build.
State is in-memory (`ConcurrentHashMap`) — restarting a service wipes its data.

| Service           | Port | Purpose                                                    |
|-------------------|------|------------------------------------------------------------|
| `product-service` | 8081 | Catalog + stock reservation                                |
| `user-service`    | 8082 | Customer / shipping address records                        |
| `order-service`   | 8083 | Places orders, calls product- and user-service             |
| `frontend-service`| 8080 | Thymeleaf web UI; aggregates the three backends            |

## Run locally (no Docker)

Requires JDK 17+ and Maven 3.9+.

```bash
# Terminal 1
mvn -pl product-service -am spring-boot:run

# Terminal 2
mvn -pl user-service -am spring-boot:run

# Terminal 3
mvn -pl order-service -am spring-boot:run

# Terminal 4
mvn -pl frontend-service -am spring-boot:run
```

Then open http://localhost:8080.

## Run with Docker Compose

Each service has its own `Dockerfile` and produces its own image
(`shop/product-service:1.0.0`, `shop/user-service:1.0.0`,
`shop/order-service:1.0.0`, `shop/frontend-service:1.0.0`).

```bash
docker compose up --build
```

Browse to http://localhost:8080.

To build a single service's image directly:

```bash
docker build -f product-service/Dockerfile -t shop/product-service:1.0.0 .
```

(Note the build context is the repo root — Maven needs the parent `pom.xml`.)

## Try the backend APIs

```bash
# List products
curl localhost:8081/products

# List users
curl localhost:8082/users

# Place an order
curl -X POST localhost:8083/orders \
  -H 'Content-Type: application/json' \
  -d '{"userId":"u-1","items":[{"productId":"p-1001","quantity":2}]}'

# View orders for a user
curl 'localhost:8083/orders?userId=u-1'
```

## Selenium UI tests

A separate `selenium-tests` Maven module runs end-to-end UI tests with
Selenium 4 + TestNG + ExtentReports against a running app. Page Object
Model lives under `src/test/java/com/example/shop/selenium/pages/`.

The module is opt-in (it needs the app running) — activate the `selenium`
profile to include it.

### Prerequisites

- App running at some URL (default expects `http://localhost:8080`)
- Chrome (default) or Firefox installed locally

### Run from CLI

```bash
# 1. Start the app (any way — docker compose, native, etc.)
docker compose up -d
# (docker compose maps to 9080–9083; native run uses 8080–8083)

# 2. Run the suite
mvn -Pselenium -pl selenium-tests test \
    -Dapp.baseUrl=http://localhost:9080 \
    -Dheadless=true
```

ExtentReport HTML lands at
`selenium-tests/target/extent-report/ExtentReport-*.html`
(open it in a browser).

### Run from IDE (IntelliJ / Eclipse)

1. Right-click `selenium-tests/src/test/resources/testng.xml` → Run
2. Or right-click any test class → Run as TestNG test
3. Override base URL via Run Configuration **VM options**:
   `-Dapp.baseUrl=http://localhost:9080 -Dheadless=false`

### Run on GitHub Actions

The `.github/workflows/selenium.yml` workflow:
1. Builds all four service jars
2. Starts each service as a background process
3. Waits for `/products`, `/users`, `/orders`, and the frontend to respond
4. Runs the Selenium suite headless against `http://localhost:8080`
5. Uploads the ExtentReport, Surefire reports, and service logs as artifacts

## Notes

- The `order-service` calls `product-service` to fetch price + reserve stock,
  and calls `user-service` to validate the user. This is the cross-service
  flow worth tracing if you bolt on logging or distributed tracing.
- `frontend-service` keeps the cart in HTTP session — open the site in a
  fresh incognito window if you want a clean cart.
- This is a teaching/demo app: no DB, no auth, no resilience patterns
  (circuit breakers, retries, etc.). Add those if you want to extend it.
