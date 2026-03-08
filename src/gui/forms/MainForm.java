package src.gui.forms;

import javax.swing.*;

public class MainForm {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel productsPanel;
    private JPanel clientsPanel;
    private JPanel suppliersPanel;
    private JPanel suppliersContainer;
    private JPanel clientsContainer;
    private JPanel settingsPanel;
    private JPanel settingsContainer;
    private JPanel productsContainer;
    private JPanel pedidosContainer;
    private JPanel searchContainer;

    private JFrame window;

    public MainForm() {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.add(mainPanel);
        window.setSize(1200, 600);
    }

    public JPanel getSuppliersContainer() {
        return suppliersContainer;
    }

    public JPanel getPedidosContainer() {
        return pedidosContainer;
    }

    public JPanel getClientsContainer() {
        return clientsContainer;
    }

    public JFrame getWindow() {
        return window;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JPanel getSearchContainer() {
        return searchContainer;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JPanel getProductsContainer() {
        return productsContainer;
    }

    public JPanel getClientsPanel() {
        return clientsPanel;
    }

    public JPanel getSuppliersPanel() {
        return suppliersPanel;
    }

    public JPanel getSettingsContainer() {
        return settingsContainer;
    }
}
