package src.serialization;

import org.bson.Document;

public interface ISerializable {
    public Document serialize();
}
