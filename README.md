# 🗣️ Presentación del Proyecto: Gestión de Contactos (Migración a Maven y JSON)

Este resumen detalla el proceso completo de modernización que apliqué a mi aplicación de Gestión de Contactos, cubriendo los requisitos de arquitectura, persistencia de datos y gestión avanzada de dependencias.

## 1. ⚙️ Fundación y Configuración Inicial (Migración a Maven)

Mi primer paso fue migrar el proyecto a la arquitectura **Maven**, esencial para gestionar dependencias externas y estandarizar el proceso de construcción.

### 1.1 Estructura del Proyecto

Me aseguré de seguir el estándar de directorios de Maven. Esto fue crucial, especialmente en la colocación de recursos:

* **`src/main/resources`:** Aquí ubiqué mi carpeta **`idiomas`**, resolviendo el `MissingResourceException` que encontré inicialmente. Esta es la ubicación oficial de Maven para recursos.
* **Clase Principal:** Designé a `org.example.vista.ventana` como la clase de inicio en el `pom.xml`.

### 1.2 Configuración del JDK y Maven

Configuré mi entorno para máxima coherencia, lo cual fue vital para resolver el `UnsatisfiedLinkError`:

* **Entorno de Ejecución:** Utilicé el JDK **Java 21** para ejecutar el proyecto, instalando una versión estable de Oracle OpenJDK.
* **Coherencia de Versiones:** Sincronicé el **SDK de IntelliJ** y el **Language Level** a **21** para coincidir con el JDK instalado, eliminando conflictos de *runtime*.
* **POM:** Mi `pom.xml` finaliza con la configuración para compilar en Java 21.

## 2. 🎨 Modernización de la Interfaz y Persistencia (Punto 2 y 3)

Para modernizar la aplicación y mejorar el manejo de datos, introduje dos librerías clave.

### 2.1 FlatLaf: Apariencia Gráfica Moderna

* **Justificación:** Elegí FlatLaf por ser una solución *Look and Feel* activa y ligera, lo que me permitió eliminar el estilo anticuado de Swing.
* **Implementación:** Llamé a `UIManager.setLookAndFeel(new FlatLightLaf())` en el constructor de `ventana.java`.

### 2.2 Google Gson: Serialización JSON

* **Justificación:** Elegí Gson por su estabilidad institucional (Google) y su **API limpia**, que me permite un mapeo directo de objetos a JSON. Su repositorio no introduce dependencias conflictivas.
* **Impacto:** **Eliminé** la persistencia CSV y refactoricé `personaDAO.java` para manejar la lectura y escritura del archivo **`datosContactos.json`** usando `toJson` y `fromJson`.

### 2.3 Funcionalidad de Importación (Punto 3.b)

Implementé la funcionalidad de importación, demostrando el manejo de archivos externos:

* Añadí el botón **`btn_importar`** a la interfaz.
* En `logica_ventana.java`, codifiqué el método `importarContactosJSON()` para usar **`JFileChooser`** y leer un JSON externo, y luego **fusionar** esos contactos con la lista actual (`contactos.addAll()`).

## 3. 🔒 Gestión de Dependencias y Despliegue (Punto 4)

Me enfoqué en la calidad del *build* y la distribución del proyecto.

### 3.1 Auditoría y Estabilidad

* **Versiones Estables:** Centralicé todas las versiones de las librerías (`flatlaf.version`, `gson.version`) en el bloque `<properties>` de Maven, asegurando la trazabilidad.
* **Dependencias Transitivas:** Confirmé que **no fue necesario** incluir el bloque `<exclusions>` porque FlatLaf y Gson tienen una huella limpia, lo que simplifica mi `pom.xml`.

### 3.2 Despliegue y Distribución

Para asegurar la ejecución en cualquier entorno, configuré el **Maven Shade Plugin**.

* Este *plugin* genera un **JAR monolítico (uber-JAR)** que incluye mi código compilado junto con las librerías FlatLaf y Gson.
* Esto garantiza que el proyecto es totalmente portátil y ejecutable en cualquier máquina con Java 21 (o superior) sin requerir una instalación manual de dependencias.

---

## 4. 💻 Instrucciones para Clonar y Ejecutar el Proyecto

Para evaluar mi proyecto, estos son los pasos para ponerlo en funcionamiento:

1.  **Clonación:** Abrir la terminal y usar `git clone https://github.com/Alisson-27/POO_Unidad4.git`.
2.  **Apertura en IDE:** Abrir IntelliJ IDEA y seleccionar **"Open"** o **"Import Project"**, apuntando al archivo **`pom.xml`** clonado.
3.  **Sincronización:** El IDE descargará automáticamente FlatLaf y Gson (dependencias).
4.  **Ejecución:** Ejecutar la clase principal **`org.example.vista.ventana`**.
