package src.gui.controllers;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import src.app.Cliente;
import src.gui.Application;
import src.gui.Controller;
import src.gui.WrappedModel;
import src.gui.WrappedView;
import src.gui.forms.ClientForm;
import src.serialization.MongoSerializator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ClientController
        extends Controller<WrappedModel<Application>, WrappedView<ClientForm>> {

    private final DefaultListModel<Cliente> listModel = new DefaultListModel<>();

    public ClientController(Controller<?, ?> parent,
                            WrappedModel<Application> model) {
        super(parent, model, new WrappedView<>(new ClientForm()));
        initController();

        if (!getModel().get().isConnected()) return;

        loadClientes();
    }

    public void loadClientes() {
        ClientForm form = getView().get();
        listModel.clear();

        MongoCollection<Document> collection =
                getModel().get().getClientesCollection();

        List<Cliente> clientes = new ArrayList<>();
        for (Document doc : collection.find()) {
            clientes.add((Cliente) MongoSerializator.deserialize(doc));
        }

        for (Cliente c : clientes) {
            listModel.addElement(c);
        }

        form.getListClientes().setModel(listModel);
    }

    public void initController() {
        ClientForm form = getView().get();

        form.getListClientes().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Cliente selected = (Cliente) form.getListClientes().getSelectedValue();
                if (selected != null) {
                    form.getNombre().setText(selected.getNombre());
                    form.getEmail().setText(selected.getEmail());
                    form.getTipo().setSelectedItem(selected.getTipo());
                    form.getActivo().setSelected(Boolean.TRUE.equals(selected.getActivo()));
                }
            }
        });

        form.getCreateButton().addActionListener(e -> {
            if (!getModel().get().isConnected()) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Connect to database first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nombre = form.getNombre().getText().trim();
            String email = form.getEmail().getText().trim();
            String tipo = (String) form.getTipo().getSelectedItem();
            boolean activo = form.getActivo().isSelected();

            Cliente cliente = new Cliente();
            cliente.setId(new ObjectId());
            cliente.setNombre(nombre);
            cliente.setEmail(email);
            cliente.setTipo(tipo);
            cliente.setActivo(activo);

            Document doc = MongoSerializator.serialize(cliente);

            getModel().get().getClientesCollection().insertOne(doc);

            loadClientes();
            clearForm();
            MainController parent = (MainController) getParent();
            parent.refreshTables();
        });

        form.getDeleteButton().addActionListener(e -> {
            if (!getModel().get().isConnected()) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Connect to database first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente selected = (Cliente) form.getListClientes().getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select client to delete",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            getModel().get()
                    .getClientesCollection()
                    .deleteOne(new Document("_id", selected.getId()));

            loadClientes();
            clearForm();
            MainController parent = (MainController) getParent();
            parent.refreshTables();
        });

        form.getUpdateButton().addActionListener(e -> {
            if (!getModel().get().isConnected()) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Connect to database first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente selected = (Cliente) form.getListClientes().getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select client to update",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            selected.setNombre(form.getNombre().getText().trim());
            selected.setEmail(form.getEmail().getText().trim());
            selected.setTipo((String) form.getTipo().getSelectedItem());
            selected.setActivo(form.getActivo().isSelected());

            Document newDoc = MongoSerializator.serialize(selected);
            newDoc.remove("_id");

            getModel().get()
                    .getClientesCollection()
                    .updateOne(
                            new Document("_id", selected.getId()),
                            new Document("$set", newDoc)
                    );

            loadClientes();
            clearForm();
            MainController parent = (MainController) getParent();
            parent.refreshTables();
        });
    }

    public void clearForm() {
        ClientForm form = getView().get();
        form.getNombre().setText("");
        form.getEmail().setText("");
        form.getTipo().setSelectedIndex(0);
        form.getActivo().setSelected(false);
        form.getListClientes().clearSelection();
    }

    public void clearAll() {
        clearForm();
        listModel.clear();
    }
}