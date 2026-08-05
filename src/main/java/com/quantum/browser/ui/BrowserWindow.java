package com.quantum.browser.ui;


import com.techsenger.ceffx.core.CefApp;
import com.techsenger.ceffx.core.CefClient;
import com.techsenger.ceffx.core.browser.CefBrowser;
import com.techsenger.ceffx.core.browser.CefRendererD3D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BrowserWindow extends BorderPane {

    private final Stage stage;
    private final CefApp cefApp;
    private CefClient cefClient;
    private CefBrowser cefBrowser;
    private Pane browserPane;

    public BrowserWindow(Stage stage,CefApp cefApp) {
        this.cefApp = cefApp;
        this.stage = stage;
        inicializate();
    }

    private void inicializate() {
        cefClient = cefApp.createClient();
        cefClient.setRendererFactory(()-> new CefRendererD3D());
        cefBrowser = cefClient.createBrowser("https://google.com", true, false);

        browserPane = cefBrowser.getPane();

        VBox topArea = new VBox();
        setTop(topArea);

        setCenter(browserPane);

        stage.widthProperty().addListener((obs, oldVal, newVal) ->
            browserPane.setPrefWidth(newVal.doubleValue()));
        stage.heightProperty().addListener((obs, oldVal, newVal)->
            browserPane.setPrefHeight(newVal.doubleValue()));
    }

    public CefBrowser getCefBrowser() {
        return cefBrowser;
    }

    public CefClient getCefClient() {
        return cefClient;
    }
}
