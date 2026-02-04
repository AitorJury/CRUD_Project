package crud_project.test.crud_project.ui.controller;

import crud_project.AppCRUD;
import crud_project.model.Account;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import static org.junit.Assert.assertEquals;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Clase de test para el controlador de Cuentas (AccountsController). Evalúa el
 * cumplimiento de los requisitos CRUD y las reglas de negocio (RA8). * Basado
 * en la estructura de paquetes de crud_project.ui.controller.
 *
 * * @author Aitor Jury Rodríguez.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountsControllerTest extends ApplicationTest {

    /**
     * Inicia la aplicación principal utilizando la clase AppCRUD. Realiza un
     * login automático para posicionarse en la ventana de Accounts.
     *
     * @param stage Escenario principal de JavaFX.
     * @throws Exception En caso de error en el inicio.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Iniciamos la aplicación desde el punto de entrada real: AppCRUD
        new AppCRUD().start(stage);

        // Proceso de Login automático para acceder a la gestión de cuentas
        clickOn("#txtEmail");
        write("aitor@gmail.com");
        clickOn("#txtPassword");
        write("abcd*1234");
        clickOn("#btnSignIn");
    }

    /**
     * Test 1: Verifica que la ventana de Accounts se carga correctamente y que
     * los componentes principales están visibles y habilitados.
     */
    @Test
    public void test1_WindowOpenAndInitialState() {
        verifyThat("#tableAccounts", isVisible());
        verifyThat("#btnAddAccount", isEnabled());
        verifyThat("#btnRefresh", isEnabled());
        verifyThat("#btnLogOut", isEnabled());
    }

    /**
     * Test 2: Verifica el proceso completo de creación de una nueva cuenta.
     * Comprueba el cambio de texto del botón a "Confirm" y el aumento de filas.
     */
    @Test
    public void test2_CreateAccountProcess() {
        // Obtenemos el número inicial de cuentas en la tabla
        int rowCount = lookup("#tableAccounts").queryTableView().getItems().size();

        clickOn("#btnAddAccount"); // Entrar en modo creación
        verifyThat("#btnAddAccount", hasText("Confirm"));
        verifyThat("#btnCancelAccount", isEnabled());

        // Simulamos la edición de la descripción en la nueva celda
        clickOn("Description");
        write("Test De Cuenta Nueva");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);

        clickOn("#btnAddAccount"); // Pulsar Confirmar

        // Verificamos el mensaje de éxito y el refresco de la tabla
        verifyThat("Account successfully created.", isVisible());
        clickOn("OK");

        // Comprobamos que el recuento de filas ha aumentado en 1
        assertEquals("La tabla debería tener una fila más tras la creación",
                rowCount + 1, lookup("#tableAccounts").queryTableView().getItems().size());
    }

    /**
     * Test 3: Verifica la Regla de Negocio (NOTA 1). Prueba que el controlador
     * bloquea el borrado si la cuenta tiene movimientos.
     */
    @Test
    public void test3_DeleteAccountWithMovementsConstraint() {
        // Seleccionamos la columna ID para ordenar y marcamos la primera fila
        clickOn("ID");
        clickOn(".table-row-cell");

        clickOn("#btnDeleteAccount");

        // Verificamos que salta el aviso local antes de ir al servidor
        verifyThat("Cannot delete account with existing movements.", isVisible());
        clickOn("OK");
    }

    /**
     * Test 4: Verifica la funcionalidad del Menú Contextual (clic derecho).
     * Comprueba que se puede navegar a la ventana modal de movimientos.
     */
    @Test
    public void test4_ContextMenuNavigation() {
        rightClickOn(".table-row-cell");
        verifyThat("View Account Movements", isVisible());
        clickOn("View Account Movements");

        // Verificamos que la tabla de movimientos es ahora visible
        verifyThat("#tbMovement", isVisible());

        // Volvemos a la ventana de Accounts cerrando la modal
        clickOn("#btnBack");
        clickOn("Yes"); // Confirmación del diálogo de salida de movimientos
    }

    /**
     * Test 5: Verifica la apertura de la Ayuda Sensible al Contexto (RA6-c).
     * Comprueba que el menú superior carga la ayuda específica de Cuentas.
     */
    @Test
    public void test5_HelpWindowOpening() {
        clickOn("Help");
        clickOn("Content");

        // Verificamos que se abre la ventana con el título definido en AccountsController
        verifyThat("Help: Managing Accounts", isVisible());
    }

    /**
     * Test 6: Verifica el flujo de cierre de sesión. Comprueba que tras
     * confirmar el Log Out volvemos a la ventana de SignIn.
     */
    @Test
    public void test6_LogOutFlow() {
        clickOn("#btnLogOut");
        verifyThat("Log out?", isVisible());
        clickOn("Yes");

        // Comprobamos que el botón de SignIn vuelve a ser visible
        verifyThat("#btnSignIn", isVisible());
    }
}
