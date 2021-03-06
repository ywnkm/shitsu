version = "0.0.1"
group = "net.ywnkm"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version Versions.kotlin
    application
    `maven-publish`
}

kotlin {
    explicitApi()

    sourceSets {
        all {
            languageSettings {
                // languageVersion = "1.6"
            }
        }
    }
}

dependencies {

    // region kotlin

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")
    implementation("io.ktor:ktor-client-serialization:${Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktor}")

    // endregion

    api("net.mamoe:mirai-core:${Versions.mirai}")

    implementation("org.yaml:snakeyaml:${Versions.snakeyaml}")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("net.ywnkm.shitsu.MainKt")
}

tasks.register<Jar>("fatJar") {
    group = "ywnkm"


    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    exclude(
            "META-INF/*.SF",
            "META-INF/*.DSA",
            //"META-INF/*.RSA",
            //"META-INF/*.kotlin_module",
            "META-INF/*.md"
            //"**/*.kotlin_metadata",
            //"**/*.kotlin_builtins",
            //"**/*.kotlin_module"
    )
    manifest {
        attributes(
                mapOf(
                        "Main-Class" to "net.ywnkm.shitsu.MainKt",
                )
        )
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

