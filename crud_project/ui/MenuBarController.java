package crud_project.ui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.logging.Logger;

import static crud_project.ui.CustomerController.EXIT_CONFIRMATION_MESSAGE;
import static crud_project.ui.CustomerController.EXIT_CONFIRMATION_TITLE;

/**
 * The MenuBarController class manages the behavior of the menu bar in a JavaFX application.
 * It handles user actions associated with menu items and provides functionality such as
 * closing the application, showing an "About" window, logging out, and displaying a help page.
 */
public class MenuBarController {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private Stage userStage;

    /**
     * MenuItem contains a button to close the application
     */
    @FXML
    public MenuItem fxMenuClose;
    /**
     * MenuItem contains a button to show the about page of the application
     */
    @FXML
    public MenuItem fxMenuAbout;
    /**
     * MenuItem contains a button to sign out of the application. It sends you to the login window
     */
    @FXML
    public MenuItem fxMenuSignOut;
    /**
     * MenuItem contains a button to show the help page of the application
     */
    @FXML
    public MenuItem fxMenuContent;

    /**
     * Initializes the menu-related functionality and sets up event handling for the menu items.
     *
     * @param stage the primary stage for this application, used to manage application windows
     */
    public void init(Stage stage) {
        this.userStage = stage;
        fxMenuClose.setOnAction(e -> System.exit(0));
        fxMenuSignOut.setOnAction(this::handleOnExitAction);
        fxMenuAbout.setOnAction(this::handleAboutWindow);
        fxMenuContent.setOnAction(this::handleWindowShowing);


    }

    /**
     * Handles the action performed when the exit button is clicked. This method displays
     * a confirmation dialog to the user, and if the user confirms, it closes the application
     * window. If an error occurs during the operation, an alert is shown to notify the user.
     *
     * @param event the event object associated with the exit button action
     */
    public void handleOnExitAction(Event event) {
        try {
            LOGGER.info("Clicked exit button");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, EXIT_CONFIRMATION_MESSAGE,
                    ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle(EXIT_CONFIRMATION_TITLE);

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    userStage.close();
                }
            });

            event.consume();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            handleAlertError("Fail to Close try again");
        }
    }

    /**
     * Handles the showing of the help window triggered by an event.
     * This method creates a new stage displaying an embedded HTML file
     * (help.html), which contains help documentation for the application.
     * If the help file cannot be found, it displays an error alert to notify the user.
     *
     * @param event the event that triggers the help window to be displayed
     */
    private void handleWindowShowing(Event event) {
        WebView webView = new WebView();

        WebEngine webEngine = webView.getEngine();

        try {
            String url = getClass().getResource("/crud_project/ui/help.html").toExternalForm();
            webEngine.load(url);

            //Crear ventana para mostrar la help al igual que el about
            StackPane root = new StackPane(webView);
            Scene scene = new Scene(root, 800, 600);
            Stage helpStage = new Stage();

            helpStage.setResizable(false);
            helpStage.setTitle("System help");
            helpStage.setScene(scene);
            helpStage.show();


        } catch (Exception e) {
            LOGGER.severe("File help not found: " + e.getMessage());
            handleAlertError("File not found");
        }


    }

    /**
     * Shows a non-resizable "about" window with credits
     */
    private void handleAboutWindow(Event event) {

        StackPane root = new StackPane();
        Text contentText = new Text("This is a simple bank application made by group 3\n" +
                "made up for:\n" +
                "Aitor, Cynthia and Juan");

        // Centrar el alineamiento de las líneas de texto entre sí
        contentText.setTextAlignment(TextAlignment.CENTER);

        root.getChildren().add(contentText);

        Scene scene = new Scene(root, 400, 300);
        Stage stage = new Stage();
        stage.setTitle("About");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Displays an error alert dialog with the provided message.
     * The dialog contains a title, the error message, and an "OK" button.
     *
     * @param message the error message to display in the alert dialog
     */
    private void handleAlertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();

    }

}
