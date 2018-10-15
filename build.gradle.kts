import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version = "0.9.5"
group = "com.ktor.finance"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.2.71"
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("io.ktor:ktor-auth-jwt:$ktor_version")
    testCompile("io.ktor:ktor-server-test-host:$ktor_version")
    testCompile(group = "junit", name = "junit", version = "4.12")
}
