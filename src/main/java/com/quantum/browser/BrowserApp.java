package com.quantum.browser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;

// IMPORTS REAIS DO SEU JAR CEFFX (Removido o NativeExtractor)
import com.techsenger.ceffx.core.CefApp;
import com.techsenger.ceffx.core.CefClient;
import com.techsenger.ceffx.core.CefSettings;
import com.techsenger.ceffx.core.browser.CefBrowser;
import com.techsenger.ceffx.core.browser.CefRendererD3D;

public class BrowserApp extends Application {

    private static CefApp cefAppInstance;
    private CefClient client;
    private CefBrowser browser;

    public static void main(String[] args) {
        try {
            // A. Localiza a pasta "chromium" de forma absoluta no Windows
            String baseDir = new File(".").getAbsolutePath();
            if (baseDir.endsWith(".")) {
                baseDir = baseDir.substring(0, baseDir.length() - 1);
            }
            String chromiumPath = baseDir + "chromium";

            // B. Garante que a propriedade do java.library.path aponte para as DLLs nativas
            System.setProperty("java.library.path", chromiumPath);

            // C. INICIALIZAÇÃO INICIAL DO CHROMIUM (Obrigatório vir antes do JavaFX!)
            // Como você já extraiu as DLLs manualmente na pasta, o startup vai ler direto
            CefApp.startup(args);

            // D. Cria as configurações idênticas do criador
            CefSettings settings = new CefSettings();
            settings.windowless_rendering_enabled = true; // Ativa nosso ImageView OSR
            settings.multi_threaded_message_loop = true;
            settings.external_message_pump = false;
            settings.command_line_args_disabled = false;

            // E. Instancia o CefApp global na memória antes do launch
            cefAppInstance = CefApp.getInstance(settings);

            // F. Dispara a interface gráfica do JavaFX
            Application.launch(BrowserApp.class, args);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. O CefApp já foi pré-inicializado no main, criamos o cliente na FX Thread
            CefApp.runLater(() -> {
                try {
                    client = cefAppInstance.createClient();

                    // 2. ATIVAÇÃO DO MOTOR D3D CUSTOMIZADO E ULTRA-RÁPIDO QUE COMPILAMOS
                    client.setRendererFactory(() -> new CefRendererD3D());

                    // 3. CRIAÇÃO DO BROWSER: 'true' no segundo parâmetro para amarrar o fix do ImageView
                    browser = client.createBrowser("https://youtube.com", true, false);

                    // 4. MONTAGEM DO LAYOUT NO JAVAFX
                    javafx.application.Platform.runLater(() -> {
                        StackPane root = new StackPane();
                        javafx.scene.layout.Pane browserPane = browser.getPane();
                        root.getChildren().add(browserPane);

                        Scene scene = new Scene(root, 1024, 768);

                        // Mantém a proporção geométrica sincronizada no resize
                        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> browserPane.setPrefWidth(newVal.doubleValue()));
                        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> browserPane.setPrefHeight(newVal.doubleValue()));

                        primaryStage.setTitle("QuantumBrowser");
                        primaryStage.setScene(scene);
                        primaryStage.setOnCloseRequest(e -> {
                            // Evita o fechamento padrão do JavaFX para dar tempo ao Chromium de limpar a memória
                            e.consume();

                            // Dispara o encerramento limpo na thread correta
                            fecharAplicacaoSeguro(primaryStage);
                        });

                        primaryStage.show();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fecharAplicacaoSeguro(Stage stage) {
    // 1. Esconde a janela imediatamente para dar feedback visual ao usuário
    if (stage != null) {
        stage.hide();
    }

    // 2. Executa o descarte do CefApp de forma assíncrona dentro da Thread do CEF
    CefApp.runLater(() -> {
        try {
            if (cefAppInstance != null) {
                cefAppInstance.dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 3. O golpe de misericórdia: Após desligar as DLLs, mata o processo no Windows
            Platform.runLater(() -> {
                Platform.exit();
                System.exit(0); // Garante que o Maven e o processo sumam do gerenciador
            });
        }
    });
}

    @Override
    public void stop() throws Exception {
        // Fallback caso o fechamento não passe pelo setOnCloseRequest
        if (cefAppInstance != null) {
            fecharAplicacaoSeguro(null);
        }
        super.stop();
    }
}
