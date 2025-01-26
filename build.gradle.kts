import java.io.FileInputStream
import java.util.*

val localProperty =
    Properties().apply { load(FileInputStream(rootProject.file("local.properties"))) }

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "3.0.3"
}

group = "ru.mishenko.maksim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.2.0")
    api("tech.ydb:ydb-sdk-bom:2.1.11")
    api("tech.ydb:ydb-sdk-table:2.1.11")
    api("tech.ydb:ydb-sdk-topic:2.1.11")
    api("tech.ydb:ydb-sdk-scheme:2.1.11")
    api("tech.ydb:ydb-sdk-coordination:2.1.11")
    api("tech.ydb.auth:yc-auth-provider:2.1.1")
    api("io.perfmark:perfmark-api:0.27.0")
    implementation("io.ktor:ktor-server-core:3.0.3")
    implementation("io.ktor:ktor-server-cio:3.0.3")

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}

ktor {
    docker {
        jib {
            from {
                image = "openjdk:17-jdk"
            }
            to {
                val dockerHubUrl = localProperty.getProperty("yandex.registry.url") as String
                val yandexToken = localProperty.getProperty("yandex.oauth.token") as String
                image = "$dockerHubUrl/bot"
                auth {
                    username = "oauth"
                    password = yandexToken
                }
            }
        }
    }
}