---
name: ejemplo-skill
description: Skill de ejemplo que muestra cómo crear skills para Pi. Úsala cuando necesites una plantilla base para crear tus propias skills personalizadas.
---

# Ejemplo Skill

Esta skill demuestra la estructura básica para crear skills en Pi.

## Uso

Cuando se cargue esta skill, sigue estas instrucciones:

1. Lee `references/guia.md` para entender las convenciones
2. Los scripts en `scripts/` se ejecutan relativos al directorio de la skill
3. Los assets en `assets/` son archivos de soporte (plantillas, configs, etc.)

## Ejemplo de script

Ejecuta el script de ejemplo:
```bash
./scripts/saludo.sh "Tu Nombre"
```

## Estructura de la skill

```
ejemplo-skill/
├── SKILL.md           # Este archivo (obligatorio)
├── scripts/
│   └── saludo.sh      # Script de ejemplo
├── references/
│   └── guia.md        # Documentación de referencia
└── assets/
    └── plantilla.txt  # Archivo de plantilla
```

## Comandos disponibles

- `/skill:ejemplo-skill` - Carga esta skill
- `/skill:ejemplo-skill ayuda` - Muestra esta ayuda