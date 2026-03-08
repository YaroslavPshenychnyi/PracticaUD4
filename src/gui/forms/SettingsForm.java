package src.gui.forms;

import javax.swing.*;

public class SettingsForm {
    private JPanel mainPanel;
    private JLabel connectionStatusLabel;
    private JButton connectButton;
    private JButton disconnectButton;

    public SettingsForm() {

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JLabel getConnectionStatusLabel() {
        return connectionStatusLabel;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public JButton getDisconnectButton() {
        return disconnectButton;
    }
}
