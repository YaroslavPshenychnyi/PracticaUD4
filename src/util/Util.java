package src.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Util {
    public static void mensajeError(String mensaje) {
        JOptionPane.showMessageDialog(null,
                mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static int mensajeConfirmacion(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(null, mensaje
                , titulo, JOptionPane.YES_NO_OPTION);
    }

    public static JFileChooser crearSelectorFichero() {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos XML", "xml");
        fc.setFileFilter(filtro);
        return new JFileChooser();
    }
}
