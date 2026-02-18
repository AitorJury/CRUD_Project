package crud_project.ui.controller;

import javafx.event.ActionEvent;
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


/**
 * La clase {@code MenuBarController} gestiona el comportamiento de la barra de
 * menú en una aplicación JavaFX.
 * <p>
 * Se encarga de manejar las acciones del usuario asociadas a los distintos
 * elementos del menú, como cerrar la aplicación, mostrar la ventana "Acerca
 * de", cerrar sesión y mostrar la página de ayuda.
 */
public class MenuBarController extends BaseController{

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private Stage userStage;

    /**
     * Elemento de menú que permite cerrar la aplicación.
     */
    @FXML
    public MenuItem fxMenuClose;

    /**
     * Elemento de menú que muestra la ventana "Acerca de" de la aplicación.
     */
    @FXML
    public MenuItem fxMenuAbout;

    /**
     * Elemento de menú que permite cerrar sesión y volver a la ventana de
     * inicio de sesión.
     */
    @FXML
    public MenuItem fxMenuSignOut;

    /**
     * Elemento de menú que muestra la página de ayuda de la aplicación.
     */
    @FXML
    public MenuItem fxMenuContent;
    @FXML
    public MenuItem fxCreateCustomer;
    @FXML
    public MenuItem fxDeleteCustomer;
    @FXML
    public MenuItem fxUpdateCustomer;


    /**
     * Inicializa la funcionalidad de la barra de menú y configura los
     * manejadores de eventos para cada elemento del menú.
     *
     * @param stage escenario principal de la aplicación, utilizado para
     *              gestionar las ventanas de la aplicación
     */
    public void init(Stage stage) {
        this.userStage = stage;
        fxMenuClose.setOnAction(e -> System.exit(0));
        fxMenuSignOut.setOnAction(this::handleExit);
        fxMenuAbout.setOnAction(this::handleAboutWindow);

    }

    /**
     * Muestra una ventana "Acerca de" no redimensionable con información y
     * créditos de la aplicación.
     *
     * @param event evento que desencadena la apertura de la ventana
     */
    private void handleAboutWindow(Event event) {

        StackPane root = new StackPane();
        Text contentText = new Text("This is a simple bank application made by group 3\n" +
                "made up for:\n" +
                "Aitor Jury, Cynthia Medina & Juan Ismael Caiza");


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


    @Override
    protected void loadData() {
        // Se queda vacio, ya que no se usa
    }

    /**
     * Lógica de creación de entidad según la ventana activa.
     *
     * @param event evento de acción
     */
    @Override
    protected void handleCreate(ActionEvent event) {

    }

    /**
     * Lógica de actualización de entidad según la ventana activa.
     *
     * @param event evento de acción
     */
    @Override
    protected void handleUpdate(ActionEvent event) {

    }

    /**
     * Lógica de borrado de entidad según la ventana activa.
     *
     * @param event evento de acción
     */
    @Override
    protected void handleDelete(ActionEvent event) {

    }

    /**
     * Devuelve la ruta al recurso HTML de ayuda específico de cada ventana.
     * Ejemplo: "/crud_project/ui/res/helpAccount.html"
     */
    @Override
    protected String getHelpResourcePath() {
        return "";
    }

    /**
     * Devuelve el título de la ventana de ayuda específico de cada ventana.
     */
    @Override
    protected String getHelpWindowTitle() {
        return "";
    }
}
