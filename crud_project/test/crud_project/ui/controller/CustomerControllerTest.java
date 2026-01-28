package crud_project.ui.controller;

import crud_project.AppCRUD;
import crud_project.model.Customer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.ButtonMatchers.isDefaultButton;

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
        //new AppCRUD().start(stage);
        new crud_project.AppCRUD().start(stage);

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

        //var needed to test
        table = lookup("#fxTableView").queryTableView();
        btnDelete = lookup("#fxBtnDelete").queryButton();
        btnAdd = lookup("#fxBtnNewCustomer").queryButton();
        btnExit = lookup("#fxBtnExit").queryButton();


    }

    @Test
    public void test_delete_customer_success() {


        verifyThat("#fxBtnDelete", isDisabled());
        //Size of the table
        int rowsCount = table.getItems().size();
        assertNotEquals("Table has no data: Cannot test", 0, rowsCount);
        Node row = lookup(".table-row-cell").nth(2).query();
        assertNotNull("Row is null: table has not that row. ", row);
        clickOn(row);

        Customer selected = table.getSelectionModel().getSelectedItem();
        String firstName = selected.getFirstName();

        verifyThat("#fxBtnDelete", isEnabled());
        clickOn("#fxBtnDelete");
        verifyThat("Deleting user: " + firstName , isVisible());

        clickOn("Sí");
        //assertEquals("The row has not been deleted", rowsCount - 1, table.getItems().size());

    }
}