package src.gui.forms;

import javax.swing.*;

public class ClientForm {
    private JPanel mainPanel;
    private JTextField nombre;
    private JTextField email;
    private JButton createButton;
    private JButton deleteButton;
    private JCheckBox activo;
    private JComboBox tipo;
    private JButton updateButton;
    private JList listClientes;

    public ClientForm() {
        listClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

    public JList getListClientes() {
        return listClientes;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JCheckBox getActivo() {
        return activo;
    }

    public JComboBox getTipo() {
        return tipo;
    }
}
