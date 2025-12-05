package ev2_crud_application.ui;

// Imports.
import ev2_crud_application.logic.CustomerRESTClient;
import ev2_crud_application.model.Customer;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;

/**
 *
 * @author Cynthia.
 */
public class SignInController {

    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;
    @FXML
    private Label lblError;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnSignIn;
    @FXML
    private Hyperlink linkSignUp;
    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private Stage stage;
    private final ButtonType ok = new ButtonType("OK");
    private final ButtonType yes = new ButtonType("Yes");
    private final ButtonType no = new ButtonType("No");

    /**
     * Inicializa la ventana Sign In y configura los controles.
     *
     * @param stage La ventana principal (Stage) donde se muestra la escena.
     * @param root La raíz (Parent) que contiene los elementos del FXML.
     */
    public void initStage(Stage stage, Parent root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);

        this.stage = stage;
        LOGGER.info("Initializing window");
        // Establecer el título de la ventana.
        stage.setTitle("Sign in");
        // La ventana no debe ser redimensaionable.
        stage.setResizable(false);
        // Asociar eventos a manejadores.
        btnExit.setOnAction(this::handleBtnExitOnAction);
        btnSignIn.setOnAction(this::handleBtnSignInOnAction);
        linkSignUp.setOnAction(this::handleLinkOnAction);

        // Asociar manejadores de propierties.
        txtEmail.focusedProperty().addListener(this::handeltxtEmailFocusChange);
        txtEmail.textProperty().addListener(this::handeltxtEmailTextChange);
        txtPassword.textProperty().addListener(this::handeltxtPasswordTextChange);

        stage.show();
        btnSignIn.setDisable(true);

