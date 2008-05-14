package com.mkalugin.pikachu.core;

public class Controller implements ViewCallback {
    
    private final ICorchyWindow window;
    private final Document model;

    public Controller(Document model, ICorchyWindow window) {
        if (model == null)
            throw new NullPointerException("model is null");
        if (window == null)
            throw new NullPointerException("window is null");
        this.model = model;
        this.window = window;
        window.setCallback(this);
    }

    public void setText(String newText) {
        model.setContent(newText);
    }

    public void startSynchronization() {
    }

    public void run() {
        window.setText(model.getContent());
        window.openWindow();
    }
    
}
