package src.gui.controllers;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import src.app.Producto;
import src.app.Proveedor;
import src.gui.Application;
import src.gui.Controller;
import src.gui.WrappedModel;
import src.gui.WrappedView;
import src.gui.forms.ProveedorForm;
import src.serialization.MongoSerializator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorController
        extends Controller<WrappedModel<Application>, WrappedView<ProveedorForm>> {

    private final DefaultListModel<Proveedor> proveedorModel = new DefaultListModel<>();
    private final DefaultListModel<Producto> productoModel = new DefaultListModel<>();

    public ProveedorController(Controller<?, ?> parent,
                               WrappedModel<Application> model) {
        super(parent, model, new WrappedView<>(new ProveedorForm()));
        initController();

        if (!getModel().get().isConnected()) return;
        loadProveedores();
    }

    public void loadProveedores() {
        proveedorModel.clear();

        MongoCollection<Document> collection =
                getModel().get().getProveedoresCollection();

        for (Document doc : collection.find()) {
            Proveedor proveedor = (Proveedor) MongoSerializator.deserialize(doc);
            proveedorModel.addElement(proveedor);
        }

        getView().get().getProveedorList().setModel(proveedorModel);

        productoModel.clear();
        getView().get().getProductosList().setModel(productoModel);
    }

    public void loadProductos(Proveedor proveedor) {
        productoModel.clear();
        if (proveedor == null) return;

        MongoCollection<Document> collection =
                getModel().get().getProductosCollection();

        List<Producto> productos = new ArrayList<>();
        for (Document doc : collection.find(new Document("proveedorId", proveedor.getId()))) {
            productos.add((Producto) MongoSerializator.deserialize(doc));
        }

        for (Producto p : productos) {
            productoModel.addElement(p);
        }

        getView().get().getProductosList().setModel(productoModel);
    }

    private void initController() {
        ProveedorForm form = getView().get();

        form.getProveedorList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Proveedor selected = (Proveedor) form.getProveedorList().getSelectedValue();
                if (selected != null) {
                    form.getNombre().setText(selected.getNombre());
                    form.getEmail().setText(selected.getEmail());
                    loadProductos(selected);
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

            Proveedor proveedor = new Proveedor();
            proveedor.setId(new ObjectId());
            proveedor.setNombre(nombre);
            proveedor.setEmail(email);

            Document doc = MongoSerializator.serialize(proveedor);

            getModel().get().getProveedoresCollection().insertOne(doc);

            loadProveedores();
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

            Proveedor selected = (Proveedor) form.getProveedorList().getSelectedValue();

            if (selected == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select proveedor",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            getModel().get()
                    .getProductosCollection()
                    .deleteMany(new Document("proveedorId", selected.getId()));

            getModel().get()
                    .getProveedoresCollection()
                    .deleteOne(new Document("_id", selected.getId()));

            loadProveedores();
            productoModel.clear();
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

            Proveedor selected = (Proveedor) form.getProveedorList().getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select proveedor to update",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            selected.setNombre(form.getNombre().getText().trim());
            selected.setEmail(form.getEmail().getText().trim());

            Document newDoc = MongoSerializator.serialize(selected);
            newDoc.remove("_id");

            getModel().get()
                    .getProveedoresCollection()
                    .updateOne(
                            new Document("_id", selected.getId()),
                            new Document("$set", newDoc)
                    );

            loadProveedores();
            clearForm();
            MainController parent = (MainController) getParent();
            parent.refreshTables();
        });
    }

    public void clearForm() {
        ProveedorForm form = getView().get();
        form.getNombre().setText("");
        form.getEmail().setText("");
        form.getProveedorList().clearSelection();
        form.getProductosList().clearSelection();
    }

    public void clearAll() {
        clearForm();
        proveedorModel.clear();
        productoModel.clear();
    }
}
