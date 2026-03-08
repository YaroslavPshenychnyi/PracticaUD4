package src.gui.controllers;

import org.bson.Document;
import src.app.Cliente;
import src.app.LineaPedido;
import src.app.Pedido;
import src.app.Producto;
import src.app.Proveedor;
import src.gui.Application;
import src.gui.Controller;
import src.gui.SearchView;
import src.gui.WrappedModel;
import src.serialization.MongoSerializator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchController
        extends Controller<WrappedModel<Application>, SearchView> {

    public SearchController(Controller<?, ?> parent,
                            WrappedModel<Application> model) {
        super(parent, model, new SearchView());
        initController();
    }

    private void initController() {
        SearchView view = getView();

        view.getSearchButton().addActionListener(e -> performSearch());

        view.getSearchField().addActionListener(e -> performSearch());

        view.getResultList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SearchView.SearchResultItem selected = view.getResultList().getSelectedValue();
                if (selected == null) {
                    view.setDetailsText("");
                    return;
                }

                view.setDetailsText(buildDetails(selected));
            }
        });
    }

    private void performSearch() {
        SearchView view = getView();

        if (!getModel().get().isConnected()) {
            JOptionPane.showMessageDialog(view.getMainPanel(),
                    "Connect to database first",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = view.getSearchField().getText();
        if (query == null) query = "";
        query = query.trim().toLowerCase(Locale.ROOT);

        view.clearResults();

        List<SearchView.SearchResultItem> results = new ArrayList<>();

        searchClientes(query, results);
        searchProductos(query, results);
        searchProveedores(query, results);
        searchPedidos(query, results);

        DefaultListModel<SearchView.SearchResultItem> model = view.getResultListModel();
        for (SearchView.SearchResultItem item : results) {
            model.addElement(item);
        }

        if (model.isEmpty()) {
            view.setDetailsText("No se encontraron resultados.");
        }
    }

    private void searchClientes(String query, List<SearchView.SearchResultItem> results) {
        for (Document doc : getModel().get().getClientesCollection().find()) {
            Cliente cliente = (Cliente) MongoSerializator.deserialize(doc);

            String text = safe(cliente.getNombre()) + " "
                    + safe(cliente.getEmail()) + " "
                    + safe(cliente.getTipo());

            if (matches(query, text)) {
                results.add(new SearchView.SearchResultItem("Cliente", cliente));
            }
        }
    }

    private void searchProductos(String query, List<SearchView.SearchResultItem> results) {
        for (Document doc : getModel().get().getProductosCollection().find()) {
            Producto producto = (Producto) MongoSerializator.deserialize(doc);

            String text = safe(producto.getProductName()) + " "
                    + safe(producto.getDeveloper()) + " "
                    + safe(producto.getProductType()) + " "
                    + safe(producto.getPrice());

            if (matches(query, text)) {
                results.add(new SearchView.SearchResultItem("Producto", producto));
            }
        }
    }

    private void searchProveedores(String query, List<SearchView.SearchResultItem> results) {
        for (Document doc : getModel().get().getProveedoresCollection().find()) {
            Proveedor proveedor = (Proveedor) MongoSerializator.deserialize(doc);

            String text = safe(proveedor.getNombre()) + " "
                    + safe(proveedor.getEmail());

            if (matches(query, text)) {
                results.add(new SearchView.SearchResultItem("Proveedor", proveedor));
            }
        }
    }

    private void searchPedidos(String query, List<SearchView.SearchResultItem> results) {
        for (Document doc : getModel().get().getPedidosCollection().find()) {
            Pedido pedido = (Pedido) MongoSerializator.deserialize(doc);

            StringBuilder text = new StringBuilder();
            text.append(safe(pedido.getFecha())).append(" ");

            if (pedido.getCliente() != null) {
                text.append(safe(pedido.getCliente().getId())).append(" ");
            }

            if (pedido.getLineas() != null) {
                for (LineaPedido lp : pedido.getLineas()) {
                    if (lp.getProducto() != null) {
                        text.append(safe(lp.getProducto().getId())).append(" ");
                    }
                    text.append(safe(lp.getCantidad())).append(" ");
                    text.append(safe(lp.getPrecio())).append(" ");
                }
            }

            if (matches(query, text.toString())) {
                results.add(new SearchView.SearchResultItem("Pedido", pedido));
            }
        }
    }

    private boolean matches(String query, String text) {
        if (query == null || query.isBlank()) {
            return true;
        }
        return text.toLowerCase(Locale.ROOT).contains(query);
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String buildDetails(SearchView.SearchResultItem item) {
        Object value = item.getValue();

        if (value instanceof Cliente) {
            return buildClienteDetails((Cliente) value);
        }

        if (value instanceof Producto) {
            return buildProductoDetails((Producto) value);
        }

        if (value instanceof Proveedor) {
            return buildProveedorDetails((Proveedor) value);
        }

        if (value instanceof Pedido) {
            return buildPedidoDetails((Pedido) value);
        }

        return String.valueOf(value);
    }

    private String buildClienteDetails(Cliente cliente) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CLIENTE ===\n");
        sb.append("ID: ").append(cliente.getId()).append("\n");
        sb.append("Nombre: ").append(cliente.getNombre()).append("\n");
        sb.append("Email: ").append(cliente.getEmail()).append("\n");
        sb.append("Tipo: ").append(cliente.getTipo()).append("\n");
        sb.append("Activo: ").append(cliente.getActivo()).append("\n");
        return sb.toString();
    }

    private String buildProductoDetails(Producto producto) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PRODUCTO ===\n");
        sb.append("ID: ").append(producto.getId()).append("\n");
        sb.append("Nombre: ").append(producto.getProductName()).append("\n");
        sb.append("Developer: ").append(producto.getDeveloper()).append("\n");
        sb.append("Tipo: ").append(producto.getProductType()).append("\n");
        sb.append("Precio: ").append(producto.getPrice()).append("\n");

        if (producto.getProveedor() != null) {
            sb.append("Proveedor ID: ").append(producto.getProveedor().getId()).append("\n");
        }

        return sb.toString();
    }

    private String buildProveedorDetails(Proveedor proveedor) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PROVEEDOR ===\n");
        sb.append("ID: ").append(proveedor.getId()).append("\n");
        sb.append("Nombre: ").append(proveedor.getNombre()).append("\n");
        sb.append("Email: ").append(proveedor.getEmail()).append("\n");
        return sb.toString();
    }

    private String buildPedidoDetails(Pedido pedido) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PEDIDO ===\n");
        sb.append("ID: ").append(pedido.getId()).append("\n");
        sb.append("Fecha: ").append(pedido.getFecha()).append("\n");

        if (pedido.getCliente() != null) {
            sb.append("Cliente ID: ").append(pedido.getCliente().getId()).append("\n");
        } else {
            sb.append("Cliente ID: null\n");
        }

        sb.append("\nLINEAS:\n");

        if (pedido.getLineas() == null || pedido.getLineas().isEmpty()) {
            sb.append("Sin líneas\n");
        } else {
            int i = 1;
            for (LineaPedido lp : pedido.getLineas()) {
                sb.append("Línea ").append(i++).append("\n");
                sb.append("  ID: ").append(lp.getId()).append("\n");
                sb.append("  Producto ID: ")
                        .append(lp.getProducto() != null ? lp.getProducto().getId() : null)
                        .append("\n");
                sb.append("  Cantidad: ").append(lp.getCantidad()).append("\n");
                sb.append("  Precio: ").append(lp.getPrecio()).append("\n");
            }
        }

        return sb.toString();
    }

    public void clearAll() {
        getView().clearResults();
        getView().getSearchField().setText("");
    }
}
