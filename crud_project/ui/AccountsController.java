package crud_project.ui;

import crud_project.model.Account;
import crud_project.model.AccountType;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

/**
 * Controlador para la gestión de Cuentas (WP1).
 */
public class AccountsController {

    private static final Logger LOGGER = Logger.getLogger("crud_project.ui");

    @FXML
    private TableView<Account> tableAccounts;
    @FXML
    private TableColumn<Account, Integer> colId;
    @FXML
    private TableColumn<Account, String> colDescription;
    @FXML
    private TableColumn<Account, AccountType> colType;
    @FXML
    private TableColumn<Account, Double> colBalance;
    @FXML
    private TableColumn<Account, Double> colCreditLine;

    @FXML
    private Button btnAddAccount, btnDeleteAccount, btnRefresh, btnBack;
    @FXML
    private Label lblMessage;

    private ObservableList<Account> accountsData;
    private Stage stage;

    public void init(Parent root) {
        Scene scene = new Scene(root);
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Account Management");
        
        // Configurar columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Columna Descripción
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescription.setOnEditCommit(this::handleEditDescription);

        // Columna CreditLine
        colCreditLine.setCellValueFactory(new PropertyValueFactory<>("creditLine"));
        colCreditLine.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colCreditLine.setOnEditCommit(this::handleEditCreditLine);

        // Cargar datos
        loadAccountsData();
        
        btnDeleteAccount.setOnAction(this::handleDeleteAccount);
        btnAddAccount.setOnAction(this::handleAddAccount);

        stage.show();
    }

    private void loadAccountsData() {
        // Aquí llamarás a tu AccountRESTClient
        accountsData = FXCollections.observableArrayList();
        tableAccounts.setItems(accountsData);
    }

    private void handleEditDescription(TableColumn.CellEditEvent<Account, String> event) {
        Account acc = event.getRowValue();
        acc.setDescription(event.getNewValue());
        // Aquí llamarías al REST Client (PUT)
        LOGGER.info("Description updated");
    }

    private void handleEditCreditLine(TableColumn.CellEditEvent<Account, Double> event) {
        Account acc = event.getRowValue();
        
        // REGLA DE NEGOCIO: Solo si es CREDIT
        if (acc.getType() == AccountType.CREDIT) {
            acc.setCreditLine(event.getNewValue());
            // Llamar al REST Client (PUT)
            lblMessage.setText("");
        } else {
            lblMessage.setText("Error: Credit Line can only be modified for CREDIT accounts.");
            tableAccounts.refresh(); // Revierte el cambio visual
        }
    }

    private void handleDeleteAccount(ActionEvent event) {
        Account selected = tableAccounts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // REGLA DE NEGOCIO: No borrar si tiene movimientos
            // Deberías consultar al servidor o verificar la integridad
            LOGGER.info("Attempting to delete account: " + selected.getId());
        }
    }

    private void handleAddAccount(ActionEvent event) {
        // Crear un objeto vacío, añadirlo a la lista y hacer scroll hasta él
        Account newAcc = new Account();
        newAcc.setDescription("New Description");
        newAcc.setType(AccountType.STANDARD);
        accountsData.add(newAcc);
        // Aquí llamarías al REST Client (POST)
    }
}