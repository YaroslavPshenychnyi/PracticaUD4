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
import src.gui.forms.ProductoForm;
import src.serialization.MongoSerializator;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoController
        extends Controller<WrappedModel<Application>, WrappedView<ProductoForm>> {

    private final DefaultListModel<Producto> listModel = new DefaultListModel<>();

    public ProductoController(Controller<?, ?> parent,
                              WrappedModel<Application> model) {
        super(parent, model, new WrappedView<>(new ProductoForm()));

        initController();

        if (!getModel().get().isConnected()) {
            return;
        }

        loadProveedores();
        loadProductos();
    }

    public void loadProveedores() {
        ProductoForm form = getView().get();
        form.getProveedorBox().removeAllItems();

        MongoCollection<Document> collection =
                getModel().get().getProveedoresCollection();

        for (Document doc : collection.find()) {
            Proveedor proveedor = (Proveedor) MongoSerializator.deserialize(doc);
            form.getProveedorBox().addItem(proveedor);
        }
    }

    public void loadProductos() {
        ProductoForm form = getView().get();
        listModel.clear();

        MongoCollection<Document> collection =
                getModel().get().getProductosCollection();

        List<Producto> productos = new ArrayList<>();
        for (Document doc : collection.find()) {
            productos.add((Producto) MongoSerializator.deserialize(doc));
        }

        for (Producto p : productos) {
            listModel.addElement(p);
        }

        form.getList().setModel(listModel);
    }

    public void initController() {
        ProductoForm form = getView().get();

        form.getCreateButton().addActionListener(e -> {
            if (!getModel().get().isConnected()) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Connect to database first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String name = form.getNameField().getText().trim();
            String developer = form.getDeveloperField().getText().trim();
            String type = form.getTypeBox().getSelectedItem().toString();

            BigDecimal price;
            try {
                price = new BigDecimal(form.getPriceField().getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Invalid price",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Proveedor proveedor = (Proveedor) form.getProveedorBox().getSelectedItem();

            if (proveedor == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select proveedor",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Producto producto = new Producto();
            producto.setId(new ObjectId());
            producto.setProductName(name);
            producto.setDeveloper(developer);
            producto.setProductType(type);
            producto.setPrice(price);
            producto.setProveedor(proveedor);

            Document doc = MongoSerializator.serialize(producto);

            getModel().get().getProductosCollection().insertOne(doc);

            loadProductos();
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

            Producto selected = form.getList().getSelectedValue();

            if (selected == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select product to delete",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            getModel().get()
                    .getProductosCollection()
                    .deleteOne(new Document("_id", selected.getId()));

            loadProductos();
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

            Producto selected = form.getList().getSelectedValue();

            if (selected == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select product to update",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal price;
            try {
                price = new BigDecimal(form.getPriceField().getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Invalid price",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Proveedor proveedor = (Proveedor) form.getProveedorBox().getSelectedItem();
            if (proveedor == null) {
                JOptionPane.showMessageDialog(form.getMainPanel(),
                        "Select proveedor",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            selected.setProductName(form.getNameField().getText().trim());
            selected.setDeveloper(form.getDeveloperField().getText().trim());
            selected.setProductType(form.getTypeBox().getSelectedItem().toString());
            selected.setPrice(price);
            selected.setProveedor(proveedor);

            Document newDoc = MongoSerializator.serialize(selected);
            newDoc.remove("_id");

            getModel().get()
                    .getProductosCollection()
                    .updateOne(
                            new Document("_id", selected.getId()),
                            new Document("$set", newDoc)
                    );

            loadProductos();
            clearForm();
            MainController parent = (MainController) getParent();
            parent.refreshTables();
        });

        form.getList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Producto selected = form.getList().getSelectedValue();
                if (selected != null) {
                    form.getNameField().setText(selected.getProductName());
                    form.getDeveloperField().setText(selected.getDeveloper());
                    form.getPriceField().setText(selected.getPrice() != null ? selected.getPrice().toString() : "");
                    form.getTypeBox().setSelectedItem(selected.getProductType());
                    form.getProveedorBox().setSelectedItem(selected.getProveedor());
                }
            }
        });
    }

    public void clearForm() {
        ProductoForm form = getView().get();
        form.getNameField().setText("");
        form.getDeveloperField().setText("");
        form.getPriceField().setText("");
        form.getTypeBox().setSelectedIndex(0);
        form.getProveedorBox().setSelectedIndex(-1);
    }

    public void clearAll() {
        clearForm();
        listModel.clear();
        getView().get().getProveedorBox().removeAllItems();
    }
}