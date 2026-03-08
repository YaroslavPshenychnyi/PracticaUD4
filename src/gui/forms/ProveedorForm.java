package src.gui.forms;

import javax.swing.*;
import java.awt.*;

public class ProveedorForm {
    private JPanel mainPanel;
    private JTextField nombre;
    private JTextField email;
    private JButton createButton;
    private JButton deleteButton;
    private JList proveedorList;
    private JList productosList;
    private JButton updateButton;


    public ProveedorForm() {
        proveedorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTextField getNombre() {
        return nombre;
    }

    public JTextField getEmail() {
        return email;
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JList getProveedorList() {
        return proveedorList;
    }

    public JList getProductosList() {
        return productosList;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }
}

