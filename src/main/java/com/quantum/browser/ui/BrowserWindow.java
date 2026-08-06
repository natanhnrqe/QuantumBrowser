package com.quantum.browser.ui;


import com.techsenger.ceffx.core.CefApp;
import com.techsenger.ceffx.core.CefClient;
import com.techsenger.ceffx.core.browser.CefBrowser;
import com.techsenger.ceffx.core.browser.CefRendererD3D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static com.quantum.browser.BrowserConfig.HOME_PAGE;

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
        cefBrowser = cefClient.createBrowser(HOME_PAGE, true, false);

        browserPane = cefBrowser.getPane();

        ToolBar toolBar = new ToolBar();
        toolBar.setOnBack(() -> cefBrowser.goBack());
        toolBar.setOnForward(() -> cefBrowser.goForward());
        toolBar.setOnReload(() -> cefBrowser.reload());
        toolBar.setOnHome(() -> cefBrowser.loadURL(HOME_PAGE));
        toolBar.setOnNavigate( url -> cefBrowser.loadURL(url));
        toolBar.setOnFavorite(() -> System.out.println("favorito: " + cefBrowser.getURL()));


        VBox topArea = new VBox(toolBar);
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
