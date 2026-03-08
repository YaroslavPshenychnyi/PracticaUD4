package src.app;

import org.bson.Document;
import org.bson.types.ObjectId;
import src.serialization.ISerializable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Cliente implements ISerializable {
    private ObjectId id;

    private String nombre;

    private String email;

    private String tipo;

    private Boolean activo;

    private Set<Pedido> pedidos = new LinkedHashSet<>();

    public Cliente() {
    }

    public Cliente(Document doc) {
        this.id = doc.getObjectId("_id");
        this.nombre = doc.getString("nombre");
        this.email = doc.getString("email");
        this.tipo = doc.getString("tipo");
        this.activo = doc.getBoolean("activo");

        List<ObjectId> pedidoIds = (List<ObjectId>) doc.get("pedidoIds");
        if (pedidoIds != null) {
            for (ObjectId pedidoId : pedidoIds) {
                Pedido pedido = new Pedido();
                pedido.setId(pedidoId);
                this.pedidos.add(pedido);
            }
        }
    }

    @Override
    public Document serialize() {
        Document doc = new Document();

        doc.append("_id", id);
        doc.append("nombre", nombre);
        doc.append("email", email);
        doc.append("tipo", tipo);
        doc.append("activo", activo);

        List<ObjectId> pedidoIds = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            if (pedido != null && pedido.getId() != null) {
                pedidoIds.add(pedido.getId());
            }
        }
        doc.append("pedidoIds", pedidoIds);

        return doc;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Set<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Set<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(nombre, cliente.nombre)
                && Objects.equals(email, cliente.email)
                && Objects.equals(tipo, cliente.tipo)
                && Objects.equals(activo, cliente.activo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, email, tipo, activo);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", tipo='" + tipo + '\'' +
                ", activo=" + activo +
                '}';
    }
}