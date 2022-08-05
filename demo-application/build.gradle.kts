plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    application
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.4.21"
}

group = "de.jds.sync-demo"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":spring-sync-spring-boot-starter"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

//task.nam {
//    useJUnitPlatform()
//}