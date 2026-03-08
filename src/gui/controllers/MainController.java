package src.gui.controllers;

import src.util.Util;
import src.gui.*;
import src.gui.forms.MainForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

public class MainController extends Controller<WrappedModel<Application>, WrappedView<MainForm>> implements WindowListener {
    private ClientController cc;
    private ProductoController pc;
    private SettingsController scp;
    private ProveedorController prc;
    private PedidoController pedidoController;

    public MainController(){
        super(null, new WrappedModel<Application>(new Application()),
                new WrappedView<MainForm>(new MainForm()));
        this.getView().get().getWindow().addWindowListener(this);
    }

    public void initController() {
        MainForm view = getView().get();

        cc = new ClientController(this, getModel());
        pc = new ProductoController(this, getModel());
        scp = new SettingsController(this, getModel());
        prc = new ProveedorController(this, getModel());
        pedidoController = new PedidoController(this, getModel());

        view.getSuppliersContainer().add(prc.getView().get().getMainPanel(), BorderLayout.CENTER);
        view.getClientsContainer().add(cc.getView().get().getMainPanel(), BorderLayout.CENTER);
        view.getProductsContainer().add(pc.getView().get().getMainPanel(), BorderLayout.CENTER);
        view.getSettingsContainer().add(scp.getView().get().getMainPanel(), BorderLayout.CENTER);
        view.getPedidosContainer().add(pedidoController.getView().getMainPanel(), BorderLayout.CENTER);

        addChild(cc);
        addChild(pc);
        addChild(scp);
        addChild(prc);
        addChild(pedidoController);

        view.getWindow().setVisible(true);
    }

    public void refreshTables() {
        cc.loadClientes();
        pc.loadProductos();
        pc.loadProveedores();
        prc.loadProveedores();
        pedidoController.loadInitialData();
    }

    public void clearAll(){
        cc.clearAll();
        pc.clearAll();
        prc.clearAll();
        pedidoController.clearAll();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        int resp = Util.mensajeConfirmacion("¿Desea cerrar la ventana?", "Salir");
        if (resp == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
