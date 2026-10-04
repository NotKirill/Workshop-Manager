plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "notkirill"
version = "0.0.1-SNAPSHOT"
description = "workshop-manager-backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.mvnpm:playwright:1.60.0")
    implementation("software.amazon.awssdk:s3:2.46.3")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("com.bucket4j:bucket4j_jdk17-core:8.21.0")
    implementation("com.bucket4j:bucket4j_jdk17-redis-common:8.21.0")
    implementation("com.bucket4j:bucket4j_jdk17-lettuce:8.21.0")
    implementation("io.github.resilience4j:resilience4j-retry:2.4.0")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.4.0")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    // CVE fix in dependency manager
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")
    testImplementation("org.springframework.boot:spring-boot-starter-quartz-test")
    testImplementation("org.mvnpm.at.playwright:test:1.60.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    dependencies {
        // CVE-2026-54515 jjwt-jackson fix
        dependency("com.fasterxml.jackson.core:jackson-databind:2.21.5")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootRun {
    systemProperty("spring.profiles.active", "dev")
}

tasks.bootTestRun {
    systemProperty("spring.profiles.active", "dev")
}

tasks.register<Exec>("dockerComposeUp") {
    group = "docker"
    commandLine("docker", "compose", "-f", "compose.dev.yaml", "up", "-d")
}

tasks.register<Exec>("dockerComposeDown") {
    group = "docker"
    commandLine("docker", "compose", "-f", "compose.dev.yaml", "down")
}