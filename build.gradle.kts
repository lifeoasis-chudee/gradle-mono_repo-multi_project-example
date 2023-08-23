import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22" apply false
    id("org.springframework.boot") version "3.1.2" apply false
    id("io.spring.dependency-management") version "1.1.2" apply false
    id("com.google.protobuf") version "0.9.4"
    id("com.google.cloud.tools.jib") version "3.3.2"
}

// https://github.com/grpc/grpc-java/releases
ext["grpcVersion"] = "1.56.1"
// https://github.com/grpc/grpc-kotlin/releases
ext["grpcKotlinVersion"] = "1.3.0"
// https://github.com/protocolbuffers/protobuf/releases
ext["protobufVersion"] = "3.23.4"
ext["grpcSpringBootVersion"] = "2.14.0.RELEASE"
// https://docs.spring.io/spring-framework/reference/languages/kotlin/coroutines.html#dependencies
ext["coroutinesVersion"] = "1.7.3"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    dependencies {
        api("com.fasterxml.jackson.module:jackson-module-kotlin")
        api("org.jetbrains.kotlin:kotlin-reflect")
        api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        // coroutines
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.ext["coroutinesVersion"]}")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${rootProject.ext["coroutinesVersion"]}")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project("grpc-lib") {
    apply {
        plugin("com.google.protobuf")
    }

    dependencies {
        api("io.grpc:grpc-netty-shaded:${rootProject.ext["grpcVersion"]}")
        api("io.grpc:grpc-stub:${rootProject.ext["grpcVersion"]}")
        api("io.grpc:grpc-protobuf:${rootProject.ext["grpcVersion"]}")
        api("io.grpc:grpc-kotlin-stub:${rootProject.ext["grpcKotlinVersion"]}")
        api("com.google.protobuf:protobuf-java-util:${rootProject.ext["protobufVersion"]}")
        api("com.google.protobuf:protobuf-kotlin:${rootProject.ext["protobufVersion"]}")
        api("jakarta.annotation:jakarta.annotation-api:2.1.1")
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:${rootProject.ext["protobufVersion"]}"
        }
        plugins {
            create("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.ext["grpcVersion"]}"
            }
            create("grpckt") {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:${rootProject.ext["grpcKotlinVersion"]}:jdk8@jar"
            }
        }
        generateProtoTasks {
            ofSourceSet("main").forEach {
                it.plugins {
                    create("grpc")
                    create("grpckt")
                }
                it.builtins {
                    create("kotlin")
                }
            }
        }
    }

    tasks.named<BootJar>("bootJar") {
        enabled = false
    }
    tasks.named<Jar>("jar") {
        enabled = true
    }
}

project("user") {
    apply {
        plugin("com.google.cloud.tools.jib")
    }

    dependencies {
        implementation(project(":grpc-lib"))

        implementation("net.devh:grpc-spring-boot-starter:${rootProject.ext["grpcSpringBootVersion"]}")
        implementation("org.springframework.boot:spring-boot-starter-web")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.named<BootJar>("bootJar") {
        enabled = true
    }
    tasks.named<Jar>("jar") {
        enabled = true
    }

    jib {
        from {
            image = "azul/zulu-openjdk-alpine:17-latest"
        }
        to {
            image = "localhost:8080/my-image/user"
            tags = setOf("latest")
        }
        container {
            jvmFlags = listOf("-Dmy.property=example.value", "-Xms512m", "-Xdebug")
            ports = listOf("8080", "9090")
        }
    }
}

project("graphql") {
    apply {
        plugin("com.google.cloud.tools.jib")
    }

    dependencies {
        implementation(project(":grpc-lib"))

        implementation("net.devh:grpc-spring-boot-starter:${rootProject.ext["grpcSpringBootVersion"]}")
        implementation("org.springframework.boot:spring-boot-starter-graphql")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework:spring-webflux")
        testImplementation("org.springframework.graphql:spring-graphql-test")
    }

    tasks.named<BootJar>("bootJar") {
        enabled = true
    }
    tasks.named<Jar>("jar") {
        enabled = true
    }

    jib {
        from {
            image = "azul/zulu-openjdk-alpine:17-latest"
        }
        to {
            image = "localhost:8080/my-image/graphql"
            tags = setOf("latest")
        }
        container {
            jvmFlags = listOf("-Dmy.property=example.value", "-Xms512m", "-Xdebug")
            ports = listOf("8080")
        }
    }
}
