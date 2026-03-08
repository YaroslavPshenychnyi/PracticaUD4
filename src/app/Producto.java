package src.app;

import org.bson.Document;
import org.bson.types.ObjectId;
import src.serialization.ISerializable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Producto implements ISerializable {
    private ObjectId id;

    private String productName;

    private String developer;

    private String productType;

    private BigDecimal price;

    private Proveedor proveedor;

    private List<LineaPedido> lineas = new ArrayList<>();

    public Producto() {
    }

    public Producto(Document doc) {
        this.id = doc.getObjectId("_id");
        this.productName = doc.getString("productName");
        this.developer = doc.getString("developer");
        this.productType = doc.getString("productType");

        Object priceObj = doc.get("price");
        if (priceObj != null) {
            this.price = new BigDecimal(priceObj.toString());
        }

        ObjectId proveedorId = doc.getObjectId("proveedorId");
        if (proveedorId != null) {
            this.proveedor = new Proveedor();
            this.proveedor.setId(proveedorId);
        }

        List<ObjectId> lineaIds = (List<ObjectId>) doc.get("lineaIds");
        if (lineaIds != null) {
            for (ObjectId lineaId : lineaIds) {
                LineaPedido linea = new LineaPedido();
                linea.setId(lineaId);
                this.lineas.add(linea);
            }
        }
    }

    @Override
    public Document serialize() {
        Document doc = new Document();

        doc.append("_id", id);
        doc.append("productName", productName);
        doc.append("developer", developer);
        doc.append("productType", productType);
        doc.append("price", price);
        doc.append("proveedorId", proveedor != null ? proveedor.getId() : null);

        List<ObjectId> lineaIds = new ArrayList<>();
        for (LineaPedido linea : lineas) {
            if (linea != null && linea.getId() != null) {
                lineaIds.add(linea.getId());
            }
        }
        doc.append("lineaIds", lineaIds);

        return doc;
    }

    public List<LineaPedido> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaPedido> lineas) {
        this.lineas = lineas;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(productName, producto.productName)
                && Objects.equals(developer, producto.developer)
                && Objects.equals(productType, producto.productType)
                && Objects.equals(price, producto.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, developer, productType, price);
    }

    @Override
    public String toString() {
        return "Producto{" +
                "productName='" + productName + '\'' +
                ", developer='" + developer + '\'' +
                ", productType='" + productType + '\'' +
                ", price=" + price +
                ", proveedor=" + proveedor +
                '}';
    }
}