package src.app;

import org.bson.Document;
import org.bson.types.ObjectId;
import src.serialization.ISerializable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Proveedor implements ISerializable {
    private ObjectId id;

    private String nombre;

    private String email;

    private Set<Producto> productos = new LinkedHashSet<>();

    public Proveedor() {
    }

    public Proveedor(Document doc) {
        this.id = doc.getObjectId("_id");
        this.nombre = doc.getString("nombre");
        this.email = doc.getString("email");

        List<ObjectId> productoIds = (List<ObjectId>) doc.get("productoIds");
        if (productoIds != null) {
            for (ObjectId productoId : productoIds) {
                Producto producto = new Producto();
                producto.setId(productoId);
                this.productos.add(producto);
            }
        }
    }

    @Override
    public Document serialize() {
        Document doc = new Document();

        doc.append("_id", id);
        doc.append("nombre", nombre);
        doc.append("email", email);

        List<ObjectId> productoIds = new ArrayList<>();
        for (Producto producto : productos) {
            if (producto != null && producto.getId() != null) {
                productoIds.add(producto.getId());
            }
        }
        doc.append("productoIds", productoIds);

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

    public Set<Producto> getProductos() {
        return productos;
    }

    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Proveedor proveedor = (Proveedor) o;
        return Objects.equals(nombre, proveedor.nombre)
                && Objects.equals(email, proveedor.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, email);
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}