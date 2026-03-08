package src.gui;

public class WrappedModel<T> implements Model {
    public T wrappedModel;

    public WrappedModel(T wrappedModel) {
        this.wrappedModel = wrappedModel;
    }

    public T get() {
        return wrappedModel;
    }

    public void set(T model) {
        this.wrappedModel = model;
    }
}
