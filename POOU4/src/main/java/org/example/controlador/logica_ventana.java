package org.example.controlador;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.example.vista.ventana;
import org.example.modelo.*;

/**
 * Controlador principal de la aplicación de gestión de contactos.
 * Se encarga de manejar los eventos de la interfaz (botones, tabla, checkboxes, etc.)
 * Implementa la lógica de CRUD y la importación de JSON.
 */
public class logica_ventana implements ActionListener, ListSelectionListener, ItemListener {

    private ventana delegado;           // Referencia a la vista
    private List<persona> contactos;    // Lista de contactos cargados
    private personaDAO dao;             // Instancia del DAO (maneja JSON)
    private String nombres, email, telefono, categoria = "";
    private boolean favorito = false;

    public logica_ventana(ventana delegado) {
        this.delegado = delegado;
        this.dao = new personaDAO(); // Inicializa el DAO basado en JSON

        // Cargar los contactos existentes al iniciar
        cargarContactosRegistrados();

        // Asignar listeners a los componentes
        this.delegado.btn_add.addActionListener(this);
        this.delegado.btn_eliminar.addActionListener(this);
        this.delegado.btn_modificar.addActionListener(this);
        this.delegado.btn_importar.addActionListener(this); // 🆕 Listener para el botón de Importar
        this.delegado.tablaContactos.getSelectionModel().addListSelectionListener(this);
        this.delegado.cmb_categoria.addItemListener(this);
        this.delegado.chb_favorito.addItemListener(this);
    }

    /**
     * Inicializa los campos de texto con la información ingresada por el usuario.
     */
    private void inicializarCampos() {
        nombres = delegado.txt_nombres.getText().trim();
        email = delegado.txt_email.getText().trim();
        telefono = delegado.txt_telefono.getText().trim();

        // Verifica que la categoría no sea null antes de llamar toString()
        Object selectedCategory = delegado.cmb_categoria.getSelectedItem();
        if (selectedCategory != null) {
            categoria = selectedCategory.toString();
        } else {
            categoria = "";
        }
    }

    /**
     * Verifica que los campos obligatorios no estén vacíos.
     * Nota: "Elija una Categoria" debe estar en el ResourceBundle si se usa este literal.
     */
    private boolean camposLlenos() {
        // Se asume que el primer elemento del combo es el placeholder
        return !nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty() && delegado.cmb_categoria.getSelectedIndex() > 0;
    }

    /**
     * Carga los contactos guardados en el archivo JSON y los muestra en la tabla.
     */
    private void cargarContactosRegistrados() {
        contactos = dao.leerArchivo();
        if (contactos == null) {
            contactos = new ArrayList<>();
        }
        actualizarTabla(contactos);
    }

