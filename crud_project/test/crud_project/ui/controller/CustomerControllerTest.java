package crud_project.ui.controller;

import crud_project.AppCRUD;
import crud_project.logic.CustomerRESTClient;
import crud_project.model.Customer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.glassfish.jersey.internal.inject.Custom;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

import org.testfx.framework.junit.ApplicationTest;

import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        new AppCRUD().start(stage);

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

        // 1. Buscar el índice del primer cliente que se pueda borrar que no tenga cuentas para que no falle el test
        int rowIndex = 0;
        for (int i = 0; i < table.getItems().size(); i++) {
            Customer customer = table.getItems().get(i);
            //No es admin Y no tiene cuentas (o la lista está vacía)
            if (!customer.getFirstName().equalsIgnoreCase("admin") &&
                    (customer.getAccounts() == null || customer.getAccounts().isEmpty())) {
                rowIndex = i;
                break;
            }
        }
        verifyThat(btnDelete, isDisabled());
        //Tamaño de la tabla
        int rowsCount = table.getItems().size();
        assertNotEquals("Table has no data: Cannot test", 0, rowsCount);
        //se usa rowIndex para asegurar que haga clic en un customer que no tenga cuentas
        Node row = lookup(".table-row-cell").nth(rowIndex).query();
        assertNotNull("Row is null: table has not that row. ", row);
        clickOn(row);

        Customer selected = table.getSelectionModel().getSelectedItem();
        String firstName = selected.getFirstName();

        verifyThat(btnDelete, isEnabled());
        clickOn(btnDelete);
        verifyThat("Deleting user: " + firstName, isVisible());

        clickOn("Sí");
        assertEquals("The row has not been deleted", rowsCount - 1, table.getItems().size());

    }

    @Test
    public void test_add_customer_success() {

        Customer customer = new Customer(
                new Random().nextLong(),
                "Paco",
                "Perez",
                "M",
                "Avenida America",
                "Madrid",
                "Madrid",
                28052,
                615487796L,
                "name@" + System.currentTimeMillis() + ".com",
                "clave$%&"
        );


        String[] datos = {
                customer.getFirstName(),
                customer.getLastName(),
                customer.getMiddleInitial(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getPhone().toString(),
                customer.getStreet(),
                customer.getCity(),
                customer.getState(),
                customer.getZip().toString(),

        };
        int cellIndex = 1;
        int rowsCount = table.getItems().size();
        clickOn(isDefaultButton());
        for (String dato : datos) {
            Node cell = lookup(".table-cell").nth(cellIndex).query();
            assertNotNull(cell);
            clickOn(cell);
            //Escribe el dato en el orden de la lista y borra lo que haya dentro del campo antes de escribir
            write(dato).push(KeyCode.ENTER);
            cellIndex++;
        }
        assertEquals("The row has not been added!!!", rowsCount + 1, table.getItems().size());


    }

    @Test
    public void test_update_customer_success() {

        List<String> datos = new ArrayList<>(9);
        datos.add("NameTest");
        datos.add("LastNameTest");
        datos.add("T");
        datos.add("email@Test.com");
        datos.add("claveTest");
        datos.add("615487796");
        datos.add("StreetTest");
        datos.add("CityTest");
        datos.add("StateTest");
        datos.add("12345");

        verifyThat("#fxBtnDelete", isDisabled());
        for (int i = 0; i < datos.size(); i++) {

            Node cell = lookup(".table-cell").nth(i + 1).query();

            doubleClickOn(cell);
            //Pulsar el Ctrl+A para seleccionar toda la celda
            push(KeyCode.SHORTCUT, KeyCode.A);
            //Pulsa el espacio para borrarlo
            push(KeyCode.SPACE);
            //Se escribe el dato
            write(datos.get(i)).push(KeyCode.ENTER);
        }

    }

    @Test
    public void test_delete_customer_fail() {

        // 1. Buscar el índice del primer cliente que se pueda borrar que tenga cuentas para que salte el mensaje de fallo
        int rowIndex = 1;
        for (int i = 0; i < table.getItems().size(); i++) {
            Customer customer = table.getItems().get(i);
            //No es admin Y no tiene cuentas (o la lista está vacía)
            System.out.println("Cliente: " + customer.getFirstName() + " Cuentas: " + customer.getAccounts().size());
            // Selects row index based on customer properties
            if (!customer.getFirstName().equalsIgnoreCase("admin") &&
                    (customer.getAccounts() != null && !customer.getAccounts().isEmpty())) {
                rowIndex = i;
                break;
            }
        }
        verifyThat(btnDelete, isDisabled());
        int rowsCount = table.getItems().size();
        assertNotEquals("Table has no data: Cannot test", 0, rowsCount);
        Node row = lookup(".table-row-cell").nth(rowIndex).query();

        assertNotNull("Row is null: table has not that row. ", row);
        clickOn(row);


        verifyThat(btnDelete, isEnabled());
        clickOn(btnDelete);

        //Verificar que aparece el alert
        Node dialogPane = lookup(".dialog-pane").query();
        verifyThat(dialogPane, isVisible());

        //Verificar que es alert de tipo error a traves de la etiqueta css
        assertTrue("The alert is not an error", dialogPane.getStyleClass().contains("error"));

        //Verificar el texto de error es que queremos
        verifyThat("The user cannot be deleted because they have associated accounts or data.", isVisible());

        //Pulsar el boton de aceptar
        Node button = from(dialogPane).lookup(".button").query();
        clickOn(button);

        //Presiona la tecla Ctrl
        press(KeyCode.CONTROL);
        //Presiona nuevamente en la fila con el Ctrl pulsado
        clickOn(row);
        //Libera la tecla Ctrl
        release(KeyCode.CONTROL);
        //Verificar que se ha deshabilitado el boton de eliminar
        verifyThat("#fxBtnDelete", isDisabled());

    }
}