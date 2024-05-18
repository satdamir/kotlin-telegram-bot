
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
    group = "com.github.kotlintelegrambot"
    version =  "0.3.4"

    repositories {
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}
