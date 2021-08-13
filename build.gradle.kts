import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val outputName = "Mt-OreAnnouncer"
val outputDir = if (System.getProperty("user.home").contains("Carl")) "Desktop\\temp" else "IdeaProjects\\Compile"
group = "com.montown"
version = "1.17-V1.0.3"

plugins {
    kotlin("jvm") version "1.5.20"
    id("maven-publish")
}

tasks {
    jar {
        //Output name
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("$outputName.jar")
        configurations.compileClasspath.get().filter {
            it.name.startsWith("kotlin") || it.name.startsWith("JDA")
        }.forEach { from(zipTree(it.absolutePath)) }
        configurations.compileOnly.get().filter { !it.path.endsWith(".pom") }.forEach { from(zipTree(it)) }
        destinationDirectory.set(file(System.getProperty("user.home") + "\\$outputDir"))
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
}

repositories {
    mavenCentral()
    maven("https://repo.kinqdos.de/artifactory/max")
    maven("https://repo.kinqdos.de/artifactory/kinqdos-repo")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    compileOnly("net.dv8tion:JDA:4.3.0_307")
    implementation("com.kinqdos", "spigot", "1.17.1")
    implementation(kotlin("stdlib-jdk8"))
}