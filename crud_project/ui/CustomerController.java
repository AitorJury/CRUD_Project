/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import crud_project.logic.CustomerRESTClient;
import crud_project.model.Customer;
import crud_project.model.CustomerBean;
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
public class CustomerController implements CustomerManager {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private ArrayList<CustomerBean> customers;
    private final Stage userStage = new Stage();
    private Scene userScene;
    private Customer customer;
    private final ObservableList<CustomerBean> customerData = null;


    @FXML
    public TableView<CustomerBean> fxTableView;
    @FXML
    public TableColumn<CustomerBean, Long> fxTcId;
    @FXML
    public TableColumn<CustomerBean, String> fxTcFirstName;
    @FXML
    public TableColumn<CustomerBean, String> fxTcLastName;
    @FXML
    public TableColumn<CustomerBean, String> fxTcMidName;
    @FXML
    public TableColumn<CustomerBean, String> fxTcEmail;
    @FXML
    public TableColumn<CustomerBean, String> fxTcPassword;
    @FXML
    public TableColumn<CustomerBean, Long> fxTcPhone;
    @FXML
    public TableColumn<CustomerBean, String> fxTcStreet;
    @FXML
    public TableColumn<CustomerBean, String> fxTcCity;
    @FXML
    public TableColumn<CustomerBean, String> fxTcState;
    @FXML
    public TableColumn<CustomerBean, Integer> fxTcZip;
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



    public void initUserStage(Parent root) {



    }

    @Override
    public Collection getAllCustomers() throws RuntimeException {
        LOGGER.info("Getting all customers from database.");
        return customers;
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
