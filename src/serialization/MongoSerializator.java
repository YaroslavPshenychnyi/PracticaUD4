package src.serialization;

import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MongoSerializator {
    private static final HashMap<Class<?>, IConstructorRef> registratedClasses = new HashMap<>();

    public static void registrateClass(Class<?> clazz) {
        boolean isValid = isSerializable(clazz) && hasConstructorFromDocClass(clazz);

        if (!isValid) {
            throw new IllegalArgumentException(
                    "La clase " + clazz.getName() +
                            " no implementa ISerializable o no tiene constructor con Document"
            );
        }

        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(Document.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "La clase " + clazz.getName() + " no tiene constructor(Document)"
            );
        }

        registratedClasses.put(clazz, new IConstructorRef() {
            @Override
            public Object construct(Document doc) {
                try {
                    return constructor.newInstance(doc);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error al construir objeto de tipo " + clazz.getName(),
                            e
                    );
                }
            }
        });
    }

    private static boolean isSerializable(Class<?> clazz) {
        return ISerializable.class.isAssignableFrom(clazz)
                && !clazz.isInterface()
                && !Modifier.isAbstract(clazz.getModifiers());
    }

    private static boolean hasConstructorFromDocClass(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor(Document.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static Object deserialize(Document doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document no puede ser null");
        }

        String className = doc.getString("_class");

        if (className == null) {
            throw new IllegalStateException("Document no contiene información de clase (_class)");
        }

        try {
            Class<?> clazz = Class.forName(className);

            IConstructorRef constructorRef = registratedClasses.get(clazz);

            if (constructorRef == null) {
                throw new IllegalStateException(
                        "La clase " + className + " no está registrada en MongoSerializator"
                );
            }

            return constructorRef.construct(doc);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Clase no encontrada: " + className, e);
        }
    }

    public static Document serialize(Object obj) {

        if (!(obj instanceof ISerializable)) {
            throw new IllegalArgumentException(
                    "La clase " + obj.getClass().getName() + " no implementa ISerializable"
            );
        }

        Document doc = ((ISerializable) obj).serialize();

        doc.append("_class", obj.getClass().getName());

        return doc;
    }

    public static boolean isRegistrated(Class<?> clazz) {
        return registratedClasses.containsKey(clazz);
    }

    public static Map<Class<?>, IConstructorRef> getRegistratedClasses() {
        return new HashMap<>(registratedClasses);
    }
}
