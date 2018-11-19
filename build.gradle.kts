import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
  jcenter()
  maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
  maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
  maven { url = uri("https://dl.bintray.com/kotlin/exposed") }
  maven { url = uri("https://plugins.gradle.org/m2/") }
}

plugins {
  application
  kotlin("jvm") version "1.3.10"
  id("com.github.johnrengelman.shadow") version "4.0.2"
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

val ktor_version = "1.0.0"
val exposed_version = "0.11.2"
val h2_version = "1.4.196"

group = "com.ktor.finance"
version = "0.0.1"

application {
  mainClassName = "com.ktor.finance.MainKt"
}

/* jar packaging */
val shadowJar: ShadowJar by tasks
shadowJar.apply {
  baseName = "ktorfinance"
  version = version
  classifier = ""
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  implementation("io.ktor:ktor-server-netty:$ktor_version")
  implementation("io.ktor:ktor-jackson:$ktor_version")
  implementation("io.ktor:ktor-websockets:$ktor_version")
  implementation("io.ktor:ktor-auth-jwt:$ktor_version")

  implementation("com.h2database:h2:$h2_version")
  implementation("org.jetbrains.exposed:exposed:$exposed_version")
  implementation("com.zaxxer:HikariCP:3.2.0")

  implementation("ch.qos.logback:logback-classic:1.2.3")

  testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
  testImplementation("org.assertj:assertj-core:3.10.0")
  testImplementation("io.rest-assured:rest-assured:3.1.0")
  testImplementation(group = "junit", name = "junit", version = "4.12")
}

