
allprojects {
    repositories {
        mavenCentral()
    }
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.27.0")
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.goncalossilva.resources)
}

subprojects {
    group = "io.github.Kerad20"
    version = "1.0.4"
}
