# Guía de Referencia - Ejemplo Skill

## Convenciones

- Los scripts deben ser ejecutables (`chmod +x`)
- Usa rutas relativas al directorio de la skill
- Los argumentos se pasan después del nombre de la skill: `/skill:ejemplo-skill arg1 arg2`

## Buenas prácticas

1. **Frontmatter válido**: name, description son obligatorios
2. **Descripción clara**: Debe indicar QUÉ hace y CUÁNDO usarla
3. **Scripts portables**: Prefiere bash/Python sobre binarios específicos
4. **Documenta en references/**: Mantén la documentación separada

## Campos de frontmatter soportados

| Campo | Descripción |
|-------|-------------|
| `name` | Nombre del comando (lowercase, hyphens) |
| `description` | Para routing del modelo (máx 1024 chars) |
| `license` | Licencia (ej: MIT) |
| `compatibility` | Requisitos de entorno |
| `metadata` | Key-value extra |
| `allowed-tools` | Herramientas pre-aprobadas (experimental) |
| `disable-model-invocation` | Solo carga con `/skill:name` explícito |