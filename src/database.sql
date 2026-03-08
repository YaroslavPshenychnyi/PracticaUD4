CREATE DATABASE IF NOT EXISTS software_db;
USE software_db;

CREATE TABLE IF NOT EXISTS proveedor
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email  VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS cliente
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email  VARCHAR(100),
    tipo   ENUM ('EMPRESA','INDIVIDUAL') DEFAULT 'INDIVIDUAL',
    activo BOOLEAN                       DEFAULT TRUE
);


CREATE TABLE IF NOT EXISTS producto
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    developer    VARCHAR(100),
    product_type ENUM ('IDE','FRAMEWORK','DEVOPS','GENERIC') DEFAULT 'GENERIC',
    price        DECIMAL(10, 2),
    proveedor_id INT,
    CONSTRAINT fk_producto_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor (id)
);

CREATE TABLE IF NOT EXISTS pedido
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    fecha      DATE NOT NULL,
    cliente_id INT  NOT NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id)
);

CREATE TABLE IF NOT EXISTS linea_pedido
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id   INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad    INT DEFAULT 1,
    precio      DECIMAL(10, 2),
    CONSTRAINT fk_lp_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_lp_producto FOREIGN KEY (producto_id) REFERENCES producto (id)
);