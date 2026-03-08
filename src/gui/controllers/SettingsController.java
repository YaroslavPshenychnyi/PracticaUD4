package src.gui.controllers;

import javax.swing.*;
import java.awt.*;

import src.gui.Application;
import src.gui.Controller;
import src.gui.WrappedModel;
import src.gui.WrappedView;
import src.gui.forms.SettingsForm;

public class SettingsController extends Controller<WrappedModel<Application>, WrappedView<SettingsForm>> {

    public SettingsController(Controller<?, ?> parent, WrappedModel<Application> model) {
        super(parent, model, new WrappedView<>(new SettingsForm()));
        initController();
    }

    public void initController() {
        SettingsForm view = getView().get();

        updateStatusLabel(view);

        view.getConnectButton().addActionListener(e -> {
            try {
                Application app = getModel().get();
                app.connect();
                updateStatusLabel(view);

                JOptionPane.showMessageDialog(view.getMainPanel(),
                        "Connected to database",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);

                MainController parent = (MainController) getParent();
                parent.refreshTables();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view.getMainPanel(),
                        "Connection failed:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        view.getDisconnectButton().addActionListener(e -> {
            try {
                Application app = getModel().get();
                app.disconnect();
                updateStatusLabel(view);

                MainController parent = (MainController) getParent();
                parent.clearAll();

                JOptionPane.showMessageDialog(view.getMainPanel(),
                        "Disconnected from database",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view.getMainPanel(),
                        "Disconnect failed:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void updateStatusLabel(SettingsForm view) {
        boolean connected = getModel().get().isConnected();
        JLabel label = view.getConnectionStatusLabel();

        if (label == null) return;

        if (connected) {
            label.setText("Connected");
            label.setForeground(new Color(0, 128, 0));
        } else {
            label.setText("Disconnected");
            label.setForeground(Color.RED);
        }
    }
}