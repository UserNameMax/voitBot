plugins {
    kotlin("jvm") version "2.1.0"
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
    implementation ("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.2.0")
    api("tech.ydb:ydb-sdk-bom:2.1.11")
    api("tech.ydb:ydb-sdk-table:2.1.11")
    api("tech.ydb:ydb-sdk-topic:2.1.11")
    api("tech.ydb:ydb-sdk-scheme:2.1.11")
    api("tech.ydb:ydb-sdk-coordination:2.1.11")
    api("tech.ydb.auth:yc-auth-provider:2.1.1")
    api("io.perfmark:perfmark-api:0.27.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}