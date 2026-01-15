/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;

import crud_project.logic.AccountRESTClient;
import crud_project.logic.MovementRESTClient;
import crud_project.model.Account;
import crud_project.model.Customer;
import crud_project.model.Movement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.ws.rs.core.GenericType;

/**
 *
 * @author cynthia
 */
public class MovementController {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");

    private Stage stage;
    private Scene scene;
    private Movement movement;
    private Customer customer;
    private Account account;

    //Agregamos los id del fxml al controlador 
    @FXML
    private TableView<Movement> tbMovement;
    @FXML
    private TableColumn<Movement, Date> clDate;
    @FXML
    private TableColumn<Movement, Double> clAmount;
    @FXML
    private TableColumn<Movement, String> clDescription;
    @FXML
    private TableColumn<Movement, Double> clBalance;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblError;
    @FXML
    private ComboBox comboAccount;
    @FXML
    private TextField txtAmount;
    @FXML
    private ComboBox comboType;
    @FXML
    private Button createMovement;
    @FXML
    private Label lblName;

    //Se crea los botones para el alert
    private final ButtonType ok = new ButtonType("OK");
    private final ButtonType yes = new ButtonType("Yes");
    private final ButtonType no = new ButtonType("No");

    AccountRESTClient accountClient = new AccountRESTClient();
    MovementRESTClient movementClient = new MovementRESTClient();

    public void init(Parent root) {
        //Se muestra la escena
        Scene scene = new Scene(root);
        this.stage = new Stage();
        this.stage.setScene(scene);
        stage.setTitle("Movements");
        LOGGER.info("Initializing Movement Window");
        // Establecer el título de la ventana.
        this.stage.setTitle("Movement page");
        this.stage.setResizable(false);

        //Da valor a la factoría de celda 
        clDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        clAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        clDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        clBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        //Carga los movimientos de la tabla
        loadMovements();
        buttonEnable();
        btnBack.setCancelButton(true);
        
        lblName.setText(account.getId());
        //Se pone los valores en la combo de la description (type)
        ObservableList<String> items = FXCollections.observableArrayList("Deposit", "Payment");
        comboType.setItems(items);

        //Deshabilita el botón de crear movimiento
        createMovement.setDisable(true);
        //Ponemos eventos a manejadores
        createMovement.setOnAction(this::handleBtnCreate);
        btnDelete.setOnAction(this::handleBtnDelete);
        btnBack.setOnAction(this::handleBtnBack);
        //Se pone un listener en el TextField y en la ComboBox para ver cuando los valores cambian lleven al método
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> buttonEnable());
        comboType.valueProperty().addListener((observable, oldValue, newValue) -> buttonEnable());
        this.stage.show();

    }

    public void loadMovements() {
        try {
            //Id de prueba idAccount
            //Se crea una lista de movimientos
            GenericType<List<Movement>> movementListType = new GenericType<List<Movement>>() {
            };
            //id String.valueOf(account.getId())
            List<Movement> movements = movementClient.findMovementByAccount_XML(movementListType, "2654785441");
            ObservableList<Movement> dataMovement = FXCollections.observableArrayList(movements);
            //Se muestra la lista en la tabla
            LOGGER.info("Showing table of movements");
            tbMovement.setItems(dataMovement);
        } catch (Exception e) {
            handlelblError("Error to charge movements");
            LOGGER.info("Error to charge movements");
        }
    }

    public void handleBtnBack(ActionEvent event) {
        try {
            //Se carga el controlador y la vista de la ventana de Accounts
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountsController.fxml"));
            Parent root = loader.load();
            AccountsController controller = loader.getController();
            controller.setCustomer(new Customer());
            LOGGER.info("Showing accounts page");
            this.stage.hide();
            controller.init(root);
            controller.getStage().setOnHiding(e -> this.stage.show());
        } catch (Exception e) {
            
        }
    }

    public void handleBtnDelete(ActionEvent event) {
        try {
            /*Coger ultimom ovimiento
            tbMovements.getItems().get(tbMovements.getItems.size()-1)
            borrar por la ultima fecha 
             */
            if(tbMovement.getItems().isEmpty()){
             throw new Exception("There are no movements on the account.");
            }
            // Mostrar alert modal de confirmación para borrar el ultimo movimiento.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you want remove the last movement?", yes, no);
            alert.setTitle("Alert to delete movement");
            alert.setHeaderText("Departure confirmation");
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, cerrar la aplicación.
                if (resp == yes) {
                    //Si la respuesta es que si borra el ultimo movimiento
                    Movement lastMovement = tbMovement.getItems().get(tbMovement.getItems().size() - 1);
                    movementClient.remove(lastMovement.getId().toString());
                    //Se vuelve a cargar la tabla
                    loadMovements();
                    LOGGER.info("Movement deleted");
                } else {
                    alert.close();
                }
            });
        } catch (Exception e) {
            handlelblError(e.getMessage());
            e.printStackTrace();
        }
    }

    public void buttonEnable() {
        //Comprueba que la comboBox esté seleccionada y que el TextField no esté vacio
        boolean buttonTy = true;
        boolean buttonTx = true;

        String type = (String) comboType.getValue();
        String txt = txtAmount.getText();

        if (type == null) {
            buttonTy = false;
        }

        if (txt.trim().isEmpty()) {
            buttonTx = false;
        }
        createMovement.setDisable(!(buttonTy && buttonTx));
    }

    public void handleBtnCreate(ActionEvent event) {
        try {
            //Se crea el movimiento
            Movement newMovement = new Movement();
            //En el string type se parsea a un String con el valor seleccionado en la combo
            String type = (String) comboType.getValue();
            //Se recoge en amount el valor del text y se parsea a double
            Double amount = Double.valueOf(txtAmount.getText());
            //Si la cantidad es menor o igual que 0 salta el label de error
            if (amount <= 0) {
                 throw new Exception("Amount cant be negative");
            }
            //Si es tipo deposito se estable la descripcion y la cantidad
            if (type.equals("Deposit")) {
                newMovement.setAmount(amount);
                newMovement.setDescription(type);
            } else if (type.equals("Payment")) {
                //Si es tipo Payment se le pone la cantidad negativa y se establece la descripcion 
                newMovement.setAmount(-amount);
                newMovement.setDescription(type);

            }
            //Se pone la fecha y hora actual 
            Date date = new Date();
            newMovement.setTimestamp(date);
            //Salta un alert para confirmar la creacción del movimiento
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you create the movement?", yes, no);
            alert.setTitle("Alert to create movement");
            alert.setHeaderText("Departure confirmation");
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, cerrar la aplicación.
                if (resp == yes) {
                    try {
                        //Se crea el movimiento
                        movementClient.create_XML(newMovement, String.valueOf(account.getId()));
                        txtAmount.setText("");
                        loadMovements();
                        LOGGER.info("Movement created");
                        buttonEnable();
                    } catch (Exception e) {
                        handlelblError("The movement cant be created");
                    }
                } else {
                    alert.close();
                }
            });
        } catch (NumberFormatException e) {
            handlelblError("Invalid amount format");
        } catch (Exception e) {
            handlelblError(e.getMessage());
        }
    }

    public void handlelblError(String message) {
        lblError.setText(message);
    }

    public void handleAlert(String message) {

    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
