package crud_project.ui;

import crud_project.logic.AccountRESTClient;
import crud_project.model.Account;
import crud_project.model.AccountType;
import crud_project.model.Customer;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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

            tableAccounts.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused && tableAccounts.getEditingCell() != null) {
                    tableAccounts.edit(-1, null);
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
            new Alert(Alert.AlertType.ERROR, "Initialization Error: " + e.getMessage()).showAndWait();
        }
    }

    private void setupTable() {
        tableAccounts.setEditable(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colCreditLine.setCellValueFactory(new PropertyValueFactory<>("creditLine"));
        colBeginBalance.setCellValueFactory(new PropertyValueFactory<>("beginBalance"));
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("beginBalanceTimestamp"));

        colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        colDescription.setOnEditCommit(event -> {
            Account a = event.getRowValue();
            if (btnAddAccount.isSelected() && a != creatingAccount) {
                showWarning("Finish creating the new account first.");
                tableAccounts.refresh();
                return;
            }
            if (event.getNewValue() == null || event.getNewValue().trim().isEmpty()) {
                showWarning("Description cannot be empty.");
                tableAccounts.refresh();
            } else {
                a.setDescription(event.getNewValue());
                if (!btnAddAccount.isSelected()) {
                    saveOrUpdate(a);
                }
            }
        });

        colType.setCellFactory(ChoiceBoxTableCell.forTableColumn(AccountType.values()));
        colType.setOnEditCommit(event -> {
            Account a = event.getRowValue();
            if (!btnAddAccount.isSelected() || !a.equals(creatingAccount)) {
                showWarning("Type can only be modified for new accounts.");
                tableAccounts.refresh();
                return;
            }
            a.setType(event.getNewValue());
            if (a.getType() == AccountType.STANDARD) {
                a.setCreditLine(0.0);
            }
            updateBalance(a);
            tableAccounts.refresh();
        });

        colCreditLine.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colCreditLine.setOnEditCommit(event -> {
            Account a = event.getRowValue();
            if (btnAddAccount.isSelected() && !a.equals(creatingAccount)) {
                tableAccounts.refresh();
                return;
            }
            if (a.getType() != AccountType.CREDIT) {
                showWarning("Credit Line is only for CREDIT accounts.");
                a.setCreditLine(0.0);
            } else if (event.getNewValue() < 0) {
                showWarning("Credit Line must be positive.");
            } else {
                a.setCreditLine(event.getNewValue());
                updateBalance(a);
                if (!btnAddAccount.isSelected()) {
                    saveOrUpdate(a);
                }
            }
            tableAccounts.refresh();
        });

        colBeginBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colBeginBalance.setOnEditCommit(event -> {
            Account a = event.getRowValue();
            if (!btnAddAccount.isSelected() || !a.equals(creatingAccount)) {
                showWarning("Begin Balance is read-only for existing accounts.");
                tableAccounts.refresh();
                return;
            }
            if (event.getNewValue() < 0) {
                showWarning("Balance cannot be negative.");
            } else {
                a.setBeginBalance(event.getNewValue());
                updateBalance(a);
            }
            tableAccounts.refresh();
        });

        tableAccounts.editingCellProperty().addListener((obs, oldCell, newCell) -> {
            if (newCell == null && oldCell != null) {
                tableAccounts.refresh();
            }
        });
    }

    private void showWarning(String msg) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: red;");
    }

    private void updateBalance(Account a) {
        if (btnAddAccount.isSelected() && a.equals(creatingAccount)) {
            a.setBalance(a.getBeginBalance() + a.getCreditLine());
        }
    }

    private void loadAccountsData() {
        try {
            if (loggedCustomer == null || loggedCustomer.getId() == null) {
                return;
            }
            List<Account> accounts = restClient.findAccountsByCustomerId_XML(
                    new GenericType<List<Account>>() {
            },
                    loggedCustomer.getId().toString()
            );
            accountsData.setAll(accounts);
            tableAccounts.refresh();
        } catch (Exception e) {
            lblMessage.setText("Sync error.");
            accountsData.clear();
        }
    }

    private void handleAddAccount(ActionEvent event) {
        try {
            if (btnAddAccount.isSelected()) {
                btnAddAccount.setText("Confirm");
                btnCancelAccount.setDisable(false);
                setButtonsCreating(true);

                creatingAccount = new Account();
                creatingAccount.setId(generateUniqueId());
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

                tableAccounts.layout();
                int idx = accountsData.size() - 1;
                tableAccounts.getSelectionModel().select(idx);
                tableAccounts.scrollTo(idx);

                Platform.runLater(() -> {
                    tableAccounts.requestFocus();

                    tableAccounts.getSelectionModel().clearAndSelect(idx);

                    Platform.runLater(() -> {
                        tableAccounts.edit(idx, colDescription);
                    });
                });

            } else {
                if (creatingAccount.getDescription() == null || creatingAccount.getDescription().trim().isEmpty()) {
                    showWarning("Description is mandatory.");
                    btnAddAccount.setSelected(true);
                } else {
                    restClient.createAccount_XML(creatingAccount);
                    creatingAccount = null;
                    finishCreation();
                    lblMessage.setText("Account saved.");
                    lblMessage.setStyle("-fx-text-fill: green;");
                }
            }
        } catch (Exception e) {
            showWarning("Error: " + e.getMessage());
            btnAddAccount.setSelected(true);
        }
    }

    private Long generateUniqueId() {
        Random rdm = new Random();
        Long newId;
        boolean exists;
        do {
            newId = 1000000000L + (long) (rdm.nextDouble() * 8999999999L);
            exists = false;
            for (Account a : accountsData) {
                if (a.getId() != null && a.getId().equals(newId)) {
                    exists = true;
                    break;
                }
            }
        } while (exists);
        return newId;
    }

    private void handleCancelAccount(ActionEvent event) {
        if (creatingAccount != null) {
            accountsData.remove(creatingAccount);
        }
        creatingAccount = null;
        finishCreation();
    }

    private void finishCreation() {
        btnAddAccount.setText("Create Account");
        btnAddAccount.setSelected(false);
        btnCancelAccount.setDisable(true);
        setButtonsCreating(false);
        lblMessage.setText("");
        loadAccountsData();
        tableAccounts.refresh();
    }

    private void setButtonsCreating(boolean creating) {
        btnRefresh.setDisable(creating);
        btnLogOut.setDisable(creating);
        btnViewMovements.setDisable(creating);
        btnDeleteAccount.setDisable(creating);
    }

    private void saveOrUpdate(Account a) {
        try {
            restClient.updateAccount_XML(a);
            lblMessage.setText("Saved.");
            lblMessage.setStyle("-fx-text-fill: green;");
        } catch (Exception e) {
            loadAccountsData();
        }
    }

    private void handleWindowClose(WindowEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Close application?", yes, no);
        if (a.showAndWait().get() == yes) {
            Platform.exit();
            System.exit(0);
        } else {
            event.consume();
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
                showWarning("Delete failed.");
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
            FXMLLoader l = new FXMLLoader(getClass().getResource("/crud_project/ui/Movements.fxml"));
            Parent r = l.load();
            MovementController c = l.getController();
            c.setAccount(s);
            c.initStage(r);
            this.stage.close();
        } catch (Exception e) {
            showWarning("Navigation Error.");
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
            }
        }
    }

    public void setCustomer(Customer c) {
        this.loggedCustomer = c;
    }

    public Stage getStage() {
        return this.stage;
    }
}
