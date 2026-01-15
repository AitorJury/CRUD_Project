package crud_project.ui;

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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
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
    private Button btnRefresh, btnLogOut, btnViewMovements, btnDeleteAccount;
    @FXML
    private ToggleButton btnAddAccount;
    @FXML
    private Label lblMessage;

    private ObservableList<Account> accountsData;
    private Stage stage;
    private Customer loggedCustomer;
    private final AccountRESTClient restClient = new AccountRESTClient();

    private final ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

    public void init(Parent root) {
        try {
            Scene scene = new Scene(root);
            this.stage = new Stage();
            this.stage.setScene(scene);
            this.stage.setTitle("My Accounts");

            // FALTA CERRAR LA APP CON LA X DE LA VENTANA
            setupTable();
            loadAccountsData();

            btnAddAccount.setOnAction(this::handleAddAccount);
            btnRefresh.setOnAction(e -> loadAccountsData());
            btnLogOut.setOnAction(this::handleLogOut);
            btnViewMovements.setOnAction(this::handleViewMovements);
            btnDeleteAccount.setOnAction(this::handleDeleteAccount);

            btnViewMovements.setDisable(true);
            btnDeleteAccount.setDisable(true);

            tableAccounts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean disabled;
                if (newSelection == null) {
                    disabled = true;
                } else {
                    disabled = false;
                }
                btnViewMovements.setDisable(disabled);
                btnDeleteAccount.setDisable(disabled);
            });

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
        colDescription.setOnEditCommit(new EventHandler<CellEditEvent<Account, String>>() {
            @Override
            public void handle(CellEditEvent<Account, String> event) {
                Account acc = event.getRowValue();
                acc.setDescription(event.getNewValue());
                saveOrUpdate(acc);
            }
        });

        colType.setCellFactory(ChoiceBoxTableCell.forTableColumn(AccountType.values()));
        colType.setOnEditCommit(new EventHandler<CellEditEvent<Account, AccountType>>() {
            @Override
            public void handle(CellEditEvent<Account, AccountType> event) {
                Account acc = event.getRowValue();
                if (acc.getId() == null || acc.getId() <= 0) {
                    acc.setType(event.getNewValue());
                    saveOrUpdate(acc);
                } else {
                    lblMessage.setText("Cannot change type of an existing account.");
                    tableAccounts.refresh();
                }
            }
        });

        colCreditLine.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colCreditLine.setOnEditCommit(new EventHandler<CellEditEvent<Account, Double>>() {
            @Override
            public void handle(CellEditEvent<Account, Double> event) {
                Account acc = event.getRowValue();
                if ((acc.getId() == null || acc.getId() <= 0) && acc.getType() == AccountType.CREDIT) {
                    acc.setCreditLine(event.getNewValue());
                    saveOrUpdate(acc);
                } else {
                    lblMessage.setText("Credit line only for NEW accounts of type CREDIT.");
                    tableAccounts.refresh();
                }
            }
        });
        
        colBeginBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colBeginBalance.setOnEditCommit(new EventHandler<CellEditEvent<Account, Double>>() {
            @Override
            public void handle(CellEditEvent<Account, Double> event) {
                Account acc = event.getRowValue();
                if (acc.getId() == null || acc.getId() <= 0) {
                    acc.setBeginBalance(event.getNewValue());
                    saveOrUpdate(acc);
                } else {
                    lblMessage.setText("Cannot change begin balance of an existing account.");
                    tableAccounts.refresh();
                }
            }
        });
    }

    private void loadAccountsData() {
        try {
            GenericType<List<Account>> accountListType = new GenericType<List<Account>>() {
            };

            List<Account> accounts = restClient.findAccountsByCustomerId_XML(
                    accountListType,
                    loggedCustomer.getId().toString()
            );

            accountsData = FXCollections.observableArrayList(accounts);
            tableAccounts.setItems(accountsData);
            lblMessage.setText("");
            LOGGER.info("Accounts loaded for customer: " + loggedCustomer.getId());

        } catch (Exception e) {
            lblMessage.setText("Error loading data: " + e.getMessage());
        }
    }

    private void handleAddAccount(ActionEvent event) {
        try {
            Account newAcc = new Account(0L, AccountType.STANDARD, null,
                    0.0, 0.0, 0.0, new Date());

            restClient.createAccount_XML(newAcc);

            loadAccountsData();
            lblMessage.setText("Account created successfully.");
        } catch (Exception e) {
            showErrorAlert("Could not create account: " + e.getMessage());
        }
    }

    private void handleEditDescription(TableColumn.CellEditEvent<Account, String> event) {
        Account acc = event.getRowValue();
        acc.setDescription(event.getNewValue());
        saveOrUpdate(acc);
    }

    private void handleDeleteAccount(ActionEvent event) {
        Account selected = tableAccounts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        if (selected.getMovements() != null && !selected.getMovements().isEmpty()) {
            showErrorAlert("Account cannot be deleted: it has linked movements.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this account?", yes, no);
        if (alert.showAndWait().get() == yes) {
            try {
                restClient.removeAccount(selected.getId().toString());
                accountsData.remove(selected);
                lblMessage.setText("Account deleted.");
            } catch (Exception e) {
                showErrorAlert("Error during deletion: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewMovements(ActionEvent event) {
        Account selected = tableAccounts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/Movements.fxml"));
                Parent root = loader.load();

                MovementController controller = loader.getController();

                controller.setAccount(selected);
                controller.initStage(root);

                this.stage.close();
            } catch (Exception e) {
                showErrorAlert("Error opening movements: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleLogOut(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out?", yes, no);
        if (alert.showAndWait().get() == yes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/SignIn.fxml"));
                Parent root = loader.load();
                SignInController controller = loader.getController();
                controller.initStage(stage, root);
            } catch (Exception e) {
                showErrorAlert("Error returning to login.");
            }
        }
    }

    private void saveOrUpdate(Account acc) {
        try {
            if (acc.getId() != null && acc.getId() > 0) {
                restClient.updateAccount_XML(acc);
                lblMessage.setText("Account updated.");
            }
        } catch (Exception e) {
            showErrorAlert("Error updating: " + e.getMessage());
            tableAccounts.refresh();
        }
    }

    private void showErrorAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    public void setCustomer(Customer customer) {
        this.loggedCustomer = customer;
    }
}
