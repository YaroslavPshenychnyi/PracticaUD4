package src.app;

import org.bson.Document;
import org.bson.types.ObjectId;
import src.serialization.ISerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido implements ISerializable {
    private ObjectId id;
    private java.time.LocalDate fecha;
    private Cliente cliente;
    private List<LineaPedido> lineas = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Document doc) {
        this.id = doc.getObjectId("_id");

        String fechaStr = doc.getString("fecha");
        if (fechaStr != null) {
            this.fecha = java.time.LocalDate.parse(fechaStr);
        }

        ObjectId clienteId = doc.getObjectId("clienteId");
        if (clienteId != null) {
            this.cliente = new Cliente();
            this.cliente.setId(clienteId);
        }

        List<Document> lineasDocs = (List<Document>) doc.get("lineas");
        if (lineasDocs != null) {
            for (Document lineaDoc : lineasDocs) {
                LineaPedido lp = new LineaPedido(lineaDoc);
                lp.setPedido(this);
                this.lineas.add(lp);
            }
        }
    }

    @Override
    public Document serialize() {
        Document doc = new Document();

        doc.append("_id", id);
        doc.append("fecha", fecha != null ? fecha.toString() : null);
        doc.append("clienteId", cliente != null ? cliente.getId() : null);

        List<Document> lineasDocs = new ArrayList<>();
        for (LineaPedido lp : lineas) {
            if (lp != null) {
                lineasDocs.add(lp.serialize());
            }
        }

        doc.append("lineas", lineasDocs);

        return doc;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public java.time.LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(java.time.LocalDate fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaPedido> lineas) {
        this.lineas = lineas;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(fecha, pedido.fecha) &&
                Objects.equals(cliente, pedido.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha, cliente);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "fecha=" + fecha +
                ", cliente=" + cliente +
                '}';
    }
}