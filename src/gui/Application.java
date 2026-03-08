package src.gui;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Application {
    private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String DB_NAME = "software_db";

    private MongoClient client;
    private MongoDatabase database;
    private ConnectionState state;

    public Application() {
        this.state = ConnectionState.DISCONNECTED;
    }

    public void connect() {
        if (state == ConnectionState.CONNECTED) {
            return;
        }

        try {
            client = new MongoClient(HOST, PORT);
            database = client.getDatabase(DB_NAME);

            // простая проверка, что соединение реально работает
            database.listCollectionNames().first();

            state = ConnectionState.CONNECTED;
        } catch (Exception e) {
            state = ConnectionState.ERROR;
            throw new RuntimeException("No se pudo conectar a MongoDB", e);
        }
    }

    public void disconnect() {
        if (client != null) {
            client.close();
            client = null;
        }

        database = null;
        state = ConnectionState.DISCONNECTED;
    }

    public boolean isConnected() {
        return state == ConnectionState.CONNECTED;
    }

    public ConnectionState getState() {
        return state;
    }

    public MongoDatabase getDatabase() {
        ensureConnected();
        return database;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        ensureConnected();
        return database.getCollection(collectionName);
    }

    public MongoCollection<Document> getClientesCollection() {
        return getCollection("clientes");
    }

    public MongoCollection<Document> getPedidosCollection() {
        return getCollection("pedidos");
    }

    public MongoCollection<Document> getProductosCollection() {
        return getCollection("productos");
    }

    public MongoCollection<Document> getProveedoresCollection() {
        return getCollection("proveedores");
    }

    public void insertDocument(String collectionName, Document document) {
        ensureConnected();
        getCollection(collectionName).insertOne(document);
    }

    public Document findById(String collectionName, Object id) {
        ensureConnected();
        return getCollection(collectionName)
                .find(new Document("_id", id))
                .first();
    }

    public void deleteById(String collectionName, Object id) {
        ensureConnected();
        getCollection(collectionName)
                .deleteOne(new Document("_id", id));
    }

    public void updateById(String collectionName, Object id, Document newValues) {
        ensureConnected();
        getCollection(collectionName)
                .updateOne(
                        new Document("_id", id),
                        new Document("$set", newValues)
                );
    }

    private void ensureConnected() {
        if (state != ConnectionState.CONNECTED || database == null) {
            throw new IllegalStateException("La aplicación no está conectada a MongoDB");
        }
    }

    public enum ConnectionState {
        CONNECTED,
        DISCONNECTED,
        ERROR
    }
}
