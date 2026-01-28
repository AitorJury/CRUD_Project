/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;

import java.awt.*;
import java.util.*;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import crud_project.logic.CustomerRESTClient;

import crud_project.model.Customer;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 *
 * @author juancaizaduenas
 */
public class CustomerController {

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
    private ObservableList<Customer> customerData;
    ArrayList<Customer> customerList = new ArrayList<>();

    public CustomerRESTClient client = new CustomerRESTClient();

    public void initUserStage(Parent root) {

        //Deshabilitar boton de delete
        fxBtnDelete.setDisable(true);

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
        //Configuracion de columnas
        fxTfSearchBar.setPromptText("Search by name");
        //Columnas editables
        fxTcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        fxTcFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        fxTcFirstName.setEditable(true);
        fxTcLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        fxTcLastName.setEditable(true);
        fxTcMidName.setCellValueFactory(new PropertyValueFactory<>("middleInitial"));
        fxTcMidName.setEditable(true);
        fxTableView.setEditable(true);
        fxTcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        fxTcEmail.setEditable(true);
        fxTcPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        fxTcPassword.setEditable(true);
        fxTcPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        fxTcPhone.setEditable(true);
        fxTcStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        fxTcStreet.setEditable(true);
        fxTcCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        fxTcCity.setEditable(true);
        fxTcState.setCellValueFactory(new PropertyValueFactory<>("state"));
        fxTcState.setEditable(true);
        fxTcZip.setCellValueFactory(new PropertyValueFactory<>("zip"));
        fxTcZip.setEditable(true);

        //Carga de datos a las columnas
//        client.findAll_XML(customerList.getClass());
//        customerData = FXCollections.observableArrayList(customerList);
//        fxTableView.setItems(customerData);
        //Prueba de carga de datos con Array[]
        Customer[] response = client.findAll_XML(Customer[].class);
        customerList = new ArrayList<>(Arrays.asList(response));
        customerData = FXCollections.observableArrayList(customerList);

        fxTableView.setItems(customerData);
        fxTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelectionChanged);

        fxBtnNewCustomer.setOnAction(this::addCustomer);
        fxBtnDelete.setOnAction(this::handleDeleteCustomerAndRow);

    }

    private void handleTableSelectionChanged(ObservableValue observable, Object oldValue, Object newValue) {

        fxBtnDelete.setDisable(newValue == null);

    }

    private void handleDeleteCustomerAndRow(ActionEvent actionEvent) {
        try {

            Customer selectedCustomer = fxTableView.getSelectionModel().getSelectedItem();
            //Comprobar si esta seleccionado una fila
            if (selectedCustomer != null) {

                if (selectedCustomer.getFirstName().equals("admin")) {
                    throw new IllegalArgumentException("No se puede borrar el usuario administrador");
                }

                Alert deleteAlert = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Seguro que quieres eliminar al usuario: " + selectedCustomer.getFirstName() + "?",
                        ButtonType.YES, ButtonType.NO);

                deleteAlert.setTitle("Delete user?");
                deleteAlert.setHeaderText("Deleting user: " + selectedCustomer.getFirstName());
                deleteAlert.showAndWait().ifPresent(resp -> {
                    if (resp == ButtonType.YES) {
                        //client.remove(selectedCustomer.getId().toString());
                        fxTableView.getItems().remove(selectedCustomer);
                        fxTableView.getSelectionModel().clearSelection();
                        fxTableView.refresh();
                    }
                });


            }

        } catch (Exception e) {

            handleAlertError(e.getMessage());
            LOGGER.warning(e.getMessage());
        }
    }

    public void addCustomer(ActionEvent event) {

        /*fxTableView.getItems().add(Customer.builder()
                .id(customer.getId())
                .firstName("")
                .lastName("")
                .middleInitial("")
                .street("")
                .city("")
                .state("")
                .zip(0)
                .phone(0L)
                .email("")
                .password("")
                .accounts(customer.getAccounts())
                .build());
         */
        fxTableView.refresh();

    }

    /**
     * Metodo para la edicion de cada celda de las columnas editables
     */
    public void editStringCell(TableColumn<Customer, String> column, BiConsumer<Customer, String> setter) {

        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(
                (TableColumn.CellEditEvent<Customer, String> t) -> {
                    Customer customer
                            = t.getTableView().getItems().get(
                            t.getTablePosition().getRow()
                    );
                    setter.accept(customer, t.getNewValue());
                }
        );
    }

    private void handleAlertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();

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
