/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui.controller;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cynthia
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.FixMethodOrder;
import crud_project.AppCRUD;
import crud_project.model.Account;
import crud_project.model.Movement;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runners.MethodSorters;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.ButtonMatchers.isDefaultButton;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.control.ListViewMatchers.isEmpty;

/**
 *
 * @author cynthia
 * @fixme Añadir un método de test para el caso de uso READ que compruebe que
 * los items de la tabla son objetos Movement. , LISTO
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MovementControllerTest extends ApplicationTest {

    private TableView table;
    private ComboBox comboType;

    @Override
    public void start(Stage stage) throws Exception {
        new AppCRUD().start(stage);
    }

    @Before
    public void testStart() {
        clickOn("#txtEmail");
        write("awallace@gmail.com");
        clickOn("#txtPassword");
        write("qwerty*9876");
        clickOn("#btnSignIn");
        //cambiar el id para el test 0
        //clickOn("6599097192");
        clickOn("#tableAccounts");
        Node row = lookup(".table-row-cell").nth(0).query();
        clickOn(row);
        clickOn("#btnViewMovements");

    }

    @After
    public void close_window() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    /*
    @Test
    public void test0_ButtonCreatePaymentWithCredit() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write("100000");
        clickOn("#createMovement");
        clickOn("Yes");
        int numRowAfter = table.getItems().size();
        assertTrue("The movement cant be created", numRowAfter > numRowBefore);
    }
     */
    // va solo si es la segunda vez revisar
    @Ignore
    @Test
    public void test0_verifyReadMovements() {
        table = lookup("#tbMovement").queryTableView();
        ObservableList<Movement> movements = table.getItems();
        assertTrue("Algunos datos no son movimientos", movements.stream().allMatch(u -> u instanceof Movement));
    }

    @Ignore
    @Test
    public void test1_ButtonCreateDeposit() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();

        int numRowBefore = table.getItems().size();

        Double amount = Math.round(ThreadLocalRandom.current().nextDouble(100, 1000) * 100.0) / 100.0;

        Movement newMovements = new Movement();

        //Sumo id a uno y ya se cual es el ultimo 
        //fecha la cuentta
        //Filtrar el movimiento 
        clickOn("#comboType");
        //Selecciono deposito
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write(amount.toString());
        clickOn("#createMovement");
        clickOn("Yes");
        int numRowAfter = table.getItems().size();

        assertTrue("The movement cant be created", numRowAfter > numRowBefore);
        List<Movement> movements = table.getItems();
        assertEquals("No se creo el movimiento", movements.stream().filter(u -> u.getAmount().equals(amount)
                && u.getDescription().equals("Deposit")).count(), 1);
        

        //FIXME El assert anterior es insuficiente. Añadir uno que compruebe que el nuevo Movement 
        //FIXME con los datos introducidos está entre los items de la tabla. LISTO
    }

    @Ignore
    @Test
    public void test2_ButtonCreatePayment() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();

        int numRowBefore = table.getItems().size();

        Double amount = Math.round(ThreadLocalRandom.current().nextDouble(10, 20) * 100.0) / 100.0;

        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write(amount.toString());
        clickOn("#createMovement");
        clickOn("Yes");
        int numRowAfter = table.getItems().size();

        assertTrue("The movement cant be created", numRowAfter > numRowBefore);
        List<Movement> movements = table.getItems();
        
        assertEquals("No se creo el movimiento", movements.stream().filter(u -> u.getAmount().equals(-amount)
                && u.getDescription().equals("Payment")).count(),  1);
      
        //FIXME El assert anterior es insuficiente. Añadir uno que compruebe que el nuevo Movement 
        //FIXME con los datos introducidos está entre los items de la tabla. LISTO
    }

    @Ignore
    @Test
    public void test3_DeleteMovement() {
        //Obtiene las filas iniciales
        TableView<Movement> table = lookup("#tbMovement").queryTableView();

        Movement lastDate = table.getItems().stream()
                .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .get();

        Long lastId = lastDate.getId();
        int rowCountBefore = table.getItems().size();

        assertNotEquals("No hay datos en la tabla no se puede testear", rowCountBefore, 0);
        List<Movement> movements = table.getItems();
        clickOn("#btnDelete");
        clickOn("Yes");

        int rowCountAfter = table.getItems().size();
        assertEquals(rowCountBefore - 1, rowCountAfter);
        verifyThat("#btnDelete", isDisabled());
        assertTrue("El movimiento no se ha borrado",
                table.getItems().stream().noneMatch(m -> m.getId().equals(lastId)));

        //FIXME El assert anterior es insuficiente. Añadir uno que compruebe que el Movement 
        //FIXME seleccionado para borrar no está entre los items de la tabla. LISTO
    }

    @Ignore
    @Test
    public void test4_ButtonBackAccount() {
        clickOn("#btnBack");
        clickOn("Yes");
        verifyThat("My Accounts Management", isVisible());
    }

    @Ignore
    @Test
    public void test5_ButtonCreateNegativeAmountFailed() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write("-100");
        clickOn("#createMovement");
        verifyThat("Amount cant be negative", isVisible());

    }

}
