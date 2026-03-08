package src.gui;

public class WrappedView<T> implements View {
    private T wrappedView;

    public WrappedView(T wrappedView) {
        this.wrappedView = wrappedView;
    }

    public T get(){
        return wrappedView;
    }

    public void set(T wrappedView){
        this.wrappedView = wrappedView;
    }
}
