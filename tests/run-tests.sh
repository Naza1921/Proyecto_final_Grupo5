#!/bin/sh
set -eu

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT"

JAVAC=${JAVAC:-javac}
JAVA=${JAVA:-java}
JAVAFX_LIB=${JAVAFX_LIB:-}

if [ -z "$JAVAFX_LIB" ] || [ ! -d "$JAVAFX_LIB" ]; then
    printf '%s\n' "Set JAVAFX_LIB to the JavaFX lib directory before running tests." >&2
    exit 2
fi

BUILD_DIR=$(mktemp -d)
trap 'rm -rf "$BUILD_DIR"' EXIT HUP INT TERM
MODULES=javafx.controls,javafx.fxml,javafx.media,javafx.swing
mkdir -p "$BUILD_DIR/classes" "$BUILD_DIR/tests"

find src -name '*.java' -print0 |
    xargs -0 "$JAVAC" -encoding UTF-8 \
        --module-path "$JAVAFX_LIB" --add-modules "$MODULES" \
        -d "$BUILD_DIR/classes"

find tests -name '*Test.java' -print0 |
    xargs -0 "$JAVAC" -encoding UTF-8 \
        --module-path "$JAVAFX_LIB" --add-modules "$MODULES" \
        -cp "$BUILD_DIR/classes" -d "$BUILD_DIR/tests"

"$JAVA" --module-path "$JAVAFX_LIB" --add-modules "$MODULES" \
    -cp "$BUILD_DIR/classes:$BUILD_DIR/tests" \
    controlador.GestorRecogidaPowerUpsTest
"$JAVA" --module-path "$JAVAFX_LIB" --add-modules "$MODULES" \
    -cp "$BUILD_DIR/classes:$BUILD_DIR/tests" \
    modelos.items.FabricaPowerUpsTest
