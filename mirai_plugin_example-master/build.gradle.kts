plugins {
    val kotlinVersion = "1.7.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.13.0-RC2"
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenLocal()
    maven("https://repo.mirai.mamoe.net/snapshots")
    maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { url = uri("https://jitpack.io") }
    mavenCentral()

}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("cn.bigmodel.openapi:oapi-java-sdk:release-V4-2.1.0")
}

