# Registro de Versiones - NutriSalud

Todas las modificaciones relevantes del proyecto se documentan en este archivo.
El formato se basa en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/)
y el proyecto adhiere al [Versionado Semántico](https://semver.org/lang/es/).

---

## [0.0.2] - Refactorización General v1 - 2026-09-05

### Added
- **Lombok** integrado en el build con `@AllArgsConstructor` y `@NoArgsConstructor` en entidades.
- Dependencia `com.h2database:h2` en scope `test` para ejecutar tests unitarios sin requerir SQL Server real.
- Archivo `src/test/resources/application.properties` con perfil de test basado en H2 en memoria (`MODE=MSSQLServer`).
- Propiedad `api.version=v1` en `application.properties` para versionamiento dinámico de API.
- Propiedad `app.database.host` documentativa que refleja el host configurado vía `DB_HOST`.
- `CHANGELOG.md` como registro de versiones del proyecto.
- `getNombreCompleto()` en la entidad `Paciente`, `PacienteEvaluable`, `PacienteBase` y `PacienteDecorator`
  para compatibilidad de lectura del nombre completo concatenado.
- Nuevos métodos en `PacienteRepository`:
  - `findByPrimerNombreContainingIgnoreCaseOrApellidoPaternoContainingIgnoreCaseOrApellidoMaternoContainingIgnoreCase(...)`
  - `buscarPorTextoEnNombres(String texto)` (default method de conveniencia).

### Changed
- **Refactorización de nombres en Paciente**: el campo `nombre` único se reemplazó por
  `primerNombre` (columna `primer_nombre`, NOT NULL),
  `apellidoPaterno` (columna `apellido_paterno`, NOT NULL) y
  `apellidoMaterno` (columna `apellido_materno`, NULLABLE).
  - Actualizados: entidad, PacienteFactory, PacienteService (interfaz + implementación),
    PacienteController, PacienteEvaluable, PacienteBase y PacienteDecorator.
  - Las firmas de `registrarNuevoPaciente` y `crearPaciente` reciben ahora los 3 campos.
- **Conexión SQL Server**: valor por defecto de `DB_HOST` cambió de `localhost` a `192.168.1.100`
  (placeholder de IP privada; sobreescribible por variable de entorno).
  - Corregido el prefijo erróneo `+spring.datasource.driver-class-name` → `spring.datasource.driver-class-name`.
- **Endpoints versionados** en el controlador:
  - Base del controller `PacienteController` pasa de `/web` a `/api/${api.version}/pacientes`.
  - Rutas de pacientes `/pacientes/...` quedaron reducidas a `""`, `"/{dni}"`, etc. evitando segmentos duplicados.
  - Rutas de seguros (`/seguros`, `/tipos-seguro`) y citas (`/citas/...`) reubicadas con rutas absolutas
    bajo `/api/${api.version}/...` para no colgar incorrectamente del prefijo `/pacientes`.
- **Validaciones en `agregarPaciente`**: además del DNI, se valida que `primerNombre` y `apellidoPaterno`
  no sean nulos ni vacíos, devolviendo 400 con mensaje descriptivo.

### Infrastructure
- `pom.xml`:
  - Añadida dependencia `org.projectlombok:lombok` (optional=true, sin scope provided explícito para que el BOM gestione la versión).
  - `maven-compiler-plugin` configurado con `annotationProcessorPaths` para Lombok.
  - `spring-boot-maven-plugin` declara exclusión de Lombok en el fat-jar final.
  - Añadida dependencia `com.h2database:h2` en scope `test`.
- Entidades anotadas con Lombok:
  - `Paciente`: `@AllArgsConstructor` + `@NoArgsConstructor` (se eliminaron constructores manuales redundantes).
  - `Cita`: `@AllArgsConstructor` + `@NoArgsConstructor`. Se conserva constructor de 4 argumentos
    que inicializa `estado = PENDIENTE`.
  - `Seguro`: `@AllArgsConstructor` + `@NoArgsConstructor`. Se conserva constructor de 1 argumento
    que inicializa `nombreLegible = tipo.getEtiqueta()`.

### Removed
- Campo `nombre` y métodos `getNombre()`/`setNombre()` de la entidad `Paciente`.
- Método `getNombre()` de la interfaz `PacienteEvaluable` (reemplazado por los tres getters individuales y `getNombreCompleto()`).
- Método `findByNombreContainingIgnoreCase` de `PacienteRepository` (reemplazado por búsqueda combinada).

### Notas de migración de datos
- Si existían registros previos en la tabla `pacientes` con valores en la columna `nombre`,
  `spring.jpa.hibernate.ddl-auto=update` creará las tres nuevas columnas pero **no migrará automáticamente**
  el contenido. Se recomienda ejecutar un script DML manual de la forma:

  ```sql
  UPDATE pacientes SET
    primer_nombre     = LEFT(nombre, CHARINDEX(' ', nombre + ' ') - 1),
    apellido_paterno  = CASE WHEN CHARINDEX(' ', nombre) > 0
                             THEN SUBSTRING(nombre, CHARINDEX(' ', nombre) + 1,
                                            ISNULL(NULLIF(CHARINDEX(' ', nombre, CHARINDEX(' ', nombre) + 1), 0),
                                                   LEN(nombre) + 1) - CHARINDEX(' ', nombre) - 1)
                             ELSE '' END,
    apellido_materno  = CASE WHEN CHARINDEX(' ', nombre, CHARINDEX(' ', nombre) + 1) > 0
                             THEN SUBSTRING(nombre, CHARINDEX(' ', nombre, CHARINDEX(' ', nombre) + 1) + 1, LEN(nombre))
                             ELSE '' END;
  ```

  Posteriormente, validar los datos y entonces será seguro eliminar la columna `nombre`.

---

## [0.0.1] - Versión Inicial
- Primera versión funcional del backend NutriSalud:
  entidades Paciente/Cita/Seguro, servicios, repositorios JPA, controlador REST,
  patrones Singleton, Factory, Decorator, evaluación de umbral de anemia y asignación automática de citas.