        // Preparar el formulario de entrada con los campos de correo y contraseña vacíos.
        // La ventana es no modal.
        // Enfocar automáticamente el campo de correo al abrir la ventana.
    }

    /**
     * Manejador del mensaje de error.
     * 
     * @param message El mensaje a mostrar.
     */
    private void handleLabelError(String message) {
        lblError.setText(message);
    }

    /**
     * Manejador del mensaje del Alert.
     * 
     * @param message El mensaje a mostrar.
     */
    private void handleAlertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ok);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();

    }

    /**
     * Manejador del evento de acción en el hyperlink Sign Up.
     *
     * @param event El evento de acción generado por el link.
     */
    private void handleLinkOnAction(ActionEvent event) {
        try {
            // Cerrar la ventana actual.
            // Abrir la ventana de registro de nuevo usuario.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = loader.load();

            SignUpController controller = loader.getController();
            controller.init(this.stage, root);
            // Obtener el controlador correcto.
            LOGGER.info("Changing to Sign Up Window");

        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
        }
    }

    /**
     * Manejador del evento de acción en el botón Exit.
     *
     * @param event El evento de acción generado por el botón.
     */
    private void handleBtnExitOnAction(ActionEvent event) {
        try {
            // Mostrar alert modal de confirmación para salir de la aplicación.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Do you want to exit?", yes, no);
            alert.setTitle("Exit the application");
            alert.setHeaderText("Departure confirmation");
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, cerrar la aplicación.
                if (resp == yes) {
                    Stage stage = (Stage) btnExit.getScene().getWindow();
                    stage.close();
                }
                // Si no confirma, la ventana permanecerá abierta.
            });
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Manejador del evento de acción en el botón Sign In.
     *
     * @param event El evento de acción generado por el botón.
     */
    private void handleBtnSignInOnAction(ActionEvent event) {
        try {
            // Validar si el correo tiene formato del correo (@ y un dominio).
            String text = txtEmail.getText().trim();
            if (!text.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                // Si no coincide, se lanzará una excepción con el label de error 
                // y se mostrará un mensaje (“El correo o la contraseña no son correctos”)
                throw new Exception("The email must have @ an email and a domain");
            } else {
                handleLabelError("Checking in the database");
            }

            // Conectará con la base de datos para validar la contraseña y el correo.
            String email = txtEmail.getText().trim();
            String password = txtPassword.getText().trim();
            CustomerRESTClient client = new CustomerRESTClient();
            // Verificar que la contraseña coincida con la del usuario registrado. 
            // Verificar que el correo y la contraseña existe en la base de datos.
            Customer customer = client.findCustomerByEmailPassword_XML(
                    Customer.class, email, password);

            // Si todo es correcto se abrirá la página “Main” y se cerrará la actual.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            Parent root = loader.load();
            // Cargamos controlador.
            MainController controller = loader.getController();
            controller.initStage(this.stage, root);
            LOGGER.info("Changing to Main Window");

        } catch (NotAuthorizedException e) {
            LOGGER.warning(e.getMessage());
            // Si no coincide, se lanzará una excepción con el label de error y 
            // se mostrará un mensaje (“El correo o la contraseña no son correctos”).
            handleAlertError("The username or password does not match.");
        } catch (InternalServerErrorException e) {
            LOGGER.warning(e.getMessage());
            // Si el servidor no responde se lanzará una excepción con el label de error 
            // y se mostrará un mensaje.(“No se puede acceder al servidor”).
            handleAlertError("It cannot connect to the server.");
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            handleLabelError(e.getMessage());
        } finally {
            enableButtonSignIn();
        }
    }

    /**
     * Manejador del evento de cambio de texto en el campo Email.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del texto.
     * @param newValue El nuevo valor del texto.
     */
    private void handeltxtEmailTextChange(ObservableValue observable, 
            String oldValue, String newValue) {
        try {
            if (this.txtEmail.getText().isEmpty()) {
                txtEmail.setStyle(" -fx-border-color: red");
                throw new Exception("The email field must not be left empty.");
            } else {
                txtEmail.setStyle("-fx-border-color: white");
                lblError.setText("");

            }

        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            // Lanzar excepcion de error.
            handleLabelError(e.getMessage());
        } finally {
            // Si el campo del texto está rellenado se habilita el botón de entrar.
            enableButtonSignIn();
        }

    }

    /**
     * Manejador del evento de cambio de texto en el campo Password.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del texto.
     * @param newValue El nuevo valor del texto.
     */
    private void handeltxtPasswordTextChange(ObservableValue observable, 
            String oldValue, String newValue) {
        try {
            if (this.txtPassword.getText().isEmpty()) {
                txtPassword.setStyle(" -fx-border-color: red");
                throw new Exception("The password field must not be left empty.");
            } else {
                txtPassword.setStyle("-fx-border-color: white");
                lblError.setText("");
            }
            // Si el campo del texto está rellenado se habilita el botón de entrar.
            enableButtonSignIn();
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            // Lanzar excepcion de error.
            handleLabelError(e.getMessage());
        } finally {
            // Si el campo del texto está rellenado se habilita el botón de entrar.
            enableButtonSignIn();
        }
    }

    /**
     * Manejador del cambio de foco del campo Email.
     * 
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handeltxtEmailFocusChange(ObservableValue observable, 
            Boolean oldValue, Boolean newValue) {
        if (oldValue && !newValue && !txtEmail.getText().isEmpty()) {
            lblError.setText("");
        }
    }

    /**
     * Manejador de habilitador de boton Sign In.
     */
    private void enableButtonSignIn() {
        boolean validEmail = validEmail();
        boolean validPassword = validPassword();
        btnSignIn.setDisable(!(validEmail && validPassword));
    }

    /**
     * Método validador de Email.
     */
    private boolean validEmail() {
        boolean isValidEmail = txtEmail.getText().isEmpty();
        return !isValidEmail;
    }

    /**
     * Método validador de Password.
     */
    private boolean validPassword() {
        boolean isValidPsw = txtPassword.getText().isEmpty();
        return !isValidPsw;
    }
}
