plugins {
    kotlin("multiplatform") version "1.5.10"
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
