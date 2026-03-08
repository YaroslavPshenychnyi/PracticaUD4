package src.app;

import org.bson.Document;
import org.bson.types.ObjectId;
import src.serialization.ISerializable;

import java.math.BigDecimal;
import java.util.Objects;

public class LineaPedido implements ISerializable {
    private ObjectId id;
    private Pedido pedido;
    private Producto producto;
    private Integer cantidad;
    private BigDecimal precio;

    public LineaPedido() {
    }

    public LineaPedido(Document doc) {
        this.id = doc.getObjectId("_id");

        ObjectId productoId = doc.getObjectId("productoId");
        if (productoId != null) {
            this.producto = new Producto();
            this.producto.setId(productoId);
        }

        this.cantidad = doc.getInteger("cantidad");

        Object precioObj = doc.get("precio");
        if (precioObj != null) {
            this.precio = new BigDecimal(precioObj.toString());
        }
    }

    @Override
    public Document serialize() {
        Document doc = new Document();
        doc.append("_id", id);
        doc.append("productoId", producto != null ? producto.getId() : null);
        doc.append("cantidad", cantidad);
        doc.append("precio", precio);
        return doc;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LineaPedido that = (LineaPedido) o;
        return Objects.equals(producto, that.producto) &&
                Objects.equals(cantidad, that.cantidad) &&
                Objects.equals(precio, that.precio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, cantidad, precio);
    }

    @Override
    public String toString() {
        return "LineaPedido{" +
                "producto=" + producto +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                '}';
    }
}