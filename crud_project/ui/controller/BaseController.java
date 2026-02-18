package crud_project.ui.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.logging.Logger;

public abstract class BaseController {

    protected static final Logger LOGGER = Logger.getLogger(BaseController.class.getName());

    protected Stage stage;

    protected abstract void loadData();

    /**
     * Lógica de creación de entidad según la ventana activa.
     *
     * @param event evento de acción
     */
    protected abstract void handleCreate(ActionEvent event);

    /**
     * Lógica de actualización de entidad según la ventana activa.
     *
     * @param event evento de acción
     */
    protected abstract void handleUpdate(ActionEvent event);

    /**
     * Lógica de borrado de entidad según la ventana activa.
     *
     * @param event evento de acción
     */
    protected abstract void handleDelete(ActionEvent event);

    /**
     * Devuelve la ruta al recurso HTML de ayuda específico de cada ventana.
     * Ejemplo: "/crud_project/ui/res/helpAccount.html"
     */
    protected abstract String getHelpResourcePath();

    /**
     * Devuelve el título de la ventana de ayuda específico de cada ventana.
     */
    protected abstract String getHelpWindowTitle();

    // ─────────────────────────────────────────
    // MÉTODOS CONCRETOS COMUNES: evitan duplicación
    // ─────────────────────────────────────────

    protected void showHelp() {
        try {

            WebView webView = new WebView();
            webView.getEngine().load(getClass().getResource(getHelpResourcePath()).toExternalForm());

            Stage helpStage = new Stage();
            helpStage.setTitle(getHelpWindowTitle());
            helpStage.setScene(new Scene(new StackPane(webView), 800, 600));
            helpStage.setResizable(false);
            helpStage.show();


        } catch (Exception e) {
            showError("The help file could not be loaded.");
        }
    }

    protected void handleExit(Event event) {
        try {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to exit?",
                    ButtonType.OK, ButtonType.CANCEL
            );
            alert.setTitle("Exit Confirmation");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    onExitConfirmed();
                }
            });
            event.consume();
        } catch (Exception e) {
            LOGGER.severe("Error during exit: " + e.getMessage());
            showError("Failed to close. Please try again.");
        }
    }

    protected void onExitConfirmed() {
        if (stage != null) stage.close();
    }

    /**
     * Muestra un Alert de error modal. Antes duplicado en CustomerController
     * y MenuBarController con código idéntico.
     */
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.showAndWait();
    }

    /**
     * Muestra un Alert de información modal.
     */
    protected void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Information");
        alert.showAndWait();
    }

    /**
     * Muestra una confirmación genérica y devuelve si el usuario dijo OK.
     */
    protected boolean showConfirmation(String message, String title) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION, message,
                ButtonType.OK, ButtonType.CANCEL
        );
        alert.setTitle(title);
        return alert.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .isPresent();
    }

    public Stage getStage() {
        return this.stage;
    }

}
