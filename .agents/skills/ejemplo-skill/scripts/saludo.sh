#!/bin/bash
# Script de ejemplo para la skill

NOMBRE=${1:-"Mundo"}
echo "¡Hola, $NOMBRE! Bienvenido a tu skill de Pi."
echo "Directorio de la skill: $(dirname "$0")/.."