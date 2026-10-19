#!/bin/bash
set -e

cd "$(dirname "$0")"
mainClass=${1:-com.project.Main}

# Maven descarrega JavaFX i compila el projecte
mvn compile

# Tria la mateixa arquitectura que utilitza Java
javaArch=$(java -XshowSettings:properties -version 2>&1 | awk '/^[[:space:]]*os.arch = / { print $3 }')
case "$(uname -s)" in
    Darwin) fxPlatform="mac" ;;
    Linux)  fxPlatform="linux" ;;
    *) echo "Sistema no compatible amb aquest script."; exit 1 ;;
esac
case "$javaArch" in
    aarch64|arm64) fxPlatform="${fxPlatform}-aarch64" ;;
    amd64|x86_64) ;;
    *) echo "Arquitectura Java no compatible: $javaArch"; exit 1 ;;
esac

# Utilitza només els JAR de JavaFX 22 d'aquest sistema
fxPath=""
for module in base controls fxml graphics; do
    jar="$HOME/.m2/repository/org/openjfx/javafx-$module/22/javafx-$module-22-$fxPlatform.jar"
    if [[ ! -f "$jar" ]]; then
        echo "No es pot trobar JavaFX: $jar"
        exit 1
    fi
    fxPath="${fxPath:+$fxPath:}$jar"
done

exec java --module-path "$fxPath" --add-modules javafx.controls,javafx.fxml \
    -cp target/classes "$mainClass"
