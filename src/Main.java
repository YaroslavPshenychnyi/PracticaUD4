package src;

import src.app.*;
import src.gui.controllers.MainController;
import src.serialization.MongoSerializator;

public class Main {
    public static void main(String[] args) {
        MongoSerializator.registrateClass(Cliente.class);
        MongoSerializator.registrateClass(LineaPedido.class);
        MongoSerializator.registrateClass(Pedido.class);
        MongoSerializator.registrateClass(Producto.class);
        MongoSerializator.registrateClass(Proveedor.class);
        MainController mainController = new MainController();
        mainController.initController();
    }
}