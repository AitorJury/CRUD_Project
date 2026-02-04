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

/**
 * Clase de pruebas de integración para el controlador de cuentas.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountsControllerTest extends ApplicationTest {

    private TableView table;

    /**
     * Punto de entrada de la aplicación para TestFX.
     */
    @Override
    public void start(Stage stage) throws Exception {
        new AppCRUD().start(stage);
    }

    /**
     * Configuración previa a cada test: Realiza el login automático para
     * posicionarse en la ventana de gestión de cuentas.
     */
    @Before
    public void test_init_window() {
        verifyThat("#txtEmail", isVisible());
        clickOn("#txtEmail").write("test@test.test");
        clickOn("#txtPassword").write("testtest");
        clickOn("#btnSignIn");
        verifyThat("#tableAccounts", isVisible());
        table = lookup("#tableAccounts").queryTableView();
    }

    /**
     * Limpieza posterior a cada test: Cierra las ventanas abiertas para evitar
     * conflictos de estado entre pruebas.
     */
    @After
    public void close_window() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    // =========================================================================
    // 1. ESTADO INICIAL Y FUNCIONALIDADES BÁSICAS
    // =========================================================================
    /**
     * Verifica que los botones se habilitan y deshabilitan correctamente según
     * el contexto (selección de tabla o modo creación).
     */
    @Test
    public void test_A_initial_state_and_buttons_enablement() {
        // Estado sin selección.
        verifyThat("#btnAddAccount", isEnabled());
        verifyThat("#btnRefresh", isEnabled());
        verifyThat("#btnViewMovements", isDisabled());
        verifyThat("#btnDeleteAccount", isDisabled());

        // Seleccionar una fila habilita acciones.
        clickOn(".table-row-cell").nth(0);
        verifyThat("#btnViewMovements", isEnabled());
        verifyThat("#btnDeleteAccount", isEnabled());

        // Entrar en modo creación bloquea navegación y refresco.
        clickOn("#btnAddAccount");
        verifyThat("#btnCancelAccount", isEnabled());
        verifyThat("#btnRefresh", isDisabled());
        verifyThat("#btnLogOut", isDisabled());
    }

    /**
     * Comprueba que el botón de refresco muestra el mensaje de confirmación
     * tras sincronizar con el servidor.
     */
    @Test
    public void test_B_refresh_system() {
        clickOn("#btnRefresh");
        verifyThat("#lblMessage", hasText("Data refreshed from server."));
    }

    // =========================================================================
    // 2. MENÚS Y NAVEGACIÓN
    // =========================================================================
    /**
     * Valida la navegación a través de la barra de menús superior (MenuBar).
     */
    @Test
    public void test_C_menu_bar_navigation() {
        // Probar diálogo de cierre de sesión.
        clickOn("Session");
        clickOn("Log Out");
        verifyThat(".dialog-pane", isVisible());
        clickOn("No");

        // Probar apertura de ayuda.
        clickOn("Help");
        clickOn("Content");
        verifyThat("Help: Managing Accounts", isVisible());
    }

    /**
     * Verifica que las opciones del menú contextual (click derecho) funcionan
     * correctamente sobre las filas de la tabla.
     */
    @Test
    public void test_D_context_menu_actions() {
        // Navegar a movimientos mediante click derecho.
        rightClickOn(".table-row-cell").nth(0);
        clickOn("View Account Movements");
        verifyThat("#tbMovement", isVisible());
        clickOn("#btnBack");
        clickOn("Yes"); // Confirmar salida de movimientos.

        // Intentar borrar mediante click derecho.
        rightClickOn(".table-row-cell").nth(0);
        clickOn("Delete Selected Account");
        verifyThat(".dialog-pane", isVisible());
        clickOn("No");
    }

    /**
     * Comprueba la navegación estándar a la ventana de movimientos de cuenta.
     */
    @Test
    public void test_E_navigation_movements_window() {
        clickOn("#tableAccounts");
        Node row = lookup(".table-row-cell").nth(0).query();
        clickOn(row);
        clickOn("#btnViewMovements");
        verifyThat("#tbMovement", isVisible());
    }

    // =========================================================================
    // 3. REGLAS DE NEGOCIO Y VALIDACIÓN DE EDICIÓN
    // =========================================================================
    /**
     * Verifica que los campos sensibles (Tipo, Saldo Inicial) no pueden ser
     * editados en cuentas que ya existen en el sistema.
     */
    @Test
    public void test_F_validate_immutable_fields() {
        int cols = table.getColumns().size();

        // Intentar editar Tipo de Cuenta.
        Node cellType = lookup(".table-cell").nth(cols + 2).query();
        doubleClickOn(cellType);
        verifyThat("#lblMessage", hasText("Account type cannot be modified for existing accounts."));

        // Intentar editar Saldo Inicial.
        Node cellBegin = lookup(".table-cell").nth(cols + 5).query();
        doubleClickOn(cellBegin);
        verifyThat("#lblMessage", hasText("Initial balance cannot be modified."));
    }

    /**
     * Valida la lógica de la línea de crédito según el tipo de cuenta.
     */
    @Test
    public void test_G_credit_line_rules() {
        int cols = table.getColumns().size();

        // Error en cuenta estándar (no admite crédito).
        Node cellCreditFirst = lookup(".table-cell").nth(4).query();
        doubleClickOn(cellCreditFirst);
        verifyThat("#lblMessage", hasText("Credit line only applicable to CREDIT accounts."));

        // Éxito en cuenta de crédito.
        Node cellCreditSecond = lookup(".table-cell").nth(cols + 4).query();
        doubleClickOn(cellCreditSecond);
        write("500.0");
        type(KeyCode.ENTER);
        verifyThat("#lblMessage", hasText("Credit line updated."));
    }

    /**
     * Comprueba que el sistema rechaza valores negativos en la línea de
     * crédito.
     */
    @Test
    public void test_H_negative_credit_line_fail() {
        int cols = table.getColumns().size();
        Node cellCreditSecond = lookup(".table-cell").nth(cols + 4).query();
        doubleClickOn(cellCreditSecond);
        write("-100");
        type(KeyCode.ENTER);
        verifyThat("#lblMessage", hasText("Credit Line must be 0 or positive."));
    }

    /**
     * Verifica que la descripción de una cuenta existente puede ser
     * actualizada.
     */
    @Test
    public void test_I_update_description_success() {
        String newDesc = "Desc " + System.currentTimeMillis();
        Node cellDesc = lookup(".table-cell").nth(1).query();
        doubleClickOn(cellDesc);
        write(newDesc);
        type(KeyCode.ENTER);
        verifyThat("#lblMessage", hasText("Description updated."));
    }

    // =========================================================================
    // 4. GESTIÓN DE CREACIÓN DE CUENTAS
    // =========================================================================
    /**
     * Valida que la cancelación del modo creación limpia la tabla
     * correctamente.
     */
    @Test
    public void test_J_create_account_cancel_logic() {
        clickOn("#btnAddAccount");
        verifyThat("#btnAddAccount", hasText("Confirm"));
        clickOn("#btnCancelAccount");
        verifyThat("#btnAddAccount", hasText("Create Account"));
    }

    /**
     * Comprueba que no se pueden editar otras filas mientras se está creando
     * una cuenta nueva.
     */
    @Test
    public void test_K_creation_lock_active_row() {
        clickOn("#btnAddAccount");
        Node cellOther = lookup(".table-cell").nth(1).query();
        doubleClickOn(cellOther);
        verifyThat("#lblMessage", hasText("Finish creating the new account first."));
        clickOn("#btnCancelAccount");
    }

    /**
     * Valida que el sistema impide crear cuentas sin una descripción.
     */
    @Test
    public void test_L_create_without_description_fail() {
        clickOn("#btnAddAccount");
        clickOn("#btnAddAccount"); // Intentar confirmar sin escribir.
        verifyThat("#lblMessage", hasText("Description is obligatory."));
        clickOn("#btnCancelAccount");
    }

    /**
     * Realiza el flujo completo de creación exitosa de una nueva cuenta.
     */
    @Test
    public void test_M_create_account_success() {
        int rowsBefore = table.getItems().size();
        clickOn("#btnAddAccount");

        // Calcular celda de descripción en la nueva fila añadida.
        Node cell = lookup(".table-cell").nth(rowsBefore * table.getColumns().size() + 1).query();
        doubleClickOn(cell);
        write("Cuenta Tests");
        type(KeyCode.ENTER);

        clickOn("#btnAddAccount"); // Confirmar.
        verifyThat("#lblMessage", hasText("Account created."));
        assertEquals(rowsBefore + 1, table.getItems().size());
    }

    // =========================================================================
    // 5. GESTIÓN DE BORRADO DE CUENTAS
    // =========================================================================
    /**
     * Valida la regla de integridad: No se pueden borrar cuentas con
     * movimientos.
     */
    @Test
    public void test_N_delete_account_fail_has_movements() {
        clickOn("#tableAccounts");
        Node row = lookup(".table-row-cell").nth(0).query();
        clickOn(row);
        clickOn("#btnDeleteAccount");
        verifyThat("#lblMessage", hasText("Cannot delete account with existing movements."));
    }

    /**
     * Realiza el flujo completo de borrado exitoso de una cuenta recién creada.
     */
    @Test
    public void test_O_delete_new_account_success() {
        int rowsCurrent = table.getItems().size();
        clickOn("#tableAccounts");

        // Seleccionar la última fila (la cuenta creada en tests anteriores).
        Node lastRow = lookup(".table-row-cell").nth(rowsCurrent - 1).query();
        clickOn(lastRow);

        clickOn("#btnDeleteAccount");
        clickOn("Yes"); // Confirmar en el Alert.

        verifyThat("#lblMessage", hasText("Account deleted."));
        assertEquals(rowsCurrent - 1, table.getItems().size());
    }

    // =========================================================================
    // 6. CIERRE DE SESIÓN
    // =========================================================================
    /**
     * Valida que el proceso de Log Out devuelve al usuario a la pantalla de
     * login.
     */
    @Test
    public void test_P_logout_process() {
        clickOn("#btnLogOut");
        clickOn("Yes");
        verifyThat("#btnSignIn", isVisible());
    }

    /*
    @Test
    public void test_Z_server_connection_fail() {
        // Verifica el comportamiento ante una caída del servidor REST.
        verifyThat("Network connection error.", isVisible());
    }
     */
}
