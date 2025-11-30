# 🚀 Proyecto: Gestión de Contactos (Migración a Maven y Serialización JSON)

Este documento resume la migración, modernización, e implementación de nuevas funcionalidades en la aplicación de Gestión de Contactos, cumpliendo con los requisitos de arquitectura moderna y persistencia de datos.

## 1. ⚙️ Arquitectura del Proyecto y Configuración Base

El proyecto se reestructuró bajo el estándar **Maven** para manejar dependencias y el ciclo de vida de construcción.

### 1.1 Estructura del Código

| Directorio | Contenido | Propósito |
| :--- | :--- | :--- |
| `src/main/java` | Clases del Modelo, Vista y Controlador. | Lógica y presentación del negocio. |
| `src/main/resources` | **Archivos de Recursos** (`.properties`). | **Ubicación clave** para los archivos de idioma (`idiomas/mensajes_es.properties`).  |
| `target/` | Archivo JAR final (generado por Maven Shade). | Artefacto ejecutable distribuible. |

### 1.2 `pom.xml` (Gestión de Dependencias y Versiones)

El archivo `pom.xml` fue configurado para utilizar **Java 21** (coherente con el SDK instalado) y centralizar las versiones en el bloque `<properties>` (Punto 4.a).

| Propiedad | Valor | Justificación |
| :--- | :--- | :--- |
| `maven.compiler.source` | 21 | Coincide con el JDK de ejecución. |
| `flatlaf.version` | 3.4.1 | Versión estable para apariencia moderna. |
| `gson.version` | 2.10.1 | Versión estable para persistencia JSON. |

## 2. 🎨 Modernización y Dependencias (Punto 2)

Se incorporaron dos librerías clave, justificadas por su estabilidad y el valor que añaden al proyecto.

### 2.1 FlatLaf (Modernización UI)

| Aspecto | Uso en el Código | Beneficio |
| :--- | :--- | :--- |
| **Integración** | Se añadió la dependencia al `pom.xml` y se invocó `UIManager.setLookAndFeel(new FlatLightLaf())` en el constructor de `ventana.java`. | **Mejora estética radical**, proporcionando un *Look and Feel* plano y profesional a la interfaz Swing. |

### 2.2 Google Gson (Persistencia JSON)

| Aspecto | Uso en el Código | Beneficio |
| :--- | :--- | :--- |
| **Justificación** | Su repositorio activo y la propiedad de Google garantizan **seguridad** y **estabilidad**.  | Migración de persistencia de CSV a **JSON**, ofreciendo un formato de datos estructurado y legible. |

---

## 3. 💾 Manejo de Datos: Serialización JSON (Punto 3)

Se refactorizó completamente la capa de persistencia (`personaDAO.java`) para gestionar los datos como un objeto JSON único.

### 3.1 Serialización y Deserialización (Punto 3.a)

| Método | Función | Proceso |
| :--- | :--- | :--- |
| **`guardarContactos()`** | **Serialización.** | Utiliza `gson.toJson(List<persona>, FileWriter)` para escribir la lista completa de contactos en `datosContactos.json`. |
| **`leerArchivo()`** | **Deserialización.** | Utiliza `gson.fromJson(FileReader, TypeToken)` para restaurar la lista de objetos desde el archivo JSON. |

### 3.2 Importación de Archivo Externo (Punto 3.b)

Esta funcionalidad permite fusionar una fuente de contactos externa con la lista actual.

* **Interfaz:** Se añadió el botón **`btn_importar`** a `ventana.java`.
* **Controlador (`logica_ventana`):** El método `importarContactosJSON()` ejecuta el flujo:
    1.  Abre un diálogo **`JFileChooser`** para seleccionar el archivo.
    2.  Llama a `dao.importarContactos()` para leer el JSON externo.
    3.  Añade los contactos importados a la lista actual (`contactos.addAll()`).
    4.  Guarda la lista **fusionada** de vuelta al archivo principal.

---

## 4. 🔒 Gestión Avanzada de Dependencias (Punto 4)

Se aplicaron principios de ingeniería de software para la gestión de dependencias externas.

### 4.1 Coherencia y Control de Versiones

Se aseguró que el **SDK (Java 21)**, el **Language Level (21)** en IntelliJ, y los *plugins* de compilación de Maven (`pom.xml`) apunten a la misma versión para evitar inconsistencias en el *runtime*.

### 4.2 Exclusión de Transitivas (Punto 4.b)

* **Determinación:** Se auditó la estructura de dependencias y se confirmó que FlatLaf y Gson **no introducen dependencias transitivas** obsoletas o conflictivas.
* **Resultado:** Se omitió el uso de las etiquetas `<exclusions>` y `<dependencyManagement>`, manteniendo el proyecto limpio y minimizando la complejidad innecesaria.

### 4.3 Generación del JAR Final

Se utilizó el **Maven Shade Plugin** para empaquetar todo el código de la aplicación junto con las librerías FlatLaf y Gson en un solo archivo `.jar` ejecutable, asegurando que el programa funcione en cualquier entorno Java sin requerir configuración adicional de dependencias.
