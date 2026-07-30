package com.quantum.browser.ui;

import javafx.embed.swing.SwingNode;
import javafx.scene.layout.StackPane;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLifeSpanHandlerAdapter;

import javax.swing.*;
import java.awt.*;

public class BrowserView extends StackPane {

    private CefBrowser browser;
    private CefClient client;
    private String pendingUrl = null;
    private boolean browserReady = false;

    public BrowserView(CefApp cefApp) {
        SwingNode swingNode = new SwingNode();

        SwingUtilities.invokeLater(() -> {
            client = cefApp.createClient();

            // Esperar o evento nativo antes de carregar qualquer URL
            client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
                @Override
                public void onAfterCreated(CefBrowser b) {
                    browserReady = true;
                    if (pendingUrl != null) {
                        b.loadURL(pendingUrl);
                        pendingUrl = null;
                    }
                }

                @Override
                public void onBeforeClose(CefBrowser b) {
                    browserReady = false;
                }
            });

            browser = client.createBrowser("about:blank", false, false);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(browser.getUIComponent(), BorderLayout.CENTER);
            swingNode.setContent(panel);

            // Carregar a URL inicial via pending
            loadURL("https://google.com");
        });

        getChildren().add(swingNode);
    }

    public void loadURL(String url) {
        if (browserReady && browser != null) {
            browser.loadURL(url);
        } else {
            pendingUrl = url;
        }
    }

    public void goBack()    { if (browserReady && browser != null) browser.goBack(); }
    public void goForward() { if (browserReady && browser != null) browser.goForward(); }
    public void reload()    { if (browserReady && browser != null) browser.reload(); }
    public String getURL()  { return browser != null ? browser.getURL() : ""; }
}