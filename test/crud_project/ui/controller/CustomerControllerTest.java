package crud_project.ui.controller;

import crud_project.AppCRUD;
import crud_project.model.Customer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.testfx.api.FxAssert.verifyThat;

import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.matcher.base.NodeMatchers.*;

/**
 *
 * @author juan
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerControllerTest extends ApplicationTest {
    private TableView<Customer> table;
    private Button btnDelete;
    private Button btnAdd;
    private Button btnExit;

    @Override
    public void start(Stage stage) throws Exception {
        //Method to start the application
        new AppCRUD().start(stage);
        //var needed to test
        table = lookup("#fxTableView").queryTableView();
        btnDelete = lookup("#fxBtnDelete").queryButton();
        btnAdd = lookup("#fxBtnNewCustomer").queryButton();
        btnExit = lookup("#fxBtnExit").queryButton();
    }

    @Before
    public void test_init_window() {
        verifyThat("#txtEmail", isVisible());
        clickOn("#txtEmail");
        write("admin");
        clickOn("#txtPassword");
        write("admin");
        clickOn("#btnSignIn");
        verifyThat("#mainPane", isVisible());
    }

    @Test
    public void test_delete_customer_success() {

        verifyThat("#btnDelete", isDisabled());
        //Size of the table
        int rowsCount = table.getItems().size();
        assertNotEquals("Table has no data: Cannot test", 0, rowsCount);
        Node row = lookup(".table-row-cell").nth(0).query();
        assertNotNull("Row is null: table has not that row. ", row);
        clickOn(row);
        verifyThat("#btnDelete", isEnabled());
        clickOn("#btnDelete");
        verifyThat("Delete user?", isVisible());
    }
}