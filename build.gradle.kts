import jdk.tools.jlink.resources.plugins
import org.jetbrains.kotlin.gradle.targets.js.npm.importedPackageDir

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
    id("io.quarkus")
    id("com.github.johnrengelman.shadow") version ("7.1.0")

}

repositories {
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {

    /* ------------------ SEMANTIC ------------------ */

    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    /* ------------------ DATABASE ------------------ */

    implementation("io.quarkus:quarkus-hibernate-reactive")
    implementation("io.quarkus:quarkus-vertx")
    implementation("io.quarkus:quarkus-reactive-pg-client")

    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.4.0")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:3.4.0")
    implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.4.0")

    /* ------------------ LOMBOK ------------------ */

    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    /* ------------------ REST ------------------ */

    implementation("io.quarkus:quarkus-reactive-routes")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-rest-client-jackson")

    /* ------------------ REACTIVE ------------------ */

    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-web-client")
    implementation("io.vertx:vertx-web-client")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")

    /* ------------------ REFLECTION ------------------ */

    implementation("org.jetbrains.kotlin:kotlin-reflect")


    /* ------------------ SERIALIZATION ------------------ */

    implementation("io.quarkus:quarkus-jackson")

    /* ------------------ XLSX ------------------ */

    implementation("org.dhatim:fastexcel:0.18.4")

    /* ------------------ SECURITY ------------------ */

    implementation("io.quarkus:quarkus-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")

    /* ------------------ OTHER ------------------ */

    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-scheduler")

    /* ------------------ OPENAPI ------------------ */

    implementation("io.quarkus:quarkus-swagger-ui:3.21.0.CR1")
    implementation("io.quarkus:quarkus-smallrye-openapi")

    /* ------------------ TESTS ------------------ */

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    runtimeOnly("org.junit.platform:junit-platform-launcher")

    importedPackageDir(File("test"), "test", "1.0.0")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}


group = "io.diplom"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    ignoreFailures = true
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        javaParameters = true
    }
}
