import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

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

group = "es.ollie.tech"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    // Networking
    api(libs.bundles.retrofit)
    api(libs.okhttp3.logging.interceptor)
    api(libs.kotlinx.coroutines.core)
    api(libs.jsr305)
    api("ch.qos.logback:logback-classic:1.4.5")

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            credentials {
                username = if (project.hasProperty("art_username")) project.property("art_username").toString() else ""
                password = if (project.hasProperty("art_password")) project.property("art_password").toString() else ""
            }
            url = URI(if (project.hasProperty("art_url")) project.property("art_url").toString() else "")
        }

    }
}

