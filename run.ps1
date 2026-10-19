$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot
$mainClass = if ($args.Count -gt 0) { $args[0] } else { "com.project.Main" }

# Maven descarrega JavaFX i compila el projecte
mvn compile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# JavaFX 22 per a Windows utilitza l'arquitectura x64
$javaSettings = cmd /c "java -XshowSettings:properties -version 2>&1"
$javaArch = ($javaSettings | Select-String '^\s*os.arch = (.*)$').Matches.Groups[1].Value
if ($javaArch -notin @("amd64", "x86_64")) {
    Write-Error "Arquitectura Java no compatible: $javaArch"
    exit 1
}

# Utilitza només els JAR de JavaFX 22 per a Windows
$fxJars = foreach ($module in @("base", "controls", "fxml", "graphics")) {
    $jar = "$env:USERPROFILE\.m2\repository\org\openjfx\javafx-$module\22\javafx-$module-22-win.jar"
    if (-not (Test-Path $jar)) {
        Write-Error "No es pot trobar JavaFX: $jar"
        exit 1
    }
    $jar
}
$fxPath = $fxJars -join ";"

java --module-path $fxPath --add-modules javafx.controls,javafx.fxml `
    -cp target/classes $mainClass
exit $LASTEXITCODE
