allprojects {
    repositories {
        google()
        mavenCentral()
        // Lets :sdk-integration resolve the locally-provided Fawry AAR (see
        // example/sdk-integration/build.gradle) as a module dependency rather than a raw file
        // dependency, which is required by newer Android Gradle Plugin versions. `rootDir` is
        // always this build's root (android/), so the path stays correct for every subproject.
        flatDir { dirs(rootDir.resolve("../../example/sdk-integration/libs")) }
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
