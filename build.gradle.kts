plugins {
    id("java")
}

group = "cairocraft.anoobis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(files("libs/java-exaroton-api-v1.6.1.jar"))
}

tasks.test {
    useJUnitPlatform()
}