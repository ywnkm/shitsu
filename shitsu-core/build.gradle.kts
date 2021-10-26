version = "0.0.1"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version Versions.kotlin
    application
}

kotlin {
    explicitApi()
}

dependencies {

    // region kotlin

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // endregion

    api("net.mamoe:mirai-core:${Versions.mirai}")

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
                        "Main-Class" to "com.elouyi.ElyBotApplicationKt",
                )
        )
    }
}
