package src.gui.forms;

import src.app.Producto;
import src.app.Proveedor;

import javax.swing.*;
import java.awt.*;

public class ProductoForm {
    private JPanel mainPanel;
    private JTextField nameField;
    private JTextField developerField;
    private JComboBox typeBox;
    private JFormattedTextField priceField;
    private JComboBox<Proveedor> proveedorBox;
    private JButton createButton;
    private JButton deleteButton;
    private JList<Producto> list;
    private JButton updateButton;

    public ProductoForm() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JTextField getDeveloperField() {
        return developerField;
    }

    public JComboBox getTypeBox() {
        return typeBox;
    }

    public JFormattedTextField getPriceField() {
        return priceField;
    }

    public JComboBox<Proveedor> getProveedorBox() {
        return proveedorBox;
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JList<Producto> getList() {
        return list;
    }
}
