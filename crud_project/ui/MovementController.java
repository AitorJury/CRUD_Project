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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private final ButtonType ok = new ButtonType("OK");
    private final ButtonType yes = new ButtonType("Yes");
    private final ButtonType no = new ButtonType("No");
    AccountRESTClient accountClient = new AccountRESTClient();
    MovementRESTClient movementClient = new MovementRESTClient();

    public void init(Parent root) {
        Scene scene = new Scene(root);
        this.stage = new Stage();
        this.stage.setScene(scene);
        stage.setTitle("Movements");
        LOGGER.info("Initializing Movement Window");
        // Establecer el título de la ventana.
        this.stage.setTitle("Movement page");
        this.stage.setResizable(false);

        DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        clDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        clAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        clDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        clBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        loadMovements();
        btnBack.setCancelButton(true);
        
        //Poner valores en la combo de las cuentas
          
        //Account account = accountClient.findAccountsByCustomerId_XML(responseType, "");
        //ObservableList<Account> cuenta = FXCollections.observableArrayList();
        //comboAccount.setItems(cuenta);
        
        
        //Poner valores en la combo de la description
        ObservableList<String> items = FXCollections.observableArrayList("Deposit", "Payments");
        comboType.setItems(items);

        comboType.setOnAction(this::handleComboType);
        createMovement.setOnAction(this::handleBtnCreate);
        btnDelete.setOnAction(this::handleBtnDelete);
        btnBack.setOnAction(this::handleBtnBack);
        this.stage.show();

    }

    public void loadMovements() {
        try {
            //Id de prueba

            GenericType<List<Movement>> movementListType = new GenericType<List<Movement>>() {
            };
            List<Movement> movements = movementClient.findMovementByAccount_XML(movementListType, "2654785441");
            ObservableList<Movement> dataMovement = FXCollections.observableArrayList(movements);
            tbMovement.setItems(dataMovement);
        } catch (Exception e) {
            handlelblError("Error to charge movements");
            LOGGER.info("Error to charge movements" + e.getMessage());
        }
    }


    public void handleBtnBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountsController.fxml"));
            Parent root = loader.load();
            AccountsController controller = loader.getController();
            controller.setCustomer(new Customer());

            this.stage.hide();
            controller.init(root);
            controller.getStage().setOnHiding(e -> this.stage.show());
        } catch (Exception e) {

        }
    }

    public void handleComboType(Event event) {
        
        String selecction = (String) comboType.getValue();
        
        if (selecction.equals("Deposit")) {
            
            movementClient.create_XML("2654785441","");
        } else if (selecction.equals("Payments")) {

        }

        /*
       miComboBox.setOnAction(e -> {
    String valorSeleccionado = miComboBox.getValue();
    System.out.println("Seleccionaste: " + valorSeleccionado);
});

         */
    }
    
    
    public void handleBtnDelete(ActionEvent event) {
        try {
            /*Coger ultimom ovimiento
            tbMovements.getItems().get(tbMovements.getItems.size()-1)
            */
            
            // Mostrar alert modal de confirmación para salir de la aplicación.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you want remove the last movement?", yes, no);
            alert.setTitle("Alert to delete movement");
            alert.setHeaderText("Departure confirmation");
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, cerrar la aplicación.
                if (resp == yes) {
                    //ultimom movimiento
                    Movement lastMovement = tbMovement.getItems().get(tbMovement.getItems().size()-1);
                    movementClient.remove(lastMovement.getId().toString());
                    loadMovements();
                } else {
                    alert.close();
                }
            });
        } catch (Exception e) {
            handlelblError("Cannot delete movement");
        }
    }

    public void handleBtnCreate(ActionEvent event) {
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
    public void setAccount(Account account){
        this.account = account;
    }
    
}
