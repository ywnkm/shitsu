
buildscript {

    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
    }
}

allprojects {

    group = "net.ywnkm"

    repositories {
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        mavenCentral()
    }
}