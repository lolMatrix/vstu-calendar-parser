

val gitVersion: groovy.lang.Closure<String> by extra

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.gradleup.shadow") version "8.3.1"
    id("com.palantir.git-version") version "3.0.0"
}

group = "ru.matrix"
version = gitVersion()

repositories {
    mavenCentral()
}

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.gradleup.shadow:shadow-gradle-plugin:8.3.1")
    }
}

dependencies {
    implementation("org.jxls:jxls-jexcel:1.0.9")
    implementation("org.dhatim:fastexcel-reader:0.18.3")
    implementation("org.dhatim:fastexcel:0.18.3")
    implementation("org.mnode.ical4j:ical4j:4.0.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.jar.configure {
    manifest {
        attributes(mapOf("Main-Class" to "ru.matrix.MainKt"))
    }
}
