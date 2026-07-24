plugins {
    java
    id("org.springframework.boot") version "4.0.6"
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
    implementation("org.mvnpm:playwright:1.60.0")
    implementation("software.amazon.awssdk:s3:2.46.3")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.mvnpm.at.playwright:test:1.60.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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