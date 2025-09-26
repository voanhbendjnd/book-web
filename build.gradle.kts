plugins {
	java
	id("org.springframework.boot") version "3.2.12"
    id("io.spring.dependency-management") version "1.1.4"
    id("io.freefair.lombok") version "8.12.1"
}

group = "com.djnd"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	 implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.turkraft.springfilter:jpa:3.1.7")
    runtimeOnly("com.mysql:mysql-connector-j")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.22.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
   implementation("org.mapstruct:mapstruct:1.6.2")
annotationProcessor("org.mapstruct:mapstruct-processor:1.6.2")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
