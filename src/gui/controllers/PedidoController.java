package src.gui.controllers;

import org.bson.Document;
import org.bson.types.ObjectId;
import src.app.Cliente;
import src.app.LineaPedido;
import src.app.Pedido;
import src.app.Producto;
import src.gui.Application;
import src.gui.Controller;
import src.gui.PedidoView;
import src.gui.WrappedModel;
import src.serialization.MongoSerializator;

import javax.swing.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PedidoController
        extends Controller<WrappedModel<Application>, PedidoView> {

    public PedidoController(Controller<?, ?> parent,
                            WrappedModel<Application> model) {

        super(parent, model, new PedidoView());

        initController();

        if (!getModel().get().isConnected()) {
            return;
        }

        loadInitialData();
    }

    public void loadInitialData() {
        loadClientes();
        loadProductos();
        loadPedidos();
    }

    private void loadClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (Document doc : getModel().get().getClientesCollection().find()) {
            clientes.add((Cliente) MongoSerializator.deserialize(doc));
        }
        getView().setClientes(clientes);
    }

    private void loadProductos() {
        List<Producto> productos = new ArrayList<>();
        for (Document doc : getModel().get().getProductosCollection().find()) {
            productos.add((Producto) MongoSerializator.deserialize(doc));
        }
        getView().setProductos(productos);
    }

    public void loadPedidos() {
        List<Pedido> pedidos = new ArrayList<>();

        for (Document doc : getModel().get().getPedidosCollection().find()) {
            Pedido pedido = (Pedido) MongoSerializator.deserialize(doc);

            if (pedido.getLineas() != null) {
                for (LineaPedido lp : pedido.getLineas()) {
                    if (lp.getProducto() != null && lp.getProducto().getId() != null) {
                        Document prodDoc = getModel().get()
                                .getProductosCollection()
                                .find(new Document("_id", lp.getProducto().getId()))
                                .first();

                        if (prodDoc != null) {
                            lp.setProducto((Producto) MongoSerializator.deserialize(prodDoc));
                        }
                    }
                }
            }

            pedidos.add(pedido);
        }

        getView().setPedidos(pedidos);
    }

    private void initController() {
        final PedidoView view = getView();

        view.getCreateButton().addActionListener(e -> createPedido());
        view.getUpdateButton().addActionListener(e -> updatePedido());
        view.getDeleteButton().addActionListener(e -> deletePedido());

        view.getAddLineButton().addActionListener(e -> {
            if (!getModel().get().isConnected()) {
                JOptionPane.showMessageDialog(getView().getMainPanel(),
                        "Connect to database first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ((PedidoView.LineaTableModel) view.getLineasTable().getModel())
                    .addLine(null, 1, null);
        });

        view.getRemoveLineButton().addActionListener(e -> {
            if (!getModel().get().isConnected()) {
                JOptionPane.showMessageDialog(getView().getMainPanel(),
                        "Connect to database first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int row = view.getLineasTable().getSelectedRow();
            if (row >= 0) {
                ((PedidoView.LineaTableModel) view.getLineasTable().getModel())
                        .removeLine(row);
            }
        });

        view.getPedidoList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Pedido selected = view.getPedidoList().getSelectedValue();
                if (selected == null) {
                    clearForm();
                    return;
                }
                fillForm(selected);
            }
        });
    }

    private void createPedido() {
        if (!getModel().get().isConnected()) {
            JOptionPane.showMessageDialog(getView().getMainPanel(),
                    "Connect to database first",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        final PedidoView view = getView();

        if (view.getClienteBox().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Seleccione un cliente", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setId(new ObjectId());

        LocalDate selectedDate = view.getFechaPicker().getDate();
        if (selectedDate != null) {
            pedido.setFecha(selectedDate);
        }

        pedido.setCliente((Cliente) view.getClienteBox().getSelectedItem());

        List<LineaPedido> lineas = extractLineasFromTable(pedido);
        pedido.setLineas(lineas);

        Document doc = MongoSerializator.serialize(pedido);

        getModel().get().getPedidosCollection().insertOne(doc);

        loadPedidos();
        clearForm();
    }

    private void updatePedido() {
        if (!getModel().get().isConnected()) {
            JOptionPane.showMessageDialog(getView().getMainPanel(),
                    "Connect to database first",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        final PedidoView view = getView();
        final Pedido selected = view.getPedidoList().getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Seleccione un pedido para actualizar", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate selectedDate = view.getFechaPicker().getDate();
        if (selectedDate != null) {
            selected.setFecha(selectedDate);
        }

        selected.setCliente((Cliente) view.getClienteBox().getSelectedItem());
        selected.setLineas(extractLineasFromTable(selected));

        Document newDoc = MongoSerializator.serialize(selected);
        newDoc.remove("_id");

        getModel().get()
                .getPedidosCollection()
                .updateOne(
                        new Document("_id", selected.getId()),
                        new Document("$set", newDoc)
                );

        loadPedidos();
    }

    private void deletePedido() {
        if (!getModel().get().isConnected()) {
            JOptionPane.showMessageDialog(getView().getMainPanel(),
                    "Connect to database first",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        final PedidoView view = getView();
        final Pedido selected = view.getPedidoList().getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Seleccione un pedido para eliminar", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view.getMainPanel(),
                "¿Eliminar pedido seleccionado?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        getModel().get()
                .getPedidosCollection()
                .deleteOne(new Document("_id", selected.getId()));

        loadPedidos();
        clearForm();
    }

    private List<LineaPedido> extractLineasFromTable(Pedido pedido) {
        PedidoView.LineaTableModel model =
                (PedidoView.LineaTableModel) getView().getLineasTable().getModel();

        List<LineaPedido> result = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            Object prodObj = model.getValueAt(i, 0);
            Producto producto = prodObj instanceof Producto ? (Producto) prodObj : null;

            Integer cantidad;
            Object cantObj = model.getValueAt(i, 1);
            try {
                cantidad = cantObj instanceof Integer
                        ? (Integer) cantObj
                        : Integer.parseInt(String.valueOf(cantObj));
            } catch (Exception ignored) {
                cantidad = 1;
            }

            BigDecimal precio;
            Object precioObj = model.getValueAt(i, 2);
            try {
                precio = precioObj instanceof BigDecimal
                        ? (BigDecimal) precioObj
                        : new BigDecimal(String.valueOf(precioObj));
            } catch (Exception ignored) {
                precio = BigDecimal.ZERO;
            }

            if (producto == null) continue;

            LineaPedido lp = new LineaPedido();
            lp.setId(new ObjectId());
            lp.setPedido(pedido);
            lp.setProducto(producto);
            lp.setCantidad(cantidad);
            lp.setPrecio(precio);

            result.add(lp);
        }

        return result;
    }

    public void clearForm() {
        PedidoView view = getView();
        view.getFechaPicker().clear();
        view.getClienteBox().setSelectedIndex(-1);
        ((PedidoView.LineaTableModel) view.getLineasTable().getModel()).clear();
    }

    private void fillForm(Pedido pedido) {
        PedidoView view = getView();

        if (pedido.getFecha() != null) {
            view.getFechaPicker().setDate(pedido.getFecha());
        } else {
            view.getFechaPicker().clear();
        }

        Cliente clientePedido = pedido.getCliente();
        JComboBox<Cliente> box = view.getClienteBox();

        if (clientePedido != null) {
            for (int i = 0; i < box.getItemCount(); i++) {
                Cliente c = box.getItemAt(i);
                if (c.getId() != null && c.getId().equals(clientePedido.getId())) {
                    box.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            box.setSelectedIndex(-1);
        }

        ((PedidoView.LineaTableModel) view.getLineasTable().getModel())
                .setLines(new ArrayList<>(pedido.getLineas()));
    }

    public void clearAll() {
        clearForm();
        getView().getClienteBox().removeAllItems();
        getView().lineaTableModel.clear();
        ((DefaultListModel<Pedido>) getView().getPedidoList().getModel()).clear();
    }
}
