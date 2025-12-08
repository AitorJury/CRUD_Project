/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import crud_project.logic.CustomerRESTClient;
import crud_project.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;




/**
 *
 * @author juancaizaduenas
 */
public class UserController {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private final Stage userStage = new Stage();
    private Scene userScene;
    private Customer customer;


    @FXML
    public TableView<Customer> fxTableView;
    @FXML
    public TableColumn<Customer, Long> fxTcId;
    @FXML
    public TableColumn<Customer, String> fxTcFirstName;
    @FXML
    public TableColumn<Customer, String> fxTcLastName;
    @FXML
    public TableColumn<Customer, String> fxTcMidName;
    @FXML
    public TableColumn<Customer, String> fxTcEmail;
    @FXML
    public TableColumn<Customer, String> fxTcPassword;
    @FXML
    public TableColumn<Customer, Long> fxTcPhone;
    @FXML
    public TableColumn<Customer, String> fxTcStreet;
    @FXML
    public TableColumn<Customer, String> fxTcCity;
    @FXML
    public TableColumn<Customer, String> fxTcState;
    @FXML
    public TableColumn<Customer, Integer> fxTcZip;
    @FXML
    public TextField fxTfSearchBar;
    @FXML
    public Button fxBtnFind;
    @FXML
    public Button fxBtnNewCustomer;
    @FXML
    public Button fxBtnDelete;
    @FXML
    public Button fxBtnSaveChanges;
    private ObservableList<Customer> customerData = FXCollections.observableArrayList();

    public void initUserStage(Parent root) {
        //Creacion de la nueva ventana para User
        userScene = new Scene(root);
        userStage.setScene(userScene);
        LOGGER.info("Initialization window user");
        userStage.setTitle("Users management for " + customer.getFirstName());
        LOGGER.info("Setting title");
        userStage.setResizable(false);
        LOGGER.info("Setting fix size");
        userStage.show();
        LOGGER.info("Showing window");

        //Recuperar lista de todos los customers
        CustomerRESTClient client = new CustomerRESTClient();

        //Configuracion de columnas
        fxTcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        fxTcFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        fxTcLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        fxTcMidName.setCellValueFactory(new PropertyValueFactory<>("middleInitial"));
        fxTcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        fxTcPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        fxTcPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        fxTcStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        fxTcCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        fxTcState.setCellValueFactory(new PropertyValueFactory<>("state"));
        fxTcZip.setCellValueFactory(new PropertyValueFactory<>("zip"));

        //Carga de datos a las columnas
        Customer[] customersArray = client.findAll_XML(Customer[].class);
        List<Customer> customers = Arrays.asList(customersArray);

        customerData.setAll(customers);
        fxTableView.setItems(customerData);



    }

    public Stage getStage() {
        return this.userStage;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
