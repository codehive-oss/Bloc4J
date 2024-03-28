plugins {
    id("java")
    id("application")
}

group = "io.codehive"
version = "1.0-SNAPSHOT"

val mainClassName = "io.codehive.bloc4j.Main"
val joglVersion = "2.3.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jogamp.gluegen:gluegen-rt-main:$joglVersion")
    implementation("org.jogamp.jogl:jogl-all-main:$joglVersion")

    implementation("org.joml:joml:1.10.5")
}

application {
    mainClass = mainClassName
}
