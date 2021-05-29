plugins {
    kotlin("multiplatform") version "1.5.10"
    java
}

group = "micapolos"
version = "0.25"

repositories {
    mavenCentral()
}

kotlin {
    macosX64 {
        binaries {
            executable {
                entryPoint = "leo25.main"
            }
        }
    }
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
                        attributes["Main-Class"] = "leo25.MainKt"
                    }
                    from(
                        configurations
                            .getByName("compileClasspath")
                            .map { if (it.isDirectory) it else zipTree(it) },
                        main.output.classesDirs)
                    archiveBaseName.set("${project.name}-fat")
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
        val macosX64Main by getting
        val macosX64Test by getting
        val jvmMain by getting
        val jvmTest by getting
    }
}
