import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.kotlinx.binaryCompatibilityValidator)
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
}

group="com.github.kotlintelegrambot"
version="6.1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Networking
    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jsr305)

    // Testing
    testImplementation(libs.bundles.junit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockk)
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.withType<Jar> {
    enabled = true
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    enabled = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
//        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

tasks.test {
    useJUnitPlatform()
}
