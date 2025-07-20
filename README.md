# Synthetic Human Core Starter

A **Spring Boot Starter** for command handling and auditing. It auto-configures:

* **Dispatcher**: command dispatch with `COMMON` vs `CRITICAL` priorities
* **Async Executor**: thread pool for immediate execution of critical commands
* **Queue Processor**: background processing for common commands
* **AOP Audit**: logs or publishes `enter`/`exit`/`error` events
* **Micrometer Metrics**: metrics for dispatch queue and executor pool

> For a runnable demo, see the [bishop-prototype emulator](https://github.com/Jonnnnh/bishop-prototype)

---

## 1. Build & Publish

1. Build the starter and publish to local Maven repository:

   ```bash
   ./gradlew clean build publishToMavenLocal   
   ```

---

## 2. Add to Your Project

In your **`build.gradle`** include:

```groovy
dependencies {
    implementation 'com.example:synthetic-human-core-starter:0.0.1-SNAPSHOT'
}
```

Ensure your `spring.factories` or `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration` contains the starter’s auto-configuration class

---

## 3. Configuration (application.yml)

```yaml
app:
   executor:
      core-pool-size: 4
      max-pool-size: 10
      queue-capacity: 100
   audit:
      mode: console
      topic: audit.logs

spring:
   kafka:
      bootstrap-servers: localhost:9092
      producer:
         key-serializer: org.apache.kafka.common.serialization.StringSerializer
         value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

management:
   endpoints:
      web:
         exposure:
            include: health,metrics,prometheus

logging:
   level:
      org.springframework.aop: DEBUG
      com.example.synthetichumancore.aspect.AuditAspect: DEBUG
```

> Default `mode=console` writes audit events to the application log

---

## 4. Try It with the Bishop-Prototype Emulator

1. Clone and navigate:

   ```bash
   git clone https://github.com/Jonnnnh/bishop-prototype.git
   cd bishop-prototype
   ```

2. Ensure the starter is published locally, then run:

   ```bash
   ./gradlew clean bootRun
   ```

3. Send commands (similar to usage examples in emulator README)

