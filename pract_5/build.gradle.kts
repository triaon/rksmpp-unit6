plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    application
}

group = "ru.mirea.barnatovich"
version = "1.0"

val ktorVersion = "3.6.0"
val exposedVersion = "1.6.0"
val openApiToolsVersion = "5.7.0"

application {
    mainClass.set("ru.mirea.barnatovich.nobelserver.ApplicationKt")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    // сервер
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // клиент для разового импорта из публичного Nobel Prize API
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // PostgreSQL
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.mindrot:jbcrypt:0.4")

    // Swagger UI
    implementation("io.github.smiley4:ktor-openapi:$openApiToolsVersion")
    implementation("io.github.smiley4:ktor-swagger-ui:$openApiToolsVersion")
}