    /**
     * Actualiza la tabla de contactos en pantalla con la lista actual.
     */
    private void actualizarTabla(List<persona> lista) {
        DefaultTableModel modelo = delegado.modeloTabla;
        modelo.setRowCount(0); // limpiar tabla

        for (persona p : lista) {
            Object[] fila = {
                    p.getNombre(),
                    p.getTelefono(),
                    p.getEmail(),
                    p.getCategoria(),
                    p.isFavorito() ? "Sí" : "No"
            };
            modelo.addRow(fila);
        }
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarCampos() {
        delegado.txt_nombres.setText("");
        delegado.txt_email.setText("");
        delegado.txt_telefono.setText("");
        delegado.cmb_categoria.setSelectedIndex(0);
        delegado.chb_favorito.setSelected(false);
        delegado.tablaContactos.clearSelection();
    }

    /**
     * Manejo de eventos de botones.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object fuente = e.getSource();

        if (fuente == delegado.btn_add) {
            agregarContacto();
        } else if (fuente == delegado.btn_eliminar) {
            eliminarContacto();
        } else if (fuente == delegado.btn_modificar) {
            modificarContacto();
        } else if (fuente == delegado.btn_importar) { // 🆕 Nuevo: Importación JSON
            importarContactosJSON();
        }
    }

    /**
     * Agrega un nuevo contacto al archivo JSON y la tabla.
     */
    private void agregarContacto() {
        inicializarCampos();

        if (!camposLlenos()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos obligatorios.");
            return;
        }

        persona nueva = new persona(nombres, telefono, email, categoria, favorito);

        // Añadir a la lista en memoria
        contactos.add(nueva);

        // Guardar la lista completa al archivo JSON
        dao.guardarContactos(contactos);

        actualizarTabla(contactos);
        limpiarCampos();
        JOptionPane.showMessageDialog(null, "Contacto agregado correctamente.");
    }

    /**
     * Elimina el contacto seleccionado de la lista y el archivo JSON.
     */
    private void eliminarContacto() {
        int fila = delegado.tablaContactos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un contacto para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "¿Desea eliminar el contacto seleccionado?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        contactos.remove(fila);

        // Actualizar el archivo JSON
        dao.guardarContactos(contactos);

        actualizarTabla(contactos);
        limpiarCampos();
        JOptionPane.showMessageDialog(null, "Contacto eliminado.");
    }

    /**
     * Modifica los datos del contacto seleccionado.
     */
    private void modificarContacto() {
        int fila = delegado.tablaContactos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione un contacto para modificar.");
            return;
        }

        inicializarCampos();
        if (!camposLlenos()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos obligatorios.");
            return;
        }

        // Actualizar el objeto persona en la lista en memoria
        persona p = contactos.get(fila);
        p.setNombre(nombres);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setCategoria(categoria);
        p.setFavorito(favorito);

        // Reescribir la lista completa al archivo JSON
        dao.guardarContactos(contactos);

        actualizarTabla(contactos);
        limpiarCampos();
        JOptionPane.showMessageDialog(null, "Contacto modificado correctamente.");
    }

    /**
     * 🆕 Permite al usuario seleccionar un archivo JSON externo e importa los contactos.
     */
    private void importarContactosJSON() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo JSON para importar");

        // Filtro para solo mostrar archivos .json
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON", "json"));

        int resultado = fileChooser.showOpenDialog(delegado);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            try {
                // 1. Leer los contactos del archivo JSON externo
                List<persona> contactosImportados = dao.importarContactos(archivoSeleccionado);

                // 2. Combinar con los contactos actuales y remover duplicados (se simplifica con addAll)
                contactos.addAll(contactosImportados);

                // 3. Guardar la lista combinada al archivo JSON principal
                dao.guardarContactos(contactos);

                // 4. Actualizar la interfaz
                actualizarTabla(contactos);

                JOptionPane.showMessageDialog(delegado,
                        contactosImportados.size() + " contactos importados correctamente.",
                        "Importación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(delegado,
                        "Error al leer el archivo JSON: " + e.getMessage(),
                        "Error de Lectura", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(delegado,
                        "Error durante la deserialización JSON. Verifique el formato del archivo.",
                        "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * Cuando el usuario selecciona una fila en la tabla, se muestran los datos en los campos.
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Asegura que el evento final de la selección es procesado
        if (e.getValueIsAdjusting()) return;

        int fila = delegado.tablaContactos.getSelectedRow();
        if (fila != -1 && fila < contactos.size()) {
            persona p = contactos.get(fila);
            delegado.txt_nombres.setText(p.getNombre());
            delegado.txt_telefono.setText(p.getTelefono());
            delegado.txt_email.setText(p.getEmail());
            // Intenta seleccionar el ítem, si no existe en el combo, se queda en el primero
            delegado.cmb_categoria.setSelectedItem(p.getCategoria());
            delegado.chb_favorito.setSelected(p.isFavorito());
        }
    }

    /**
     * Cuando cambia el valor del JComboBox o el CheckBox.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        Object fuente = e.getSource();

        if (fuente == delegado.cmb_categoria && e.getStateChange() == ItemEvent.SELECTED) {
            categoria = delegado.cmb_categoria.getSelectedItem().toString();
        } else if (fuente == delegado.chb_favorito) {
            favorito = delegado.chb_favorito.isSelected();
        }
    }
}