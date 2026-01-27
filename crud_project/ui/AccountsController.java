package crud_project.ui;

import crud_project.logic.AccountRESTClient;
import crud_project.model.Account;
import crud_project.model.AccountType;
import crud_project.model.Customer;
import java.util.*;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    private Button btnRefresh, btnLogOut, btnViewMovements, btnDeleteAccount, btnCancelAccount;
    @FXML
    private ToggleButton btnAddAccount;
    @FXML
    private Label lblMessage;

    private final ObservableList<Account> accountsData = FXCollections.observableArrayList();
    private Stage stage;
    private Customer loggedCustomer;
    private final AccountRESTClient restClient = new AccountRESTClient();
    private Account creatingAccount;

    private final ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

    public void init(Parent root) {
        try {
            this.stage = new Stage();
            this.stage.setScene(new Scene(root));
            this.stage.setTitle("My Accounts");
            this.stage.setResizable(false);
            this.stage.setOnCloseRequest(this::handleWindowClose);

            setupTable();
            tableAccounts.setItems(accountsData);

            tableAccounts.focusedProperty().addListener((obs, oldV, newV) -> {
                if (!newV && tableAccounts.getEditingCell() != null) {
                    // tableAccounts.edit(-1, null);
                }
            });

            if (loggedCustomer != null) {
                loadAccountsData();
            }

            btnAddAccount.setOnAction(this::handleAddAccount);
            btnCancelAccount.setOnAction(this::handleCancelAccount);
            btnRefresh.setOnAction(e -> loadAccountsData());
            btnLogOut.setOnAction(this::handleLogOut);
            btnViewMovements.setOnAction(this::handleViewMovements);
            btnDeleteAccount.setOnAction(this::handleDeleteAccount);

            this.stage.show();
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private void setupTable() {
        tableAccounts.setEditable(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescription.setEditable(true);
        colDescription.setOnEditStart(event -> {
            if (btnAddAccount.isSelected() && event.getRowValue() != creatingAccount) {
                cancelEdit("Finish creating the new account first.");
            }
        });
        colDescription.setOnEditCommit(this::handleDescriptionEdit);

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(ChoiceBoxTableCell.forTableColumn(AccountType.values()));
        colType.setEditable(true);
        colType.setOnEditStart(event -> {
            if (!btnAddAccount.isSelected() || event.getRowValue() != creatingAccount) {
                cancelEdit("Account type cannot be modified for existing accounts.");
            }
        });
        colType.setOnEditCommit(this::handleTypeEdit);

        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colBalance.setEditable(false);

        colCreditLine.setCellValueFactory(new PropertyValueFactory<>("creditLine"));
        colCreditLine.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colCreditLine.setEditable(true);
        colCreditLine.setOnEditStart(event -> {
            Account a = event.getRowValue();
            if (!btnAddAccount.isSelected() || a != creatingAccount) {
                cancelEdit("Credit line is immutable for existing accounts.");
            } else if (a.getType() != AccountType.CREDIT) {
                cancelEdit("Credit line only applicable to CREDIT accounts.");
            }
        });
        colCreditLine.setOnEditCommit(this::handleCreditLineEdit);

        colBeginBalance.setCellValueFactory(new PropertyValueFactory<>("beginBalance"));
        colBeginBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colBeginBalance.setEditable(true);
        colBeginBalance.setOnEditStart(event -> {
            if (!btnAddAccount.isSelected() || event.getRowValue() != creatingAccount) {
                cancelEdit("Initial balance cannot be modified.");
            }
        });
        colBeginBalance.setOnEditCommit(this::handleBeginBalanceEdit);

        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("beginBalanceTimestamp"));
    }

    private void handleDescriptionEdit(TableColumn.CellEditEvent<Account, String> event) {
        Account a = event.getRowValue();
        String val = event.getNewValue();
        if (btnAddAccount.isSelected() && a != creatingAccount) {
            tableAccounts.refresh();
            return;
        }
        if (val == null || val.trim().isEmpty()) {
            showWarning("Description is obligatory.");
            tableAccounts.refresh();
        } else {
            a.setDescription(val);
            lblMessage.setText("");
            if (!btnAddAccount.isSelected()) {
                saveOrUpdate(a);
            }
        }
    }

    private void handleTypeEdit(TableColumn.CellEditEvent<Account, AccountType> event) {
        Account a = event.getRowValue();
        if (!btnAddAccount.isSelected()) {
            showWarning("Account type is immutable.");
            tableAccounts.refresh();
            return;
        }
        a.setType(event.getNewValue());
        if (a.getType() == AccountType.STANDARD) {
            a.setCreditLine(0.0);
        }
        updateBalance(a);
        tableAccounts.refresh();
    }

    private void handleCreditLineEdit(TableColumn.CellEditEvent<Account, Double> event) {
        Account a = event.getRowValue();
        Double val = event.getNewValue();
        if (a.getType() != AccountType.CREDIT) {
            showWarning("Credit line only for CREDIT accounts.");
            a.setCreditLine(0.0);
        } else if (val == null || val < 0) {
            showWarning("Credit Line must be 0 or positive.");
            tableAccounts.refresh();
        } else {
            a.setCreditLine(val);
            updateBalance(a);
            if (!btnAddAccount.isSelected()) {
                saveOrUpdate(a);
            }
        }
    }

    private void handleBeginBalanceEdit(TableColumn.CellEditEvent<Account, Double> event) {
        Account a = event.getRowValue();
        Double val = event.getNewValue();
        if (!btnAddAccount.isSelected()) {
            showWarning("Initial balance is immutable.");
        } else if (val == null || val < 0) {
            showWarning("Balance cannot be negative.");
            tableAccounts.refresh();
        } else {
            a.setBeginBalance(val);
            updateBalance(a);
        }
    }

    private void handleAddAccount(ActionEvent event) {
        try {
            if (btnAddAccount.isSelected()) {
                btnAddAccount.setText("Confirm");
                btnCancelAccount.setDisable(false);
                setButtonsCreating(true);

                creatingAccount = new Account();
                creatingAccount.setId(1000000000L + (long) (new Random().nextDouble() * 8999999999L));
                Set<Customer> c = new HashSet<>();
                c.add(loggedCustomer);
                creatingAccount.setCustomers(c);
                creatingAccount.setBeginBalanceTimestamp(new Date());
                creatingAccount.setDescription("");
                creatingAccount.setType(AccountType.STANDARD);
                creatingAccount.setBalance(0.0);
                creatingAccount.setCreditLine(0.0);
                creatingAccount.setBeginBalance(0.0);

                accountsData.add(creatingAccount);
                int idx = accountsData.size() - 1;
                tableAccounts.getSelectionModel().select(idx);
                tableAccounts.scrollTo(idx);

                Platform.runLater(() -> {
                    tableAccounts.requestFocus();
                    tableAccounts.edit(idx, colDescription);
                });
            } else {
                if (creatingAccount.getDescription().trim().isEmpty()) {
                    showWarning("Description is obligatory.");
                    btnAddAccount.setSelected(true);
                } else {
                    restClient.createAccount_XML(creatingAccount);
                    creatingAccount = null;
                    finishCreation("Account created.");
                }
            }
        } catch (Exception e) {
            showWarning("Error: " + e.getMessage());
            btnAddAccount.setSelected(true);
        }
    }

    private void handleCancelAccount(ActionEvent event) {
        if (creatingAccount != null) {
            accountsData.remove(creatingAccount);
        }
        creatingAccount = null;
        finishCreation("Cancelled");
    }

    private void cancelEdit(String message) {
        Platform.runLater(() -> {
            tableAccounts.edit(-1, null);
            showWarning(message);
            tableAccounts.refresh();
        });
    }

    private void finishCreation(String message) {
        btnAddAccount.setText("Create Account");
        btnAddAccount.setSelected(false);
        btnCancelAccount.setDisable(true);
        setButtonsCreating(false);
        loadAccountsData();
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: green;");
    }

    private void loadAccountsData() {
        try {
            List<Account> accounts = restClient.findAccountsByCustomerId_XML(
                    new GenericType<List<Account>>() {
            },
                    loggedCustomer.getId().toString()
            );
            accountsData.setAll(accounts);
            tableAccounts.refresh();
        } catch (Exception e) {
            showWarning("Server sync failed.");
        }
    }

    private void saveOrUpdate(Account a) {
        try {
            restClient.updateAccount_XML(a);
            lblMessage.setText("Saved.");
            lblMessage.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            showError("Update failed.");
            loadAccountsData();
        }
    }

    private void updateBalance(Account a) {
        if (btnAddAccount.isSelected() && a == creatingAccount) {
            a.setBalance(a.getBeginBalance() + a.getCreditLine());
        }
    }

    private void setButtonsCreating(boolean creating) {
        btnRefresh.setDisable(creating);
        btnLogOut.setDisable(creating);
        btnViewMovements.setDisable(creating);
        btnDeleteAccount.setDisable(creating);
    }

    private void handleWindowClose(WindowEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Exit application?", yes, no);
        if (a.showAndWait().get() == yes) {
            Platform.exit();
            System.exit(0);
        } else {
            event.consume();
        }
    }

    private void handleLogOut(ActionEvent event) {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Logout?", yes, no).showAndWait().get() == yes) {
            try {
                FXMLLoader l = new FXMLLoader(getClass().getResource("/crud_project/ui/SignIn.fxml"));
                Parent r = l.load();
                SignInController c = l.getController();
                c.initStage(this.stage, r);
                restClient.close();
            } catch (Exception e) {
                showError("Logout error.");
            }
        }
    }

    private void handleDeleteAccount(ActionEvent event) {
        Account s = tableAccounts.getSelectionModel().getSelectedItem();
        if (s == null) {
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Delete this account?", yes, no).showAndWait().get() == yes) {
            try {
                restClient.removeAccount(s.getId().toString());
                loadAccountsData();
            } catch (Exception e) {
                showError("Delete failed.");
            }
        }
    }

    @FXML
    private void handleViewMovements(ActionEvent event) {
        Account s = tableAccounts.getSelectionModel().getSelectedItem();
        if (s == null) {
            return;
        }
        try {
            FXMLLoader l = new FXMLLoader(getClass().getResource("/crud_project/ui/Movement.fxml"));
            Parent r = l.load();
            MovementController c = l.getController();
            c.setAccount(s);
            c.initStage(r);
            this.stage.close();
        } catch (Exception e) {
            showError("Navigation Error.");
        }
    }

    private void showWarning(String msg) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: red;");
    }

    private void showError(String m) {
        new Alert(Alert.AlertType.ERROR, m, ButtonType.OK).showAndWait();
    }

    public void setCustomer(Customer c) {
        this.loggedCustomer = c;
    }

    public Stage getStage() {
        return this.stage;
    }
}
