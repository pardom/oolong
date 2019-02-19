import oolong.Dependencies

plugins {
    kotlin("jvm")
}

repositories {
    jcenter()
}

dependencies {
    api(project(":oolong"))
    implementation(Dependencies.Kotlin.Coroutines.Swing)
}

apply(from = rootProject.file("gradle/gradle-mvn-push.gradle"))
