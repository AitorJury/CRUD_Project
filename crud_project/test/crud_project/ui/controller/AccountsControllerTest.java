package crud_project.ui.controller;

import crud_project.AppCRUD;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import javafx.scene.input.KeyCode;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountsControllerTest extends ApplicationTest {

    private TableView table;

    @Override
    public void start(Stage stage) throws Exception {
        new AppCRUD().start(stage);
    }

    @Before
    public void test_init_window() {
        verifyThat("#txtEmail", isVisible());
        clickOn("#txtEmail").write("aitor@gmail.com");
        clickOn("#txtPassword").write("abcd*1234");
        clickOn("#btnSignIn");
        verifyThat("#tableAccounts", isVisible());
        table = lookup("#tableAccounts").queryTableView();
    }

    @After
    public void close_window() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    @Test
    public void test_A_initial_state() {
        verifyThat("#btnAddAccount", isEnabled());
        verifyThat("#btnRefresh", isEnabled());
        verifyThat("#btnLogOut", isEnabled());
    }

    @Test
    public void test_B_create_account_cancel() {
        clickOn("#btnAddAccount");
        verifyThat("#btnAddAccount", hasText("Confirm"));
        clickOn("#btnCancelAccount");
        verifyThat("#btnAddAccount", hasText("Create Account"));
    }

    @Test
    public void test_C_delete_account_fail_movements() {
        clickOn("#tableAccounts");
        Node row = lookup(".table-row-cell").nth(0).query();
        clickOn(row);
        clickOn("#btnDeleteAccount");
        verifyThat(".dialog-pane", isVisible());
        clickOn(".button");
    }

    @Test
    public void test_D_create_account_success() {
        int rowsBefore = table.getItems().size();
        clickOn("#btnAddAccount");

        Node cell = lookup(".table-cell").nth(rowsBefore * table.getColumns().size() + 1).query();
        doubleClickOn(cell);
        write("Cuenta Tests");
        type(KeyCode.ENTER);

        clickOn("#btnAddAccount");

        verifyThat(".dialog-pane", isVisible());
        clickOn(".button");

        assertEquals(rowsBefore + 1, table.getItems().size());
    }

    @Test
    public void test_E_delete_new_account_success() {
        int rowsCurrent = table.getItems().size();
        clickOn("#tableAccounts");

        Node lastRow = lookup(".table-row-cell").nth(rowsCurrent - 1).query();
        clickOn(lastRow);

        clickOn("#btnDeleteAccount");

        verifyThat(".dialog-pane", isVisible());
        clickOn("Yes");

        verifyThat(".dialog-pane", isVisible());
        clickOn(".button");

        assertEquals(rowsCurrent - 1, table.getItems().size());
    }

    @Test
    public void test_F_navigation_movements() {
        clickOn("#tableAccounts");
        Node row = lookup(".table-row-cell").nth(0).query();
        clickOn(row);
        clickOn("#btnViewMovements");
        verifyThat("#tbMovement", isVisible());
    }

    @Test
    public void test_G_logout() {
        clickOn("#btnLogOut");
        clickOn("Yes");
        verifyThat("#btnSignIn", isVisible());
    }
}
