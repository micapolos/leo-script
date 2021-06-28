plugins {
    kotlin("multiplatform") version "1.5.10"
    java
}

group = "micapolos"
version = "0.25.6"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
        compilations {
            val main = getByName("main")
            tasks {
                register<Jar>("buildFatJar") {
                    group = "application"
                    dependsOn(build)
                    manifest {
                        attributes["Main-Class"] = "leo.MainKt"
                    }
                    from(
                        configurations
                            .getByName("compileClasspath")
                            .map { if (it.isDirectory) it else zipTree(it) },
                        main.output.classesDirs)
                    archiveBaseName.set("leo")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
    }
}
