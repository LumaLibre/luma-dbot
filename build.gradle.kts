plugins {
    id("java")
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "dev.jsinco.luma.discord"
version = "1.5"

repositories {
    mavenCentral()
    maven("https://repo.jsinco.dev/releases")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    implementation("dev.jsinco.discord:jda-framework:1.7")
    // JDA
    implementation("net.dv8tion:JDA:5.0.0-beta.24") {
        exclude("org.slf4j", "slf4j-api")
    }
    // Logger
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.24.1")
    implementation("org.apache.logging.log4j:log4j-api:2.24.1")

    // Going to provide my own guava/gson and okaeri configs for this example.

    // Google guava/gson
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("com.google.code.gson:gson:2.10.1")

    // Config
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.5")
}

tasks {

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }

    shadowJar {
        manifest {
            attributes(
                "Main-Class" to "dev.jsinco.luma.discord.Main"
            )
        }
        dependencies {

        }
        archiveBaseName.set(project.rootProject.name)
        archiveClassifier.set("")
    }
}
kotlin {
    jvmToolchain(21)
}