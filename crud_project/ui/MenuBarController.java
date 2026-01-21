package crud_project.ui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.logging.Logger;

import static crud_project.ui.CustomerController.EXIT_CONFIRMATION_MESSAGE;
import static crud_project.ui.CustomerController.EXIT_CONFIRMATION_TITLE;

public class MenuBarController {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private Stage userStage;

    @FXML
    public MenuItem fxMenuClose;
    @FXML
    public MenuItem fxMenuAbout;
    @FXML
    public MenuItem fxMenuSignOut;
    @FXML
    public MenuItem fxMenuContent;


    public void init(Stage stage) {
        this.userStage = stage;
        fxMenuClose.setOnAction(e -> System.exit(0));
        fxMenuSignOut.setOnAction(this::handleOnExitAction);
        fxMenuAbout.setOnAction(this::handleAboutWindow);
        fxMenuContent.setOnAction(this::handleAboutWindowv2);


    }

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
            handleAlertError("Fail to Close");
        }
    }

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

    private void handleAboutWindowv2(Event event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Cynthor Bank Application");
        alert.setContentText("This is a simple bank application made by group 3\n" +
                "Members: Aitor, Cynthia and Juan");
        alert.showAndWait();
    }


    private void handleAlertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();

    }

}
