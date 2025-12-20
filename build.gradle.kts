plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.5" apply false
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.gradle.maven-publish")
}

group = "com.bluent.interfaces"
version = "0.0.1-SNAPSHOT"

val springBootVersion = "3.4.5"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven {
		name = "nexus"
        url = uri("http://148.230.116.99:8081/repository/NebryonModule/")
		isAllowInsecureProtocol = true
		credentials {
			username = "admin"
            password = System.getenv("NEXUS_PASSWORD")
        }
	}
	maven {
		url = uri("https://maven.pkg.github.com/SPYDP13/module")
		credentials {
			username = System.getenv("USERNAME_GITHUB") ?: ""
			password = System.getenv("TOKEN_GITHUB") ?: ""
		}
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
	implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.3")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("com.mysql:mysql-connector-j:8.2.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
	implementation("com.nebryon.modules:annotations-module:1.0.2")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}


tasks.getByName<Jar>("jar") {
	enabled = true
}

//tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
//	enabled = false
//}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			groupId = "com.nebryon.modules"
			artifactId = "interfaces-module"
			version = "1.0.5"
		}
	}

	repositories {
		maven {
			name = "nexus"
            url = uri("http://148.230.116.99:8081/repository/NebryonModule/")
			isAllowInsecureProtocol = true
			credentials {
				username = "admin"
                password = System.getenv("NEXUS_PASSWORD")
			}
		}
	}
}


//publishing {
//	publications {
//		create<MavenPublication>("gpr") {
//			from(components["java"]) // ou "kotlin" si applicable
//			groupId = "com.github.bluent"
//			artifactId = "interfaces-module"
//			version = "1.0.2"
//		}
//	}
//
//	repositories {
//		maven {
//			name = "GitHubPackages"
//			url = uri("https://maven.pkg.github.com/SPYDP13/module")
//
//			credentials {
//				username = System.getenv("USERNAME_GITHUB")
//				password = System.getenv("TOKEN_GITHUB")
//			}
//		}
//	}
//}