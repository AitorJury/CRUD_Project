package crud_project.ui;

import crud_project.logic.AccountRESTClient;
import crud_project.model.Account;
import crud_project.model.AccountType;
import crud_project.model.Customer;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
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
    private Button btnRefresh, btnLogOut, btnViewMovements, btnDeleteAccount, btnCancelAccount;
    @FXML
    private ToggleButton btnAddAccount;
    @FXML
    private Label lblMessage;

    private ObservableList<Account> accountsData = FXCollections.observableArrayList();
    private Stage stage;
    private Customer loggedCustomer;
    private final AccountRESTClient restClient = new AccountRESTClient();
    private Account creatingAccount;

    private final ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

    public void init(Parent root) {
        try {
            Scene scene = new Scene(root);
            this.stage = new Stage();
            this.stage.setScene(scene);
            this.stage.setTitle("My Accounts");
            this.stage.setResizable(false);

            setupTable();
            tableAccounts.setItems(accountsData);
            loadAccountsData();

            btnAddAccount.setOnAction(this::handleAddAccount);
            btnCancelAccount.setOnAction(this::handleCancelAccount);
            btnRefresh.setOnAction(e -> loadAccountsData());
            btnLogOut.setOnAction(this::handleLogOut);
            btnViewMovements.setOnAction(this::handleViewMovements);
            btnDeleteAccount.setOnAction(this::handleDeleteAccount);

            btnViewMovements.setDisable(true);
            btnDeleteAccount.setDisable(true);
            btnCancelAccount.setDisable(true);

            tableAccounts.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
                boolean disabled = (newSelection == null || btnAddAccount.isSelected());
                btnViewMovements.setDisable(disabled);
                btnDeleteAccount.setDisable(disabled);
            });

            this.stage.show();
        } catch (Exception e) {
            showErrorAlert("Error initializing: " + e.getMessage());
        }
    }

    private void setupTable() {
        try {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
            colCreditLine.setCellValueFactory(new PropertyValueFactory<>("creditLine"));
            colBeginBalance.setCellValueFactory(new PropertyValueFactory<>("beginBalance"));
            colTimestamp.setCellValueFactory(new PropertyValueFactory<>("beginBalanceTimestamp"));

            colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
            colDescription.setOnEditCommit(event -> {
                Account account = event.getRowValue();
                if (btnAddAccount.isSelected() && !account.equals(creatingAccount)) {
                    showErrorAlert("Cannot edit other accounts while creating a new one.");
                    tableAccounts.refresh();
                    return;
                }
                account.setDescription(event.getNewValue());
                if (!btnAddAccount.isSelected()) {
                    saveOrUpdate(account);
                }

            });

            colType.setCellFactory(ChoiceBoxTableCell.forTableColumn(AccountType.values()));
            colType.setOnEditCommit(event -> {
            Account account = event.getRowValue();
            if (btnAddAccount.isSelected() && !account.equals(creatingAccount)) {
                tableAccounts.refresh();
                return;
            }
            if (account.getId() != null && account.getId() > 0 && !btnAddAccount.isSelected()) {
                lblMessage.setText("Cannot change type of an existing account.");
                tableAccounts.refresh();
                return;
            }
            account.setType(event.getNewValue());
            if (account.getType() == AccountType.STANDARD) account.setCreditLine(0.0);
            tableAccounts.refresh();
        });

            colCreditLine.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            colCreditLine.setOnEditCommit(event -> {
            Account account = event.getRowValue();
            if (btnAddAccount.isSelected() && !account.equals(creatingAccount)) {
                tableAccounts.refresh();
                return;
            }
            if (account.getType() != AccountType.CREDIT) {
                lblMessage.setText("Credit line only for CREDIT accounts.");
                account.setCreditLine(0.0);
                tableAccounts.refresh();
                return;
            }
            if (event.getNewValue() == null || event.getNewValue() < 0) {
                lblMessage.setText("Credit line must be >= 0.");
                tableAccounts.refresh();
                return;
            }
            account.setCreditLine(event.getNewValue());
            if (!btnAddAccount.isSelected()) saveOrUpdate(account);
        });

            colBeginBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            colBeginBalance.setOnEditCommit(event -> {
            Account account = event.getRowValue();
            if (btnAddAccount.isSelected() && !account.equals(creatingAccount)) {
                tableAccounts.refresh();
                return;
            }
            if (account.getId() != null && account.getId() > 0 && !btnAddAccount.isSelected()) {
                lblMessage.setText("Cannot change begin balance of an existing account.");
                tableAccounts.refresh();
                return;
            }
            if (event.getNewValue() == null || event.getNewValue() < 0) {
                lblMessage.setText("Begin balance must be >= 0.");
                tableAccounts.refresh();
                return;
            }
            account.setBeginBalance(event.getNewValue());
        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAccountsData() {
        try {
            GenericType<List<Account>> accountListType = new GenericType<List<Account>>() {
            };
            List<Account> accounts = restClient.findAccountsByCustomerId_XML(
                    accountListType,
                    loggedCustomer.getId().toString()
            );
            accountsData.clear();
            accountsData.addAll(accounts);
            
            if (creatingAccount != null) accountsData.add(creatingAccount);

            tableAccounts.refresh();
            lblMessage.setText("");
        } catch (Exception e) {
            lblMessage.setText("Error loading data: " + e.getMessage());
        }
    }

    private void handleAddAccount(ActionEvent event) {
        try {
            if (btnAddAccount.isSelected()) {
                btnAddAccount.setText("Create");
                btnCancelAccount.setDisable(false);
                toggleControls(true);

                creatingAccount = new Account();
                creatingAccount.setId(generateUniqueId());
                creatingAccount.setBeginBalanceTimestamp(new Date());
                creatingAccount.setDescription("");
                creatingAccount.setType(AccountType.STANDARD);
                creatingAccount.setBalance(0.0);
                creatingAccount.setCreditLine(0.0);
                creatingAccount.setBeginBalance(0.0);

                accountsData.add(creatingAccount);
                tableAccounts.getSelectionModel().select(creatingAccount);
                tableAccounts.scrollTo(creatingAccount);
            } else {
                if (validateAccount(creatingAccount)) {
                    restClient.createAccount_XML(creatingAccount);
                    finishCreation("Account created successfully.");
                } else {
                    btnAddAccount.setSelected(true);
                }
            }
        } catch (BadRequestException e) {
            showErrorAlert("Logic Error: Server rejected the account data format.");
            btnAddAccount.setSelected(true);
        } catch (InternalServerErrorException e) {
            showErrorAlert("Database Error: Failed to save the new account.");
            btnAddAccount.setSelected(true);
        } catch (Exception e) {
            showErrorAlert("Network Error: Could not reach the server to save.");
            btnAddAccount.setSelected(true);
        }
    }

    private void handleCancelAccount(ActionEvent event) {
        if (creatingAccount != null) {
            accountsData.remove(creatingAccount);
            creatingAccount = null;
            finishCreation("Creation cancelled.");
        }
    }

    private void finishCreation(String message) {
        btnAddAccount.setText("Create Account");
        btnAddAccount.setSelected(false);
        btnCancelAccount.setDisable(true);
        toggleControls(false);
        loadAccountsData();
        lblMessage.setText(message);
    }

    private void toggleControls(boolean creating) {
        btnRefresh.setDisable(creating);
        btnLogOut.setDisable(creating);
        btnViewMovements.setDisable(creating);
        btnDeleteAccount.setDisable(creating);
    }

    private boolean validateAccount(Account account) {
        if (account.getDescription() == null || account.getDescription().trim().isEmpty()) {
            showErrorAlert("Validation Error: Description is mandatory.");
            return false;
        }
        if (account.getBeginBalance() < 0) {
            showErrorAlert("Validation Error: Begin Balance cannot be negative.");
            return false;
        }
        if (account.getType() == AccountType.CREDIT && account.getCreditLine() < 0) {
            showErrorAlert("Validation Error: Credit Line must be 0 or positive.");
            return false;
        }
        return true;

    }

    private Long generateUniqueId() {
        Random rdm = new Random();
        Long newId;
        boolean exists;
        do {
            newId = 1000000L + rdm.nextInt(9000000);
            exists = false;
            for (Account account : accountsData) {
                if (account.getId().equals(newId)) {
                    exists = true;
                    break;
                }
            }
        } while (exists);
        return newId;
    }

    private void handleDeleteAccount(ActionEvent event) {
        Account selected = tableAccounts.getSelectionModel().getSelectedItem();
        if (selected == null || btnAddAccount.isSelected()) {
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
                tableAccounts.refresh();
            } catch (NotFoundException e) {
                showErrorAlert("Sync Error: Account no longer exists on the server.");
            } catch (Exception e) {
                showErrorAlert("Delete Error: Failed to process the request.");
            }
        }
    }

    @FXML
    private void handleViewMovements(ActionEvent event) {
        Account selected = tableAccounts.getSelectionModel().getSelectedItem();
        if (selected != null && !btnAddAccount.isSelected()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/Movements.fxml"));
                Parent root = loader.load();
                MovementController controller = loader.getController();
                controller.setAccount(selected);
                controller.initStage(root);
                this.stage.close();
            } catch (Exception e) {
                showErrorAlert("Error opening movements: " + e.getMessage());
            }
        }
    }

    private void handleLogOut(ActionEvent event) {
        if (btnAddAccount.isSelected()) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out?", yes, no);
        if (alert.showAndWait().get() == yes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/SignIn.fxml"));
                Parent root = loader.load();
                SignInController controller = loader.getController();
                controller.initStage(stage, root);
                restClient.close();
            } catch (Exception e) {
                showErrorAlert("Error returning to sign in.");
            }
        }
    }

    private void saveOrUpdate(Account account) {
        try {
            if (account.getId() != null && account.getId() > 0) {
                restClient.updateAccount_XML(account);
                lblMessage.setText("Account updated.");
            }
        } catch (BadRequestException e) {
            showErrorAlert("Input Error: The server rejected the updated data.");
            loadAccountsData();
        } catch (Exception e) {
            showErrorAlert("Update Error: Could not synchronize changes with the server.");
            loadAccountsData();
        }
    }

    private void showErrorAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }

    public void setCustomer(Customer customer) {
        this.loggedCustomer = customer;
    }

    public Stage getStage() {
        return this.stage;
    }
}
