plugins {
    id("java-library")
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}
kotlin {

    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24
    }

    dependencies {
        compileOnly(libs.kotlin.gradle.plugin.api)
        compileOnly(libs.kotlin.gradle.plugin)

        compileOnly(libs.kotlin.compiler.embeddable)
    }
}

group = "br.com.brd"
version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

gradlePlugin {
    plugins {
        create("debugLogPlugin") {
            id = "br.com.brd.debuglog"
            version = "1.0.0"
            implementationClass = "br.com.brd.processor.DebugLogGradleSubPlugin"
        }
    }
}

