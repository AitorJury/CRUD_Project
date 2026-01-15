/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;

import java.util.*;

import java.util.logging.Logger;

import crud_project.logic.CustomerRESTClient;

import crud_project.model.Customer;

import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

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
    public Button fxBtnFind;
    @FXML
    public Button fxBtnNewCustomer;
    @FXML
    public Button fxBtnDelete;
    @FXML
    public Button fxBtnSaveChanges;
    @FXML
    public Button fxBtnExit;


    public static final CustomerRESTClient client = new CustomerRESTClient();
    ObservableList<Customer> customersData;

    public void initUserStage(Parent root) {


        //Creacion de la nueva ventana para User
        userScene = new Scene(root);
        userStage.setScene(userScene);
        LOGGER.info("Initialization window user");
        userStage.setTitle("Users management for ADMIN");
        LOGGER.info("Setting title");
        userStage.setResizable(false);
        LOGGER.info("Setting fix size");
        userStage.show();
        LOGGER.info("Showing window");

        //Deshabilitar boton de delete
        fxBtnDelete.setDisable(true);


        //Recuperar lista de todos los customers
        //Configuracion de columnas
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
        fxTcPhone.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));

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
        fxTcZip.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        //Carga de datos a las columnas
        customersData = FXCollections.observableArrayList(client.findAll_XML(new GenericType<List<Customer>>() {
        }));
        fxTableView.setItems(customersData);


        userStage.setOnCloseRequest(this::handleOnExitAction);


        //Comprobacion de cambio de fila
        fxTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelectionChanged);

        //Filtro para la celda, nombre en modo edicion
        fxTcFirstName.setOnEditCommit(this::handleFirstNameCellEdit);
        fxTcLastName.setOnEditCommit(this::handleLastNameCellEdit);
        fxTcMidName.setOnEditCommit(this::handleMiddleInitialCellEdit);
        fxTcStreet.setOnEditCommit(this::handleStreetCellEdit);
        fxTcCity.setOnEditCommit(this::handleCityCellEdit);
        fxTcState.setOnEditCommit(this::handleStateCellEdit);
        fxTcEmail.setOnEditCommit(this::handleEmailCellEdit);
        fxTcPassword.setOnEditCommit(this::handlePasswordCellEdit);
        fxTcZip.setOnEditCommit(this::handleZipCellEdit);
        fxTcPhone.setOnEditCommit(this::handlePhoneCellEdit);

        //---- Accion de botones
        fxBtnNewCustomer.setOnAction(this::handleAddCustomerRow);
        fxBtnDelete.setOnAction(this::handleDeleteCustomerAndRow);
        fxBtnExit.setOnAction(this::handleOnExitAction);


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
                if (!selectedCustomer.getAccounts().isEmpty()) {
                    throw new WebApplicationException("No se puede eliminar un cliente con cuentas");
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

        } catch (IllegalArgumentException | WebApplicationException ex) {
            LOGGER.warning(ex.getMessage());
            handleAlertError(ex.getMessage());

        } catch (Exception e) {

            handleAlertError("Cannot dele this user");
            LOGGER.warning(e.getMessage());
        }
    }

    public void handleAddCustomerRow(ActionEvent event) {

        try {

            Customer newCustomer = new Customer();
            client.create_XML(newCustomer);
            fxTableView.getItems().add(0, newCustomer);

            Customer bdCustomer = client.findCustomerByEmailPassword_XML(Customer.class, newCustomer.emailProperty().get(), "clave$%&");
            Long idCustomer = bdCustomer.getId();
            newCustomer.setId(idCustomer);

            fxTableView.requestFocus();
            fxTableView.getSelectionModel().clearAndSelect(0);
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
     * @param cellName
     */
    private void handleFirstNameCellEdit(TableColumn.CellEditEvent<Customer, String> cellName) {

        String newValue = cellName.getNewValue().trim();
        Customer myCustomer = cellName.getRowValue();

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
            myCustomer.firstNameProperty().set(newValue);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {
            LOGGER.warning("Error in First Name cell edit: " + e.getMessage());
            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }

    }

    private void handleLastNameCellEdit(TableColumn.CellEditEvent<Customer, String> cellName) {

        String newValue = cellName.getNewValue().trim();
        Customer myCustomer = cellName.getRowValue();

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
            myCustomer.lastNameProperty().set(newValue);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {
            LOGGER.warning("Error in First Name cell edit: " + e.getMessage());
            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }

    }

    private void handleMiddleInitialCellEdit(TableColumn.CellEditEvent<Customer, String> cell) {

        //Obetener valor de la celda
        String newValue = cell.getNewValue().trim().toUpperCase();
        //Obtener 
        Customer myCustomer = cell.getRowValue();
        try {

            if (newValue.isEmpty()) {
                throw new Exception("The field must be filled");
            }

            if (newValue.length() > 1) {

                throw new Exception("The initial should be one letter");
            }
            if (!newValue.matches("[a-zA-Z]+")) {

                throw new Exception("The name should contain only letter");
            }
            myCustomer.middleInitialProperty().set(newValue);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {


            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }
    }

    private void handleStreetCellEdit(TableColumn.CellEditEvent<Customer, String> streetCell) {

        String text = streetCell.getNewValue().trim();
        Customer myCustomer = streetCell.getRowValue();
        try {

            if (text.isEmpty()) {
                throw new Exception("The field must be filled");
            }
            if (!text.matches("[\\p{L}\\p{N}\\s,.-/ºª#]+")) {
                throw new Exception("Street contains invalid characters");
            }
            if (text.length() > 50) {
                throw new Exception("Street cannot exceed length of 50");
            }
            client.edit_XML(myCustomer, myCustomer.getId());
            myCustomer.streetProperty().set(text);

        } catch (Exception e) {

            handleAlertError(e.getMessage());
            fxTableView.refresh();

        }

    }

    private void handleCityCellEdit(TableColumn.CellEditEvent<Customer, String> cell) {

        try {

            String text = cell.getNewValue().trim();
            Customer myCustomer = cell.getRowValue();

            if (text.isEmpty()) {
                throw new Exception("City must not be empty");
            }
            // Sí tiene algo distinto a letras y espacios, lanzar excepción.
            if (!text.matches("[a-zA-Z\\s]+")) {
                throw new Exception("City must contain only letters and spaces");
            }
            // Sí tiene más de 20 caracteres lanzar excepcion
            if (text.length() > 20) {

                throw new Exception("City cannot exceed length of 20");
            }
            myCustomer.cityProperty().set(text);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {
            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }

    }

    private void handleStateCellEdit(TableColumn.CellEditEvent<Customer, String> cell) {
        try {
            String text = cell.getNewValue().trim();
            Customer myCustomer = cell.getRowValue();

            if (text.isEmpty()) {
                throw new Exception("State must not be empty");
            }
            if (!text.matches("[a-zA-Z\\s]+")) {
                throw new Exception("State must contain only letters");
            }
            if (text.length() > 20) {
                throw new Exception("State cannot exceed length of 20");
            }
            myCustomer.stateProperty().set(text);
            client.edit_XML(myCustomer, myCustomer.getId());


        } catch (Exception e) {
            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }
    }

    private void handleEmailCellEdit(TableColumn.CellEditEvent<Customer, String> cell) {
        try {
            String text = cell.getNewValue().trim();
            Customer myCustomer = cell.getRowValue();

            if (text.isEmpty()) {
                throw new Exception("Email must not be empty");
            }
            if (!text.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new Exception("Email format invalid");
            }
            if (text.length() > 50) {
                throw new Exception("Email cannot exceed length of 50");
            }
            myCustomer.emailProperty().set(text);
            client.edit_XML(myCustomer, myCustomer.getId());
            LOGGER.info("Email updated");


        } catch (Exception e) {
            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }
    }

    private void handlePasswordCellEdit(TableColumn.CellEditEvent<Customer, String> cell) {
        try {
            String text = cell.getNewValue().trim();
            Customer myCustomer = cell.getRowValue();

            if (text.isEmpty()) {
                throw new Exception("Password must not be empty");
            }
            if (!text.matches("[a-zA-Z0-9.*!@#$%&\\-_]+")) {
                throw new Exception("Password contains invalid characters");
            }
            if (text.length() < 8) {
                throw new Exception("Password must be at least 8 characters");
            }
            LOGGER.info("Correct Password");
            myCustomer.passwordProperty().set(text);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {
            LOGGER.warning("Error in Password cell edit: " + e.getMessage());
            handleAlertError(e.getMessage());
            fxTableView.refresh();
        }
    }

    private void handleZipCellEdit(TableColumn.CellEditEvent<Customer, Integer> cell) {
        try {
            Integer text = cell.getNewValue();
            Customer myCustomer = cell.getRowValue();

            if (text == null) {
                throw new Exception("Zip code must not be empty");
            }
            if (text < 0) {
                throw new Exception("Zip code must be positive");
            }
            myCustomer.zipProperty().set(text);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (NumberFormatException | InputMismatchException e) {
            handleAlertError("Zip code must be a number");
            LOGGER.severe("Error in Zip cell edit: " + e.getMessage());
            fxTableView.refresh();

        } catch (Exception e) {
            handleAlertError(e.getMessage());
            LOGGER.severe(e.getMessage());
            fxTableView.refresh();
        }
    }

    private void handlePhoneCellEdit(TableColumn.CellEditEvent<Customer, Long> cell) {
        try {
            Long number = cell.getNewValue();
            Customer myCustomer = cell.getRowValue();

            if (number == null) {
                throw new Exception("Phone number must not be empty");
            }

            String text = String.valueOf(number);

            if (text.length() < 7 || text.length() > 11) {
                throw new Exception("Phone length must be 7-11 digits");
            }


            myCustomer.phoneProperty().set(number);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (NumberFormatException | InputMismatchException e) {
            handleAlertError("Phone number must be a number");
            LOGGER.severe("Error in Phone cell edit: " + e.getMessage());
            fxTableView.refresh();
        } catch (Exception e) {
            handleAlertError(e.getMessage());
            LOGGER.severe(e.getMessage());
            fxTableView.refresh();
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


    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
