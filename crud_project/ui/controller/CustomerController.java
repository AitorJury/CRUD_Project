/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui.controller;


import java.util.*;

import java.util.logging.Logger;

import crud_project.logic.AccountRESTClient;
import crud_project.logic.CustomerRESTClient;

import crud_project.model.Account;
import crud_project.model.Customer;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.GenericType;

/**
 *
 * @author juancaizaduenas
 */
public class CustomerController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(CustomerController.class.getName());

    public static final String BASIC_MATCHER = "[a-zA-Z]+";

    private Scene userScene;
    public Customer customer;

    /**
     * Tabla que muestra la lista de clientes
     */
    @FXML
    public TableView<Customer> fxTableView;
    /**
     * Columna para el ID del cliente
     */
    @FXML
    public TableColumn<Customer, Long> fxTcId;
    /**
     * Columna para el nombre del cliente
     */
    @FXML
    public TableColumn<Customer, String> fxTcFirstName;
    /**
     * Columna para el apellido del cliente
     */
    @FXML
    public TableColumn<Customer, String> fxTcLastName;
    /**
     * Columna para la inicial del segundo nombre
     */
    @FXML
    public TableColumn<Customer, String> fxTcMidName;
    /**
     * Columna para el correo electrónico del cliente
     */
    @FXML
    public TableColumn<Customer, String> fxTcEmail;
    /**
     * Columna para la contraseña del cliente
     */
    @FXML
    public TableColumn<Customer, String> fxTcPassword;
    /**
     * Columna para el teléfono del cliente
     */
    @FXML
    public TableColumn<Customer, Long> fxTcPhone;
    /**
     * Columna para la calle de la dirección del cliente
     */
    @FXML
    public TableColumn<Customer, String> fxTcStreet;
    /**
     * Columna para la ciudad del cliente
     */
    @FXML
    public TableColumn<Customer, String> fxTcCity;
    /**
     * Columna para el estado/provincia del cliente
     */
    @FXML
    public TableColumn<Customer, String> fxTcState;
    /**
     * Columna para el código postal del cliente
     */
    @FXML
    public TableColumn<Customer, Integer> fxTcZip;

    /**
     * Botón para buscar clientes
     */
    @FXML
    public Button fxBtnFind;
    /**
     * Botón para crear un nuevo cliente
     */
    @FXML
    public Button fxBtnNewCustomer;
    /**
     * Botón para eliminar un cliente
     */
    @FXML
    public Button fxBtnDelete;
    /**
     * Botón para guardar cambios realizados
     */
    @FXML
    public Button fxBtnSaveChanges;
    /**
     * Botón para salir de la ventana
     */
    @FXML
    public Button fxBtnExit;

    /**
     * Controlador del menú superior
     */
    @FXML
    public MenuBarController topMenuController;


    /**
     * Inicializacion del cliente REST para comunicacion con la BD
     */
    public static final CustomerRESTClient client = new CustomerRESTClient();

    /**
     * Lista de clientes
     */
    ObservableList<Customer> customersData;

    /**
     * Inicializa la ventana del usuario con los componentes de la interfaz.
     *
     * @param root El nodo raíz de la escena.
     */
    public void initUserStage(Parent root) {

        this.stage = new Stage();
        //Creacion de la nueva ventana para User
        userScene = new Scene(root);
        stage.setScene(userScene);
        LOGGER.info("Initialization window user");
        stage.setTitle("Users management for ADMIN");
        LOGGER.info("Setting title");
        stage.setResizable(false);
        LOGGER.info("Setting fix size");
        stage.show();
        LOGGER.info("Showing window");

        //Deshabilitar boton de delete
        fxBtnDelete.setDisable(true);

        loadData();

        //metodo para cuando el usuario quiere cerrar la aplicacion
        stage.setOnCloseRequest(this::handleExit);

        //Comprobacion de cambio de fila
        fxTableView.getSelectionModel().selectedItemProperty().addListener(this::handleTableSelectionChanged);

        //Filtro para cada celda, se acciona en el evento de commit (en este caso al pulsar enter)
        //Cada celda tiene sus propias validaciones
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

        //---- Accion de botones ----//
        /* Añade una fila y llama al metodo create_XML del RESTClient para crear un nuevo cliente*/
        fxBtnNewCustomer.setOnAction(this::handleCreate);
        /* Borra una fila y llama al metodo delete_XML del RESTClient para eliminar el cliente*/
        fxBtnDelete.setOnAction(this::handleDelete);
        /* Salir de la aplicacion a traves del boton Exit creado*/
        fxBtnExit.setOnAction(this::handleExit);

        /*
         * Import del componente reutilizable de MenuBarController
         */
        topMenuController.init(stage);
        topMenuController.fxMenuContent.setOnAction(event -> showHelp());

        setupTableContextMenu();


    }

    @Override
    protected void handleCreate(ActionEvent event) {
        try {

            Customer newCustomer = new Customer();
            client.create_XML(newCustomer);
            fxTableView.getItems().add(0, newCustomer);

            Customer bdCustomer = client.findCustomerByEmailPassword_XML(Customer.class, newCustomer.emailProperty().get(), "clave$%&");
            Long idCustomer = bdCustomer.getId();
            newCustomer.setId(idCustomer);

            Platform.runLater(() -> {
                fxTableView.requestFocus();
                fxTableView.getSelectionModel().select(0);
                fxTableView.scrollTo(0);
                fxTableView.edit(0, fxTcFirstName);
            });


        } catch (Exception e) {
            showError("Error saving new customer...");
            LOGGER.severe("Error saving new customer: " + e.getMessage());
        }
    }

    @Override
    protected void handleUpdate(ActionEvent event) {
        // La actualización se realiza mediante la edición de celdas
    }

    @Override
    protected void handleDelete(ActionEvent event) {
        try {

            AccountRESTClient accClient = new AccountRESTClient();

            Customer selectedCustomer = fxTableView.getSelectionModel().getSelectedItem();
            //Comprobar si esta seleccionado una fila

            Set<Account> account = accClient.findAccountsByCustomerId_XML(
                    new GenericType<Set<Account>>() {
                    },
                    selectedCustomer.getId().toString()
            );

            if (selectedCustomer.getFirstName().equals("admin")) {
                throw new IllegalArgumentException("No se puede borrar el usuario administrador");
            }

            if (account != null && !account.isEmpty()) {
                throw new InternalServerErrorException("The user cannot be deleted because they have associated accounts or data.");
            }

            Alert deleteAlert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete the user: " + selectedCustomer.getFirstName() + "?",
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


        } catch (InternalServerErrorException | IllegalArgumentException ex) {
            showError(ex.getMessage());
            LOGGER.severe(ex.getMessage());
        } catch (Exception e) {
            showError("Error while deleting this user, please try again.");
            LOGGER.severe(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    protected String getHelpResourcePath() {
        return "/crud_project/ui/res/help.html";
    }

    @Override
    protected String getHelpWindowTitle() {
        return "Customer manager help";
    }

    @Override
    protected void loadData() {
        //Configuracion de la tabla a modo editable
        fxTableView.setEditable(true);

        // setCellValueFactory define cómo extraer el valor de la propiedad del objeto de modelo para mostrarlo en la celda.

        // setCellFactory define cómo se renderiza la celda y permite que sea editable, por ejemplo, usando un TextField.

        fxTcId.setCellValueFactory(new PropertyValueFactory<>("id"));

        fxTcFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        fxTcFirstName.setEditable(true);
        fxTcFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

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

        customersData = FXCollections.observableArrayList(client.findAll_XML(new GenericType<List<Customer>>() {
        }));
        fxTableView.setItems(customersData);
    }


    /**
     * Maneja el cambio de selección de filas en la tabla de clientes.
     *
     * @param observable El valor observable.
     * @param oldValue   El valor antiguo.
     * @param newValue   El nuevo valor seleccionado.
     */
    private void handleTableSelectionChanged(ObservableValue observable, Object oldValue, Object newValue) {

        fxBtnDelete.setDisable(newValue == null);


    }


    /**
     * Metodo para la gestión y validación de la edición de la celda del nombre.
     *
     * @param cellName El evento de edición de celda.
     */
    private void handleFirstNameCellEdit(TableColumn.CellEditEvent<Customer, String> cellName) {

        String newValue = cellName.getNewValue().trim();
        Customer myCustomer = cellName.getRowValue();

        try {
            if (newValue.isEmpty()) {
                throw new Exception("The name should be fill");
            }
            if (!newValue.matches(BASIC_MATCHER)) {
                throw new Exception("The name should contain only letters");
            }
            if (newValue.length() > 20) {
                throw new Exception("The name should be less than 20 characters");

            }
            myCustomer.firstNameProperty().set(newValue);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {
            LOGGER.warning("Error in First Name cell edit: " + e.getMessage());
            showError(e.getMessage());
            fxTableView.refresh();
        }

    }

    /**
     * Maneja la edición de la celda del apellido.
     *
     * @param cellName El evento de edición de celda.
     */
    private void handleLastNameCellEdit(TableColumn.CellEditEvent<Customer, String> cellName) {

        String newValue = cellName.getNewValue().trim();
        Customer myCustomer = cellName.getRowValue();

        try {
            if (newValue.isEmpty()) {
                throw new Exception("The name should be fill");
            }
            if (!newValue.matches(BASIC_MATCHER)) {
                throw new Exception("The name should contain only letters");
            }
            if (newValue.length() > 20) {
                throw new Exception("The name should be less than 20 characters");

            }
            myCustomer.lastNameProperty().set(newValue);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {
            LOGGER.warning("Error in First Name cell edit: " + e.getMessage());
            showError(e.getMessage());
            fxTableView.refresh();
        }

    }

    /**
     * Maneja la edición de la celda de la inicial del segundo nombre.
     *
     * @param cell El evento de edición de celda.
     */
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
            if (!newValue.matches(BASIC_MATCHER)) {

                throw new Exception("The name should contain only letter");
            }
            myCustomer.middleInitialProperty().set(newValue);
            client.edit_XML(myCustomer, myCustomer.getId());

        } catch (Exception e) {


            showError(e.getMessage());
            fxTableView.refresh();
        }
    }

    /**
     * Maneja la edición de la celda de la calle.
     *
     * @param streetCell El evento de edición de celda.
     */
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

            showError(e.getMessage());
            fxTableView.refresh();

        }

    }

    /**
     * Maneja la edición de la celda de la ciudad.
     *
     * @param cell El evento de edición de celda.
     */
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
            showError(e.getMessage());
            fxTableView.refresh();
        }

    }

    /**
     * Maneja la edición de la celda del estado.
     *
     * @param cell El evento de edición de celda.
     */
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
            showError(e.getMessage());
            fxTableView.refresh();
        }
    }

    /**
     * Maneja la edición de la celda del correo electrónico.
     *
     * @param cell El evento de edición de celda.
     */
    private void handleEmailCellEdit(TableColumn.CellEditEvent<Customer, String> cell) {
        String newEmail = cell.getNewValue().trim();
        String oldEmail = cell.getOldValue();
        Customer myCustomer = cell.getRowValue();
        try {

            if (newEmail.isEmpty()) {
                throw new Exception("Email must not be empty");
            }
            if (!newEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new Exception("Email format invalid");
            }
            if (newEmail.length() > 50) {
                throw new Exception("Email cannot exceed length of 50");
            }
            myCustomer.setEmail(newEmail);
            client.edit_XML(myCustomer, myCustomer.getId());
            LOGGER.info("Email updated");


        } catch (InternalServerErrorException ex) {
            LOGGER.severe("Error the email already exists");
            myCustomer.setEmail(oldEmail);
            showError("Error the email already exists");
            fxTableView.refresh();
        } catch (Exception e) {
            myCustomer.setEmail(oldEmail);
            showError(e.getMessage());
            fxTableView.refresh();
        }
    }

    /**
     * Maneja la edición de la celda de la contraseña.
     *
     * @param cell El evento de edición de celda.
     */
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
            showError(e.getMessage());
            fxTableView.refresh();
        }
    }

    /**
     * Maneja la edición de la celda del código postal.
     *
     * @param cell El evento de edición de celda.
     */
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
            showError("Zip code must be a number");
            LOGGER.severe("Error in Zip cell edit: " + e.getMessage());
            fxTableView.refresh();

        } catch (Exception e) {
            showError(e.getMessage());
            LOGGER.severe(e.getMessage());
            fxTableView.refresh();
        }
    }

    /**
     * Maneja la edición de la celda del teléfono.
     *
     * @param cell El evento de edición de celda.
     */
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
            showError("Phone number must be a number");
            LOGGER.severe("Error in Phone cell edit: " + e.getMessage());
            fxTableView.refresh();
        } catch (Exception e) {
            showError(e.getMessage());
            LOGGER.severe(e.getMessage());
            fxTableView.refresh();
            fxTableView.refresh();
        }
    }



    private void setupTableContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Edit Customer");
        MenuItem deleteItem = new MenuItem("Remove Customer");
        MenuItem addItem = new MenuItem("Add Customer");

        // Reutilizar metodos
        //Busca la fila seleccionada, y en la columna name la pone en edicion
        editItem.setOnAction(event -> fxTableView.edit(fxTableView.getSelectionModel().getSelectedIndex(), fxTcFirstName));
        addItem.setOnAction(this::handleCreate);
        deleteItem.setOnAction(this::handleDelete);

        contextMenu.getItems().addAll(addItem, new SeparatorMenuItem(), editItem, new SeparatorMenuItem(), deleteItem);

        // Asignar el menú a la tabla de forma permanente
        fxTableView.setContextMenu(contextMenu);
    }

    /**
     * Obtiene la etapa (Stage) actual.
     *
     * @return El Stage del usuario.
     */
    @Override
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Establece el cliente actual.
     *
     * @param customer El objeto Customer a establecer.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
