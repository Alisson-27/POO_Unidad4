package org.example.modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la gestión de contactos.
 * Implementa persistencia usando serialización y deserialización JSON con Gson,
 * almacenando los contactos en el archivo 'datosContactos.json'.
 */
public class personaDAO {

    private final File directorio;
    private final File archivo;
    private static final String NOMBRE_ARCHIVO = "datosContactos.json";
    private final Gson gson;

    public personaDAO() {
        // Inicializa Gson con formato de salida legible (Pretty Printing)
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        // Define el directorio de guardado en la carpeta del usuario
        this.directorio = new File(System.getProperty("user.home") + "/gestionContactos");
        this.archivo = new File(directorio, NOMBRE_ARCHIVO);

        // Asegura que el directorio y el archivo JSON existan
        prepararArchivo();
    }

    // Método para crear el directorio y archivo JSON si no existen
    private void prepararArchivo() {
        if (!directorio.exists()) {
            directorio.mkdir();
        }

        if (!archivo.exists() || archivo.length() == 0) {
            try {
                // Si el archivo no existe o está vacío, lo inicializa con una lista JSON vacía
                archivo.createNewFile();
                guardarContactos(new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Escribe la lista completa de contactos al archivo JSON (Serialización).
     * Este método se usa para añadir, modificar o eliminar contactos, reescribiendo todo.
     * * @param personas La lista de contactos a guardar.
     */
    public void guardarContactos(List<persona> personas) {
        try (FileWriter writer = new FileWriter(archivo)) {
            // Convierte la lista de Java a un String JSON y lo escribe en el archivo
            gson.toJson(personas, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lee todos los contactos desde el archivo JSON (Deserialización).
     * * @return Una lista de objetos persona.
     */
    public List<persona> leerArchivo() {
        if (!archivo.exists() || archivo.length() == 0) {
            // Si el archivo está vacío o no existe, devuelve una lista vacía
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(archivo)) {
            // Define el tipo de dato que Gson debe esperar al deserializar (List<persona>)
            Type tipoLista = new TypeToken<List<persona>>() {}.getType();

            // Lee el JSON del archivo y lo convierte a la lista
            List<persona> personas = gson.fromJson(reader, tipoLista);
            return personas != null ? personas : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lee contactos desde un archivo JSON externo (para la importación).
     * * @param archivoExterno El archivo JSON desde donde importar.
     * @return Una lista de objetos persona importados.
     */
    public List<persona> importarContactos(File archivoExterno) throws IOException {
        try (FileReader reader = new FileReader(archivoExterno)) {
            // Define el tipo de dato esperado
            Type tipoLista = new TypeToken<List<persona>>() {}.getType();

            // Deserializa el contenido del archivo externo
            List<persona> personasImportadas = gson.fromJson(reader, tipoLista);
            return personasImportadas != null ? personasImportadas : new ArrayList<>();
        }
    }
}