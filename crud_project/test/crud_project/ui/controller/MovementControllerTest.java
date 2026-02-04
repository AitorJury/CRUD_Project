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
import crud_project.AppCRUD;
import crud_project.model.Account;
import crud_project.model.Movement;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.control.ListViewMatchers.isEmpty;

/**
 *
 * @author cynthia
 */
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
        write("cynthia@gmail.com");
        clickOn("#txtPassword");
        write("cynthia1");
        clickOn("#btnSignIn");
        clickOn("8182225860");
        clickOn("#btnViewMovements");

    }

    @Test
    public void test1_ButtonCreate() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write("1000");
        clickOn("#createMovement");
        clickOn("Yes");

        int numRowAfter = table.getItems().size();
        assertTrue("Could not delete movement", numRowAfter > numRowBefore);
        /*clickOn("Type");
        clickOn("Payment");
        clickOn("#txtAmount");
        write("1000");
        clickOn("#btnCreate");
        clickOn("Yes");
        int numRowAfter2 = table.getItems().size();
        assertTrue("Could not delete movement", numRowAfter2 > numRowBefore);
         */
    }

    @Test
    public void test2_ButtonBackAccount() {
        clickOn("#txtEmail");
        write("cynthia@gmail.com");
        clickOn("#txtPassword");
        write("cynthia1");
        clickOn("#btnSignIn");
        clickOn("#btnBack");
        clickOn("Yes");
        verifyThat("My Accounts Management", isVisible());
    }

    @Test
    public void test2_DeleteMovement() {
        //Obtiene las filas iniciales
        TableView<Account> table = lookup("#tbMovement").queryTableView();
        int rowCountBefore = table.getItems().size();
        clickOn("#btnDelete");
        clickOn("Yes");
        //Miramos cuantas celdas hay despues
        int rowCountAfter = table.getItems().size();
        assertEquals(rowCountBefore - 1, rowCountAfter);
        
    }

}

/*
    @Test
    public void test2_DeleteMovement() {
        //Obtiene las filas iniciales
        TableView<Account> table = lookup("#tbMovement").queryTableView();
        int rowCountBefore = table.getItems().size();
        clickOn("#btnDelete");
        clickOn("Yes");
        //Miramos cuantas celdas hay despues
        int rowCountAfter = table.getItems().size();
        assertEquals(rowCountBefore - 1, rowCountAfter);
    }

     
/*
    
/*
    @Test
    public void test3_ButtonCreate() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        //table = lookup("#tbMovement").queryTableView();
        //Node cell = comboType.lookup(".list-cell");
        //comboType.getSelectionModel().select("Deposit");
        clickOn("#txtAmount");
        write("1000");
        clickOn("#createMovement");
        int numRowAfter = table.getItems().size();
        assertTrue("Could not delete movement", numRowAfter > numRowBefore);
        clickOn("Type");
        clickOn("Payment");
        clickOn("#txtAmount");
        write("1000");
        clickOn("#btnCreate");
        int numRowAfter2 = table.getItems().size();
        assertTrue("Could not delete movement", numRowAfter2 > numRowBefore);
    }

    /*@Test
    public void test0_InitialStage() {
        clickOn("#txtEmail");
        write("cynthia@gmail.com");
        clickOn("#txtPassword");
        write("cynthia1");
        clickOn("#btnSignIn");
        verifyThat("#txtAmount", hasText(""));
        verifyThat("#btnDelete", isEnabled());
        verifyThat("#btnCreate", isDisabled());
        verifyThat("#lblError", hasText(""));
        /*
    } */

 /* 

    }*/
 /*   
     
    
    
    /*

    @Test
    public void test3_ButtonCreatePaymentFailed() {
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        table = lookup("#tbMovement").queryTableView();
        Node cell = comboType.lookup(".list-cell");
        comboType.getSelectionModel().select("Payment");
        clickOn("#txtAmount");
        write("100000");
        verifyThat("The balance and the credit are insuficient", isVisible());
    }

    @Test
    public void test4_ButtonCreateNegativeAmountFailed() {
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        table = lookup("#tbMovement").queryTableView();
        Node cell = comboType.lookup(".list-cell");
        comboType.getSelectionModel().select("Payment");
        clickOn("#txtAmount");
        write("-100000");
        verifyThat("The balance and the credit are insuficient", isVisible());
    }

    
    }*/
