import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("co.touchlab.kotlinxcodesync")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(targetVersion)

    defaultConfig {
        minSdkVersion(minVersion)
        targetSdkVersion(targetVersion)
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

xcodeSync {
    projectPath = "../iosApp/iosApp.xcodeproj"
    target = "iosApp"
}

kotlin {
    iosArm64 {
        binaries {
            framework {
                embedBitcode("disable")
            }
        }
        compilations.forEach { compilation ->
            compilation.kotlinOptions.freeCompilerArgs += "-Xobjc-generics"
        }
    }
    iosX64 {
        binaries {
            framework {
                embedBitcode("disable")
            }
        }
        compilations.forEach { compilation ->
            compilation.kotlinOptions.freeCompilerArgs += "-Xobjc-generics"
        }
    }
    android()

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutinesVersion")

                implementation("org.kodein.di:kodein-di-erased:$kodeinVersion")
                implementation("com.russhwolf:multiplatform-settings:$multiplatformSettingVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")

                implementation("com.github.adevone.summer:summer:$summerVersion")
//                implementation(project(":summer"))
            }
        }
        getByName("androidMain") {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
            }
        }
        getByName("iosArm64Main") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serializationVersion")
                implementation("io.ktor:ktor-client-core-native:$ktorVersion")
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
        }
        getByName("iosX64Main").dependsOn(getByName("iosArm64Main"))
    }
}

tasks.create("copyFramework") {
    val buildType = project.findProperty("kotlin.build.type")?.toString() ?: "DEBUG"
    val targetName = if (buildType == "DEBUG") "iosX64" else "iosArm64"
    dependsOn("link${buildType.toLowerCase().capitalize()}Framework${targetName.capitalize()}")
    doLast {
        val target = kotlin.targets.getByName(targetName) as KotlinNativeTarget
        val srcFile = target.binaries.getFramework(buildType).outputFile
        val targetDir = project.property("configuration.build.dir")!!
        copy {
            from(srcFile.parent)
            into(targetDir)
            include("shared.framework/**")
            include("shared.framework.dSYM")
        }
    }
}