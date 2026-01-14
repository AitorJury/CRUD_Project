/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;
import crud_project.logic.MovementRESTClient;
import crud_project.model.Customer;
import crud_project.model.Movement;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private Button btnDeposit;
    @FXML
    private Button btnWithdraw;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblError;
    @FXML
    private ComboBox combo;
    @FXML
    private DatePicker datepicker;

    private final ButtonType ok = new ButtonType("OK");
    private final ButtonType yes = new ButtonType("Yes");
    private final ButtonType no = new ButtonType("No");
    MovementRESTClient movementClient = new MovementRESTClient();

    public void init(Parent root) {
        Scene scene = new Scene(root);
        this.stage = new Stage();
        this.stage.setScene(scene);
        LOGGER.info("Initializing Movement Window");
        // Establecer el título de la ventana.
        this.stage.setTitle("Movement page");
        this.stage.setResizable(false);
        
        clDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        clAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        clDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        clBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        loadMovements();
        btnBack.setCancelButton(true);

        btnDeposit.setOnAction(this::handleBtnDeposit);
        btnWithdraw.setOnAction(this::handleBtnWithdraw);
        btnDelete.setOnAction(this::handleBtnDelete);
        btnBack.setOnAction(this::handleBtnBack);
        this.stage.show();    

    }

    public void loadMovements() {
        try {
            //Id de prueba
             
            GenericType<List<Movement>> movementListType = new GenericType<List<Movement>>(){};  
            List<Movement> movements = movementClient.findMovementByAccount_XML(movementListType, "2654785441");
            ObservableList<Movement> dataMovement = FXCollections.observableArrayList(movements);
            tbMovement.setItems(dataMovement);
        } catch (Exception e) {
            handlelblError("Error to charge movements");
            LOGGER.info("Error to charge movements" + e.getMessage());
        }
    }

    public void handleBtnBack(ActionEvent event) {

    }

    public void handleBtnDelete(ActionEvent event) {
        try{
        movementClient.remove("");
        loadMovements();
        }
        catch(Exception e){
            handlelblError("Cannot delete movement");
        }
    }

    public void handleBtnWithdraw(ActionEvent event) {
    }
    public void handleBtnDeposit(ActionEvent event){
    }

    public void handlelblError(String message) {
        lblError.setText(message);
    }
    public void handleAlert(String message){
        
    }
    public Stage getStage() { 
        return this.stage; 
    }
    public void setCustomer(Customer customer){
        this.customer = customer;
    }
   
}
