import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version = "1.0.0-beta-3"
val exposed_version = "0.10.5"
val h2_version = "1.4.196"

group = "com.ktor.finance"
version = "0.0.1"

plugins {
    application
    kotlin("jvm") version "1.3.0"
    id("com.github.johnrengelman.shadow") version "2.0.1"
}
application {
    mainClassName = "com.ktor.finance.Main"
}

/* JAR packaging */
val shadowJar: ShadowJar by tasks
shadowJar.apply {
    baseName = "finance-server"
    version = version
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    maven { url = uri("https://dl.bintray.com/kotlin/exposed") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("io.ktor:ktor-jackson:$ktor_version")
    compile("io.ktor:ktor-websockets:$ktor_version")
    compile("io.ktor:ktor-auth-jwt:$ktor_version")

    compile("com.h2database:h2:$h2_version")
    compile("org.jetbrains.exposed:exposed:$exposed_version")
    compile("com.zaxxer:HikariCP:3.2.0")

    compile("ch.qos.logback:logback-classic:1.2.3")

    testCompile("io.ktor:ktor-server-test-host:$ktor_version")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testCompile("org.assertj:assertj-core:3.10.0")
    testCompile("io.rest-assured:rest-assured:3.1.0")
    testCompile(group = "junit", name = "junit", version = "4.12")
}

