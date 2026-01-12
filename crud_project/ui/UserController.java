/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;

import java.util.*;

import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import crud_project.logic.CustomerRESTClient;

import crud_project.model.Customer;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.event.Event;
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
public class UserController {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private final Stage userStage = new Stage();
    private Scene userScene;
    private Customer customer;

    @FXML
    public TableView<Customer> fxTableView;
    @FXML
    public TableColumn<Customer, String> fxTcId;
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
    public TableColumn<Customer, String> fxTcPhone;
    @FXML
    public TableColumn<Customer, String> fxTcStreet;
    @FXML
    public TableColumn<Customer, String> fxTcCity;
    @FXML
    public TableColumn<Customer, String> fxTcState;
    @FXML
    public TableColumn<Customer, String> fxTcZip;
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
    @FXML
    public Button fxBtnExit;


    ArrayList<Customer> customerList = new ArrayList<>();

    public static final CustomerRESTClient client = new CustomerRESTClient();
    ObservableList<Customer> customersData;

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
        fxTableView.setEditable(true);
        fxTcId.setCellValueFactory(new PropertyValueFactory<>("id"));

        fxTcFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        fxTcFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        fxTcFirstName.setEditable(true);

        fxTcLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        fxTcLastName.setEditable(true);
        fxTcLastName.setCellFactory(TextFieldTableCell.forTableColumn());

        fxTcMidName.setCellValueFactory(new PropertyValueFactory<>("middleInitial"));
        fxTcMidName.setEditable(true);
        fxTcMidName.setCellFactory(TextFieldTableCell.forTableColumn());

        fxTcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        fxTcEmail.setEditable(true);
        fxTcEmail.setCellFactory(TextFieldTableCell.forTableColumn());

        fxTcPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        fxTcPassword.setEditable(true);
        fxTcPassword.setCellFactory(TextFieldTableCell.forTableColumn());

        fxTcPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        fxTcPhone.setEditable(true);
        fxTcPhone.setCellFactory(TextFieldTableCell.forTableColumn());


        fxTcStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        fxTcStreet.setEditable(true);
        fxTcStreet.setCellFactory(TextFieldTableCell.forTableColumn());

        fxTcCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        fxTcCity.setEditable(true);
        fxTcCity.setCellFactory(TextFieldTableCell.forTableColumn());

        fxTcState.setCellValueFactory(new PropertyValueFactory<>("state"));
        fxTcState.setEditable(true);
        fxTcState.setCellFactory(TextFieldTableCell.forTableColumn());

        fxTcZip.setCellValueFactory(new PropertyValueFactory<>("zip"));
        fxTcZip.setEditable(true);
        fxTcPhone.setCellFactory(TextFieldTableCell.forTableColumn());

        //Carga de datos a las columnas
//        client.findAll_XML(customerList.getClass());
//        customerData = FXCollections.observableArrayList(customerList);
//        fxTableView.setItems(customerData);

        //Prueba de carga de datos con Array[]
        Customer[] response = client.findAll_XML(Customer[].class);
        customerList = new ArrayList<>(Arrays.asList(response));
        customersData = FXCollections.observableArrayList(customerList);
        fxTableView.setItems(customersData);


        userStage.setOnCloseRequest(this::handleOnExitAction);

        //---- Accion de botones
        fxBtnNewCustomer.setOnAction(this::handleAddCustomerRow);
        fxBtnDelete.setOnAction(this::handleDeleteCustomerAndRow);
        fxBtnNewCustomer.setOnAction(this::handleAddCustomerRow);
        fxBtnSaveChanges.setOnAction(this::handleSaveChanges);
        fxBtnSaveChanges.setDisable(true);
        fxBtnExit.setOnAction(this::handleOnExitAction);
        //-------------------------------------

        //Comprobacion de cambio de fila
        fxTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelectionChanged);

        //Filtro para la celda, nombre en modo edicion
        fxTcFirstName.setOnEditCommit(this::handleFirstNameCellEdit);
        handleEditCellStringValue(fxTcFirstName, Customer::setFirstName);



    }


    public void handleEditCellStringValue(TableColumn<Customer, String> event, BiConsumer<Customer, String> setter) {
        //TODO implentar logica para manejar edicion de celdas de(Nombre, Apellido,Calle,Ciudad,Estado)

    }

    private void handleSaveChanges(ActionEvent actionEvent) {
        try {

            List<Customer> newCustomers = customersData.stream()
                    .filter(newCustomer -> !customerList.contains(newCustomer))
                    .collect(Collectors.toList());

            if (newCustomers.isEmpty()) {
                LOGGER.info("No new customers to save");
                throw new Exception("No new customers to save");
            }
            newCustomers.forEach(client::create_XML);

            Customer[] updatedList = client.findAll_XML(Customer[].class);
            customerList = new ArrayList<>(Arrays.asList(updatedList));

            //Para refrescar la tabla
            customersData.setAll(updatedList);


        } catch (Exception e) {
            handleAlertError("Error saving the customer " + e.getMessage());
            LOGGER.warning(e.getMessage());
        }


    }

    private void handleTableSelectionChanged(ObservableValue observable, Object oldValue, Object newValue) {

        fxBtnDelete.setDisable(newValue == null);
        fxBtnSaveChanges.setDisable(newValue == null);


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
                        client.remove(selectedCustomer.getId().toString());
                        fxTableView.getItems().remove(selectedCustomer);
                        fxTableView.getSelectionModel().clearSelection();
                    }
                });

            }

        } catch (Exception e) {

            handleAlertError(e.getMessage());
            LOGGER.warning(e.getMessage());
        }
    }

    public void handleAddCustomerRow(ActionEvent event) {

        try {

            Customer newCustomer = new Customer();

            fxTableView.getItems().add(0, newCustomer);
            fxTableView.getSelectionModel().clearAndSelect(0);
            fxTableView.requestFocus();
            fxTableView.scrollTo(0);
            fxTableView.edit(0, fxTcFirstName);


        } catch (Exception e) {
            handleAlertError("Error saving new customer...");
            LOGGER.severe("Error saving new customer: " + e.getMessage());
        }

    }

    /**
     * Metodo para la gestión y validacion de la celda de First Name
     *
     * @param event
     */
    private void handleFirstNameCellEdit(TableColumn.CellEditEvent<Customer, String> event) {

        String newValue = event.getNewValue().trim();
        Customer myCustomer = event.getRowValue();

        try {
            if (newValue.isEmpty()) {
                throw new Exception("The name should be fill");
            }
            if (!newValue.matches("[a-zA-Z]+")) {
                throw new Exception("The name should contain only letters");
            }
            if (newValue.length() > 20) {
                throw new Exception("The name should be less than 20 characters");

            }
            myCustomer.setFirstName(newValue);


        } catch (Exception e) {
            LOGGER.warning("Error in First Name cell edit: " + e.getMessage());
            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }

    }



    private void handleAlertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();

    }

    private void handleOnExitAction(Event event) {
        try {
            LOGGER.info("Clicked exit button");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.OK, ButtonType.CANCEL);
            alert.showAndWait();
            alert.setTitle("Exit Confirmation");
            if (alert.getResult() == ButtonType.OK) {
                userStage.close();
                event.consume();
            }
        } catch (Exception e) {
            handleAlertError("Fail to Close");
        }
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
