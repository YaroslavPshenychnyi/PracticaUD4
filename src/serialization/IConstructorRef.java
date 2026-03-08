package src.serialization;

import org.bson.Document;

public interface IConstructorRef {
    public Object construct(Document doc);
}
