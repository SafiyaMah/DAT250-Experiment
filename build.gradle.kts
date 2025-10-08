plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "no.hvl.dat250"
version = "0.0.1-SNAPSHOT"
description = "DAT250: Software Technology Experiment Assignment 1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	// Redis
	implementation("redis.clients:jedis:6.2.0")  
	implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2:2.3.232")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.hibernate.orm:hibernate-core:7.1.1.Final")
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}