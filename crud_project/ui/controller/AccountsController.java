package crud_project.ui.controller;

import crud_project.logic.AccountRESTClient;
import crud_project.model.Account;
import crud_project.model.AccountType;
import crud_project.model.Customer;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javax.ws.rs.core.GenericType;

public class AccountsController {
    private static final Logger LOGGER = Logger.getLogger("crud_project.ui");

    @FXML
    private TableView<Account> tableAccounts;
    @FXML
    private TableColumn<Account, Long> colId;
    @FXML
    private TableColumn<Account, String> colDescription;
    @FXML
    private TableColumn<Account, AccountType> colType;
    @FXML
    private TableColumn<Account, Double> colBalance, colCreditLine, colBeginBalance;
    @FXML
    private TableColumn<Account, Date> colTimestamp;
    @FXML
    private Button btnAddAccount, btnRefresh, btnLogOut;
    @FXML
    private Label lblMessage;

    private ObservableList<Account> accountsData;
    private Stage stage;
    private Customer loggedCustomer;
    private final AccountRESTClient restClient = new AccountRESTClient();
    
    private final ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

    public void setCustomer(Customer customer) {
        this.loggedCustomer = customer;
    }

    public void init(Parent root) {
        try {
            Scene scene = new Scene(root);
            this.stage = new Stage();
            this.stage.setScene(scene);
            this.stage.setTitle("Account Management");

            setupTable();
            setupContextMenu();
            loadAccountsData();

            btnAddAccount.setOnAction(this::handleAddAccount);
            btnRefresh.setOnAction(e -> loadAccountsData());
            btnLogOut.setOnAction(this::handleLogOut);

            this.stage.show();
        } catch (Exception e) {
            showErrorAlert("Error initializing: " + e.getMessage());
        }
    }

    private void setupTable() {
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colCreditLine.setCellValueFactory(new PropertyValueFactory<>("creditLine"));
        colBeginBalance.setCellValueFactory(new PropertyValueFactory<>("beginBalance"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("beginBalanceTimestamp"));
        
        colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescription.setOnEditCommit(this::handleEditDescription);

        colType.setCellFactory(ChoiceBoxTableCell.forTableColumn(AccountType.values()));
        colType.setOnEditCommit(event -> {
            Account acc = event.getRowValue();
            if (acc.getId() == null || acc.getId() <= 0) {
                acc.setType(event.getNewValue());
                saveOrUpdate(acc);
            } else {
                lblMessage.setText("Cannot change type of an existing account.");
                tableAccounts.refresh(); 
            }
        });

        colCreditLine.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colCreditLine.setOnEditCommit(event -> {
            Account acc = event.getRowValue();
            if ((acc.getId() == null || acc.getId() <= 0) && acc.getType() == AccountType.CREDIT) {
                acc.setCreditLine(event.getNewValue());
                saveOrUpdate(acc);
            } else {
                lblMessage.setText("Credit line only for NEW accounts of type CREDIT.");
                tableAccounts.refresh();
            }
        });
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Account");
        delete.setOnAction(this::handleDeleteAccount);
        contextMenu.getItems().add(delete);
        tableAccounts.setContextMenu(contextMenu);
    }

    private void loadAccountsData() {
        try {
            GenericType<List<Account>> accountListType = new GenericType<List<Account>>() {};
            List<Account> accounts = (List<Account>) restClient.findAccountsByCustomerId_XML(
            accountListType.getRawType(), loggedCustomer.getId().toString());
            
            accountsData = FXCollections.observableArrayList(accounts);
            tableAccounts.setItems(accountsData);
            lblMessage.setText("Data loaded.");
        } catch (Exception e) {
            lblMessage.setText("Error loading data.");
        }
    }

    private void handleAddAccount(ActionEvent event) {
        Account newAcc = new Account(0L, AccountType.STANDARD, "New Description",
                0.0, 0.0, 0.0, new Date());
        
        accountsData.add(newAcc);
        tableAccounts.getSelectionModel().select(newAcc);
        lblMessage.setText("New row added. Right-click to Save.");
    }

    private void handleEditDescription(TableColumn.CellEditEvent<Account, String> event) {
        Account acc = event.getRowValue();
        acc.setDescription(event.getNewValue());
        saveOrUpdate(acc);
    }

    private void handleDeleteAccount(ActionEvent event) {
        Account selected = tableAccounts.getSelectionModel().getSelectedItem();
        
        if (selected == null) return;

        if (selected.getMovements() != null && !selected.getMovements().isEmpty()) {
            showErrorAlert("It cannot be deleted: The account has linked transactions.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this account?", yes, no);
        if (alert.showAndWait().get() == yes) {
            try {
                restClient.removeAccount(selected.getId().toString());
                accountsData.remove(selected);
                lblMessage.setText("Account successfully deleted.");
            } catch (Exception e) {
                showErrorAlert("Server error while deleting.");
            }
        }
    }

    private void handleLogOut(ActionEvent event) {
        try {
            LOGGER.info("Log out button clicked.");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Log Out");
            alert.setHeaderText("Confirm Log Out");
            alert.setContentText("Are you sure you want to log out?");
        
            alert.getButtonTypes().setAll(yes, no);
            ButtonType result = alert.showAndWait().get();
        
            if (result == yes) {
                LOGGER.info("User confirmed log out.");
            
                if (restClient != null) {
                    restClient.close();
                }
            
                this.stage.close();
            
            } else {
                LOGGER.info("Log out cancelled.");
            }

        } catch (Exception e) {
            LOGGER.severe("Error: " + e.getMessage());
            showErrorAlert("An error occurred during log out.");
        }
    }

    private void saveOrUpdate(Account acc) {
        try {
            if (acc.getId() == null || acc.getId() <= 0) {
                restClient.createAccount_XML(acc);
                lblMessage.setText("New account created.");
            } else {
                restClient.updateAccount_XML(acc);
                lblMessage.setText("Changes saved.");
            }
            loadAccountsData();
        } catch (Exception e) {
            showErrorAlert("Error saving: " + e.getMessage());
            tableAccounts.refresh();
        }
    }
    
    private void showErrorAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    public Stage getStage() { return this.stage; }
}
