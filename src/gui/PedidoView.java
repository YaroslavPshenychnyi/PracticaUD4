package src.gui;

import com.github.lgooddatepicker.components.DatePicker;
import src.app.Cliente;
import src.app.LineaPedido;
import src.app.Pedido;
import src.app.Producto;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PedidoView implements View {

    private JPanel mainPanel;

    private JList<Pedido> pedidoList;

    private DatePicker fechaPicker;
    private JComboBox<Cliente> clienteBox;

    private JTable lineasTable;
    public LineaTableModel lineaTableModel;

    private List<Producto> productos = new ArrayList<>();

    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton addLineButton;
    private JButton removeLineButton;

    public PedidoView() {
        initComponents();
    }

    private void initComponents() {

        mainPanel = new JPanel(new BorderLayout(8, 8));

        pedidoList = new JList<>(new DefaultListModel<>());
        pedidoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScroll = new JScrollPane(pedidoList);
        listScroll.setPreferredSize(new Dimension(250, 400));
        mainPanel.add(listScroll, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Fecha:"), gbc);

        gbc.gridx = 1;
        fechaPicker = new DatePicker();
        formPanel.add(fechaPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        clienteBox = new JComboBox<>();
        formPanel.add(clienteBox, gbc);

        rightPanel.add(formPanel, BorderLayout.NORTH);

        lineaTableModel = new LineaTableModel();
        lineasTable = new JTable(lineaTableModel);
        lineasTable.setFillsViewportHeight(true);
        lineasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configureTableEditors();

        JScrollPane tableScroll = new JScrollPane(lineasTable);
        tableScroll.setPreferredSize(new Dimension(400, 220));
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        createButton = new JButton("Create");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        addLineButton = new JButton("Add Line");
        removeLineButton = new JButton("Remove Line");

        buttonsPanel.add(createButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(Box.createHorizontalStrut(16));
        buttonsPanel.add(addLineButton);
        buttonsPanel.add(removeLineButton);

        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void configureTableEditors() {

        TableColumn prodCol = lineasTable.getColumnModel().getColumn(0);
        prodCol.setCellEditor(new DefaultCellEditor(new JComboBox<>()));

        TableColumn qtyCol = lineasTable.getColumnModel().getColumn(1);
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        qtyCol.setCellEditor(new SpinnerEditor(spinner));
    }


    public JPanel getMainPanel() { return mainPanel; }
    public JList<Pedido> getPedidoList() { return pedidoList; }
    public DatePicker getFechaPicker() { return fechaPicker; }
    public JComboBox<Cliente> getClienteBox() { return clienteBox; }
    public JTable getLineasTable() { return lineasTable; }
    public JButton getCreateButton() { return createButton; }
    public JButton getUpdateButton() { return updateButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getAddLineButton() { return addLineButton; }
    public JButton getRemoveLineButton() { return removeLineButton; }


    public void setClientes(List<Cliente> clientes) {
        DefaultComboBoxModel<Cliente> model = new DefaultComboBoxModel<>();
        for (Cliente c : clientes) model.addElement(c);
        clienteBox.setModel(model);
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;

        TableColumn prodCol = lineasTable.getColumnModel().getColumn(0);
        JComboBox<Producto> combo = new JComboBox<>();
        for (Producto p : productos) combo.addItem(p);
        prodCol.setCellEditor(new DefaultCellEditor(combo));
    }

    public void setPedidos(List<Pedido> pedidos) {
        DefaultListModel<Pedido> m = new DefaultListModel<>();
        for (Pedido p : pedidos) m.addElement(p);
        pedidoList.setModel(m);
    }


    public static class LineaTableModel extends AbstractTableModel {

        private final String[] columns = {"Producto", "Cantidad", "Precio"};
        private final List<LineaPedido> data = new ArrayList<>();

        public int getRowCount() { return data.size(); }
        public int getColumnCount() { return columns.length; }
        public String getColumnName(int column) { return columns[column]; }

        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Producto.class;
            if (columnIndex == 1) return Integer.class;
            if (columnIndex == 2) return BigDecimal.class;
            return Object.class;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            LineaPedido lp = data.get(rowIndex);

            switch (columnIndex) {
                case 0: return lp.getProducto();
                case 1: return lp.getCantidad();
                case 2: return lp.getPrecio();
                default: return null;
            }
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= data.size()) {
                return;
            }

            LineaPedido lp = data.get(rowIndex);

            switch (columnIndex) {

                case 0:
                    lp.setProducto((Producto) aValue);
                    break;

                case 1:
                    if (aValue instanceof Integer) {
                        lp.setCantidad((Integer) aValue);
                    } else {
                        try {
                            lp.setCantidad(Integer.parseInt(aValue.toString()));
                        } catch (Exception ignored) {}
                    }
                    break;

                case 2:
                    if (aValue instanceof BigDecimal) {
                        lp.setPrecio((BigDecimal) aValue);
                    } else {
                        try {
                            lp.setPrecio(new BigDecimal(aValue.toString()));
                        } catch (Exception ignored) {}
                    }
                    break;
            }

            fireTableRowsUpdated(rowIndex, rowIndex);
        }

        public void addLine(Producto producto, int cantidad, BigDecimal precio) {
            LineaPedido lp = new LineaPedido();
            lp.setProducto(producto);
            lp.setCantidad(cantidad);
            lp.setPrecio(precio);
            data.add(lp);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        }

        public void removeLine(int index) {
            data.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public void setLines(List<LineaPedido> lines) {
            data.clear();
            if (lines != null) {
                for (LineaPedido lp : lines) {
                    LineaPedido copy = new LineaPedido();
                    copy.setId(lp.getId());
                    copy.setProducto(lp.getProducto());
                    copy.setCantidad(lp.getCantidad());
                    copy.setPrecio(lp.getPrecio());
                    data.add(copy);
                }
            }
            fireTableDataChanged();
        }

        public void clear() {
            data.clear();
            fireTableDataChanged();
        }
    }

    private static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {

        private final JSpinner spinner;

        public SpinnerEditor(JSpinner spinner) {
            this.spinner = spinner;
        }

        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {

            if (value instanceof Integer) {
                spinner.setValue(value);
            } else {
                spinner.setValue(Integer.valueOf(1));
            }
            return spinner;
        }
    }
}