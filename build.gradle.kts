import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val outputName = "Mt-OreAnnouncer"
val outputDir = if (System.getProperty("user.home").contains("Carl")) "Desktop\\temp" else "IdeaProjects\\Compile"
group = "com.montown"
version = "1.0.2"

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
    mavenLocal()
    maven("https://m2.dv8tion.net/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("net.dv8tion:JDA:5.0.0-beta.8")
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.md-5", "bungeecord-api", "1.19-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}