package com.quantum.browser.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.Consumer;

public class ToolBar extends HBox {

    private final Button btnBack;
    private final Button btnForward;
    private final Button btnReload;
    private final Button btnHome;
    private final TextField urlField;
    private final Button btnFavorite;

    private Runnable onBack;
    private Runnable onForward;
    private Runnable onReload;
    private Runnable onHome;
    private Consumer<String> onNavigate;
    private Runnable onFavorite;

    public ToolBar() {
        setSpacing(6);
        setPadding(new Insets(8, 12, 8, 12));
        setAlignment(Pos.CENTER_LEFT);


        btnBack    = new Button("←");
        btnForward = new Button("→");
        btnReload  = new Button("⟳");
        btnHome    = new Button("⌂");

        urlField = new TextField();
        urlField.setPromptText("Digite uma URL ou pesquise...");
        HBox.setHgrow(urlField, Priority.ALWAYS);

        btnFavorite = new Button("☆");

        getChildren().addAll(
        btnBack, btnForward, btnReload, btnHome,
        urlField,
        btnFavorite);

        bindAction();
    }

    private void bindAction(){
        btnBack.setOnAction(e -> {if (onBack != null) onBack.run();});
        btnForward.setOnAction(e -> {if (onForward != null) onForward.run();});
        btnReload.setOnAction(e -> {if (onReload != null) onReload.run();});
        btnHome.setOnAction(e -> {if (onHome != null) onHome.run();});
        btnFavorite.setOnAction(e -> {if (onFavorite != null) onFavorite.run();});

        urlField.setOnAction(e -> {
            if (onNavigate != null) {
                String url = formatUrl(urlField.getText().trim());
            }
        });
    }

    private String formatUrl(String input) {
        if (input.isEmpty()) return "";

        if (input.startsWith("http://") || input.startsWith("https://")) return input;

        if (input.contains(".") && !input.contains(" ")) return "https://" + input;

        return "https://www.google.com/search?q=" + input.replace(" ", "+");

    }

    public void setUrl(String url) {
        urlField.setText(url);
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnForward(Runnable onForward) {
        this.onForward = onForward;
    }

    public void setOnReload(Runnable onReload) {
        this.onReload = onReload;
    }

    public void setOnHome(Runnable onHome) {
        this.onHome = onHome;
    }

    public void setOnNavigate(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
    }

    public void setOnFavorite(Runnable onFavorite) {
        this.onFavorite = onFavorite;
    }
}
