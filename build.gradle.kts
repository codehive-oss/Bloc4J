plugins {
  id("application")
  kotlin("jvm") version "1.9.23"
}

group = "io.codehive"
version = "1.0-SNAPSHOT"

val mainClassName = "io.codehive.bloc4j.Main"
val joglVersion = "2.3.2"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  implementation("org.jogamp.gluegen:gluegen-rt-main:$joglVersion")
  implementation("org.jogamp.jogl:jogl-all-main:$joglVersion")

  implementation("org.joml:joml:1.10.5")

  implementation("commons-io:commons-io:2.15.1")
}

application {
  mainClass = mainClassName
}
kotlin {
  jvmToolchain(17)
}
