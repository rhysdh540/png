plugins {
    id("java-library")
}

group = "dev.rdh"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor(compileOnly("org.projectlombok:lombok:1.18.28")!!)
    compileOnly("org.jetbrains:annotations:24.1.0")
}