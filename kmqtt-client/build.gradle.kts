plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("convention.publication")
    id("org.jetbrains.kotlinx.atomicfu")
}

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    iosX64 {}
    iosArm64 {}

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":kmqtt-common"))
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {}
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        val posixMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.atomicfu)
            }
        }

        val iosX64Main by getting {
            dependsOn(posixMain)
        }
        val iosArm64Main by getting {
            dependsOn(posixMain)
        }
    }
}

// Fix Gradle warning about signing tasks using publishing task outputs without explicit dependencies
// https://github.com/gradle/gradle/issues/26091
tasks.withType<AbstractPublishToMaven>().configureEach {
    val signingTasks = tasks.withType<Sign>()
    mustRunAfter(signingTasks)
}
//
//publishing {
//    repositories {
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/Kerad20/KMQTT_EC_Support")
//            credentials {
//                username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
//                password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_TOKEN")
//            }
//        }
//    }
//}

tasks.withType<Sign>().configureEach {
    enabled = false
}
