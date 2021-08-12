import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val outputName = "Montown-Oreannouncer"
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
            it.name.startsWith("kotlin")
        }.forEach { from(zipTree(it.absolutePath)) }
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
}

dependencies {
    implementation("com.kinqdos", "spigot", "1.17.1")
    implementation(kotlin("stdlib-jdk8"))
}

publishing {
    publications {
        publishing {
            create<MavenPublication>("montown") {
                from(components["java"])
            }
        }
    }

    repositories {
        maven {
            credentials {
                val repoUser: String? by project
                val repoPassword: String? by project
                username = repoUser ?: ""
                password = repoPassword ?: ""
            }
            url = uri("https://repo.kinqdos.de/artifactory/montown")
        }
    }
}