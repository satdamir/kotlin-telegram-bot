
buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    idea
    alias(libs.plugins.kotlin.jvm).apply(false)
}

allprojects {
    group = "es.ollie.tech"
    version =  "1.0.0"

    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}
