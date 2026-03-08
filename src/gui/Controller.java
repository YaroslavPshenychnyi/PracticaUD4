package src.gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public abstract class Controller<M extends Model, V extends View> {
    private M model;
    private V view;

    private Controller<?, ?> parent;
    private final List<Controller<?, ?>> childs;

    public Controller(Controller<?,?> parent, M model, V view) {
        this.model = model;
        this.view = view;
        this.parent = parent;
        this.childs = new ArrayList<>();
    }

    public void addChild(Controller<?, ?> child) {
        childs.add(child);
        child.setParent(this);
    }

    public void removeChild(Controller<?, ?> child) {
        childs.remove(child);
        child.setParent(null);
    }

    public Controller<?, ?> getParent() {
        return parent;
    }

    protected void setParent(Controller<?, ?> parent) {
        this.parent = parent;
    }

    public List<Controller<?, ?>> getChilds() {
        return childs;
    }

    protected void setChilds(List<Controller<?, ?>> childs) {
        this.childs.clear();
        this.childs.addAll(childs);
    }

    protected void setModel(M model) {
        this.model = model;
    }

    protected void setView(V view) {
        this.view = view;
    }

    public M getModel() {
        return model;
    }

    public V getView() {
        return view;
    }
}
