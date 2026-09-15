import java.io.File
import java.security.KeyStore

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

// Function to resolve property or environment variable across multiple common naming conventions
fun resolvePropOrEnv(vararg names: String): String? {
    for (name in names) {
        val envVal = System.getenv(name)
        if (!envVal.isNullOrBlank()) return envVal.trim()
        val propVal = project.findProperty(name)?.toString()
        if (!propVal.isNullOrBlank()) return propVal.trim()
    }
    return null
}

// Helper to discover the real PrivateKeyEntry alias dynamically from a keystore file
fun findRealAlias(keystoreFile: File, storePassCandidates: List<String>): String? {
    // 1. Try in-process Java KeyStore inspection
    for (pass in storePassCandidates) {
        for (storeType in listOf("PKCS12", "JKS", KeyStore.getDefaultType())) {
            try {
                val ks = KeyStore.getInstance(storeType)
                keystoreFile.inputStream().use { ks.load(it, pass.toCharArray()) }
                val aliases = ks.aliases().toList()
                for (alias in aliases) {
                    if (ks.isKeyEntry(alias)) {
                        println("[Signing] Found PrivateKeyEntry alias via KeyStore ($storeType): $alias")
                        return alias
                    }
                }
                if (aliases.isNotEmpty()) {
                    println("[Signing] Found alias via KeyStore ($storeType): ${aliases.first()}")
                    return aliases.first()
                }
            } catch (_: Exception) {
                // Ignore and try next format/password
            }
        }
    }

    // 2. Fallback to keytool CLI inspection if available
    for (pass in storePassCandidates) {
        try {
            val process = ProcessBuilder("keytool", "-list", "-keystore", keystoreFile.absolutePath, "-storepass", pass)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            for (line in output.lines()) {
                if (line.contains("PrivateKeyEntry", ignoreCase = true)) {
                    val alias = line.split(",")[0].trim()
                    println("[Signing] Found PrivateKeyEntry alias via keytool: $alias")
                    return alias
                }
                val match = Regex("Alias name:\\s*(.+)", RegexOption.IGNORE_CASE).find(line)
                if (match != null) {
                    val alias = match.groupValues[1].trim()
                    println("[Signing] Found alias name via keytool: $alias")
                    return alias
                }
            }
        } catch (_: Exception) {
            // Ignore keytool errors
        }
    }

    return null
}

android {
    namespace = "com.example.domino"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.domino.jkpqxz"
        minSdk = 24
        targetSdk = 36
        versionCode = 5
        versionName = "5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val envStoreFile = resolvePropOrEnv(
            "RELEASE_STORE_FILE", "KEYSTORE_PATH", "KEYSTORE_FILE", "ANDROID_KEYSTORE_PATH",
            "STORE_FILE", "PLAY_STORE_FILE", "UPLOAD_KEYSTORE_PATH", "releaseStoreFile"
        )
        val envStorePass = resolvePropOrEnv(
            "RELEASE_STORE_PASSWORD", "KEYSTORE_PASSWORD", "STORE_PASSWORD", "ANDROID_KEYSTORE_PASSWORD",
            "PLAY_STORE_PASSWORD", "UPLOAD_KEYSTORE_PASSWORD", "releaseStorePassword"
        )
        val envKeyPass = resolvePropOrEnv(
            "RELEASE_KEY_PASSWORD", "KEY_PASSWORD", "ANDROID_KEY_PASSWORD",
            "PLAY_KEY_PASSWORD", "UPLOAD_KEY_PASSWORD", "releaseKeyPassword"
        )
        val envKeyAlias = resolvePropOrEnv(
            "RELEASE_KEY_ALIAS", "KEY_ALIAS", "ANDROID_KEY_ALIAS",
            "PLAY_KEY_ALIAS", "UPLOAD_KEY_ALIAS", "releaseKeyAlias"
        )

        var resolvedStoreFile: File? = if (envStoreFile != null) file(envStoreFile) else null

        // Dynamic Keystore Discovery in /tmp if not explicitly provided or found
        if (resolvedStoreFile == null || !resolvedStoreFile.exists()) {
            val tmpDir = file("/tmp")
            if (tmpDir.exists() && tmpDir.isDirectory) {
                val jksFiles = tmpDir.listFiles()?.filter { f ->
                    f.isFile && (f.name.endsWith(".jks") || f.name.endsWith(".keystore") || f.name.endsWith(".p12")) && !f.name.contains("debug", ignoreCase = true)
                }
                if (!jksFiles.isNullOrEmpty()) {
                    resolvedStoreFile = jksFiles.first()
                    println("[Signing] Discovered platform keystore file: ${resolvedStoreFile.absolutePath}")
                }
            }
        }

        val storePassCandidates = listOfNotNull(envStorePass, envKeyPass, "android", "", "changeit", "password").distinct()
        val finalStorePass = envStorePass ?: "android"
        val finalKeyPass = envKeyPass ?: finalStorePass

        var resolvedAlias: String? = null
        if (resolvedStoreFile != null && resolvedStoreFile.exists()) {
            resolvedAlias = findRealAlias(resolvedStoreFile, storePassCandidates)
        }
        if (resolvedAlias == null) {
            resolvedAlias = envKeyAlias
        }

        create("release") {
            if (resolvedStoreFile != null && resolvedStoreFile.exists()) {
                storeFile = resolvedStoreFile
                storePassword = finalStorePass
                keyAlias = resolvedAlias ?: "android"
                keyPassword = finalKeyPass
                println("[Signing] Configured release signing: file=${resolvedStoreFile.name}, alias=$keyAlias")
            }
        }
    }

    buildTypes {
        release {
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning?.storeFile != null) {
                signingConfig = releaseSigning
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
