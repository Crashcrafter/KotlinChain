import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "dev.crash"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0-rc1")
    implementation("org.bouncycastle:bcprov-jdk15to18:1.69")
    implementation("io.ktor:ktor-server-core:1.6.3")
    implementation("io.ktor:ktor-server-netty:1.6.3")
    implementation("org.slf4j:slf4j-api:1.8.0-beta4")
    implementation("org.slf4j:slf4j-simple:1.8.0-beta4")
    implementation("org.lmdbjava:lmdbjava:0.8.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.30")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes(mapOf("Implementation-Title" to project.name, "Implementation-Version" to project.version, "Main-Class" to "dev.crash.MainKt"))
    }
    doLast {
        file("./run").delete()
        file("./run").createNewFile()
        file("./build/libs/${project.name}-${project.version}-all.jar").copyTo(file("./run/${project.name}-${project.version}-all.jar"), overwrite = true)
    }
}