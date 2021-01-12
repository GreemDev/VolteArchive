import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "volte"
version = "4.0.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("net.dv8tion:JDA:4.2.0_226") {
        exclude("opus-java")
    }
    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.h2database:h2:1.4.200")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("Volte.jar")
    relocate("net.dv8tion.jda", "volte.lib.discord")
    relocate("club.minnced.opus", "volte.lib.discord.opus")
    relocate("com.iwebpp.crypto", "volte.lib.discord.crypto")
    relocate("com.fasterxml.jackson", "volte.lib.jackson")
    relocate("ch.qos.logback", "volte.lib.logging.impl")
    relocate("com.jagrosh.jdautilities", "volte.lib.jdautils")
    relocate("org.slf4j", "volte.lib.logging")
    relocate("org.h2", "volte.lib.h2")
    relocate("okhttp3", "volte.lib.http")
    relocate("kotlin", "volte.lib.kotlin")
    relocate("com.google.gson", "volte.lib.gson")
    relocate("gnu", "volte.lib.gnu")
    relocate("com.sun", "volte.lib.sun")
    relocate("natives", "volte.lib.natives")
}

application {
    mainClassName = "volte.meta.Main"
}