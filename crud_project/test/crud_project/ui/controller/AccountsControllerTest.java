package crud_project.ui.controller;

import crud_project.AppCRUD;
import crud_project.model.Account;
import crud_project.model.AccountType;
import java.util.Date;
import java.util.List;
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
import org.junit.Ignore;

/**
 * Clase de pruebas de integración para el controlador de cuentas.
 *
 * @fixme (ARREGLADO) Test insuficientes: crear un test que compruebe la
 * actualización con éxito de description y creditLine para una cuenta de
 * Crédito.Verificar que la Account seleccionada para modificar entre los items
 * de la tabla tiene modificados dichos campos. *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountsControllerTest extends ApplicationTest {

    private TableView table;
    private static Long idAcc;
    private static String uniqueName;

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
        clickOn("#txtEmail").write("awallace@gmail.com");
        // clickOn("#txtEmail").write("aitor.jr04@gmail.com");
        clickOn("#txtPassword").write("qwerty*9876");
        // clickOn("#txtPassword").write("abcd*1234");
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
    @Ignore
    @Test
    public void test_A_initial_state_and_buttons_enablement() {
        verifyThat("#btnAddAccount", isEnabled());
        verifyThat("#btnRefresh", isEnabled());

        clickOn("#btnAddAccount");
        verifyThat("#btnCancelAccount", isEnabled());
        verifyThat("#btnRefresh", isDisabled());
        verifyThat("#btnLogOut", isDisabled());

        clickOn("#btnCancelAccount");

        if (table.getItems().isEmpty()) {
            return;
        }

        clickOn((Node) lookup(".table-row-cell").nth(0).query());
        verifyThat("#btnViewMovements", isEnabled());
        verifyThat("#btnDeleteAccount", isEnabled());
    }

    /**
     * Comprueba que el botón de refresco muestra el mensaje de confirmación
     * tras sincronizar con el servidor.
     */
    @Ignore
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
    @Ignore
    @Test
    public void test_C_menu_bar_navigation() {
        clickOn("Session");
        clickOn("#fxMenuSignOut");
        verifyThat(".dialog-pane", isVisible());
        clickOn("No");

        clickOn("Help");
        clickOn("#fxMenuContent");
        Node webView = lookup(".web-view").query();
        assertNotNull("La ventana de ayuda no cargó el WebView", webView);
        verifyThat(webView, isVisible());
    }

    /**
     * Verifica que las opciones del menú contextual (click derecho) funcionan
     * correctamente sobre las filas de la tabla.
     */
    @Ignore
    @Test
    public void test_D_context_menu_actions() {
        if (table.getItems().isEmpty()) {
            return;
        }

        int targetRow = -1;
        for (int i = 0; i < table.getItems().size(); i++) {
            Account acc = (Account) table.getItems().get(i);
            if (acc.getMovements() != null && !acc.getMovements().isEmpty()) {
                targetRow = i;
                break;
            }
        }

        if (targetRow == -1) {
            return;
        }

        Node rowNode = lookup(".table-row-cell").nth(targetRow).query();

        clickOn(rowNode);

        rightClickOn(rowNode);
        clickOn("View Account Movements");
        verifyThat("#tbMovement", isVisible());
        clickOn("#btnBack");
        clickOn("Yes");

        rowNode = lookup(".table-row-cell").nth(targetRow).query();
        clickOn(rowNode);
        rightClickOn(rowNode);
        clickOn("Delete Selected Account");

        verifyThat("#lblMessage", hasText("Cannot delete account with existing movements."));
    }

    /**
     * Comprueba la navegación estándar a la ventana de movimientos de cuenta.
     */
    @Ignore
    @Test
    public void test_E_navigation_movements_window() {
        if (table.getItems().isEmpty()) {
            return;
        }

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
    @Ignore
    @Test
    public void test_F_validate_immutable_fields() {
        int cols = table.getColumns().size();

        if (table.getItems().isEmpty()) {
            return;
        }

        Node cellType = lookup(".table-cell").nth(cols + 2).query();
        doubleClickOn(cellType);
        verifyThat("#lblMessage", hasText("Account type cannot be modified for existing accounts."));

        Node cellBegin = lookup(".table-cell").nth(cols + 5).query();
        doubleClickOn(cellBegin);
        verifyThat("#lblMessage", hasText("Initial balance cannot be modified."));
    }

    /**
     * Valida la lógica de la línea de crédito según el tipo de cuenta.
     */
    @Ignore
    @Test
    public void test_G_credit_line_rules() {
        if (table.getItems().isEmpty()) {
            return;
        }

        int standardRow = -1;
        int creditRow = -1;
        int numCols = table.getColumns().size();

        for (int i = 0; i < table.getItems().size(); i++) {
            Account acc = (Account) table.getItems().get(i);
            if (standardRow == -1 && acc.getType().toString().equals("STANDARD")) {
                standardRow = i;
            }
            if (creditRow == -1 && acc.getType().toString().equals("CREDIT")) {
                creditRow = i;
            }
        }

        if (standardRow != -1) {
            Node cellCreditStd = lookup(".table-cell").nth(standardRow * numCols + 4).query();
            doubleClickOn(cellCreditStd);
            verifyThat("#lblMessage", hasText("Credit line only applicable to CREDIT accounts."));
        }

        if (creditRow != -1) {
            Node cellCreditCre = lookup(".table-cell").nth(creditRow * numCols + 4).query();
            doubleClickOn(cellCreditCre);
            write("500.0");
            type(KeyCode.ENTER);
            verifyThat("#lblMessage", hasText("Changes saved."));
        }
    }

    /**
     * Comprueba que el sistema rechaza valores negativos en la línea de
     * crédito.
     */
    @Ignore
    @Test
    public void test_H_negative_credit_line_fail() {
        if (table.getItems().isEmpty()) {
            return;
        }

        int creditRow = -1;
        int numCols = table.getColumns().size();

        for (int i = 0; i < table.getItems().size(); i++) {
            Account acc = (Account) table.getItems().get(i);
            if (acc.getType().toString().equals("CREDIT")) {
                creditRow = i;
                break;
            }
        }

        if (creditRow != -1) {
            Node cellCredit = lookup(".table-cell").nth(creditRow * numCols + 4).query();

            doubleClickOn(cellCredit);
            write("-100");
            type(KeyCode.ENTER);

            verifyThat("#lblMessage", hasText("Credit Line must be 0 or positive."));
        }
    }

    /**
     * Verifica que la descripción de una cuenta existente puede ser
     * actualizada.
     *
     * @fixme (ARREGLADO) Test insuficiente: verificar que la Account
     * seleccionada para modificar entre los items de la tabla tiene la nueva
     * descripción.
     */
    @Ignore
    @Test
    public void test_I_update_description_success() {
        if (table.getItems().isEmpty()) {
            return;
        }

        Account acc = null;
        String newDesc = "Update " + System.currentTimeMillis();

        Date actualDate = new Date();
        Long time = actualDate.getTime();
        int change = (int) (time % 1000);
        Double newCredit = (double) change;
        
        int accRow = -1;
        int numCols = table.getColumns().size();

        for (int i = 0; i < table.getItems().size(); i++) {
            acc = (Account) table.getItems().get(i);
            if (acc.getType().toString().equals("CREDIT")) {
                accRow = i;
                break;
            }
        }

        if (accRow != -1) {
            Node cellDesc = lookup(".table-cell").nth(accRow * numCols + 1).query();
            doubleClickOn(cellDesc);
            write(newDesc);
            type(KeyCode.ENTER);
            verifyThat("#lblMessage", hasText("Changes saved."));

            Node cellCredit = lookup(".table-cell").nth(accRow * numCols + 4).query();
            doubleClickOn(cellCredit);
            write(newCredit.toString());
            type(KeyCode.ENTER);
            verifyThat("#lblMessage", hasText("Changes saved."));

            clickOn("#btnRefresh");
            int index = table.getSelectionModel().getSelectedIndex();
            assertEquals("La cuenta no se ha editado.", acc, (Account) table.getItems().get(index));
            List<Account> accs = table.getItems();
            assertEquals("La cuenta no se ha actualizado en la base de datos.", accs.stream().filter(a -> a.getDescription().equals(newDesc)).count(), 1);
            assertEquals("La cuenta no se ha actualizado en la base de datos.", accs.stream().filter(a -> a.getCreditLine().equals(newCredit)).count(), 1);
        }
    }

    // =========================================================================
    // 4. GESTIÓN DE CREACIÓN DE CUENTAS
    // =========================================================================
    /**
     * Valida que la cancelación del modo creación limpia la tabla
     * correctamente.
     */
    @Ignore
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
    @Ignore
    @Test
    public void test_K_creation_lock_active_row() {
        if (table.getItems().isEmpty()) {
            return;
        }

        clickOn("#btnAddAccount");

        int numColumns = table.getColumns().size();
        Node accCell = lookup(".table-cell").nth(numColumns + 1).query();
        doubleClickOn(accCell);

        verifyThat("#lblMessage", hasText("Finish creating the new account first."));

        clickOn("#btnCancelAccount");
    }

    /**
     * Valida que el sistema impide crear cuentas sin una descripción.
     */
    @Ignore
    @Test
    public void test_L_create_without_description_fail() {
        clickOn("#btnAddAccount");
        clickOn("#btnAddAccount");
        verifyThat("#lblMessage", hasText("Description is obligatory."));
        clickOn("#btnCancelAccount");
    }

    /**
     * Realiza el flujo completo de creación exitosa de una nueva cuenta.
     *
     * @fixme (ARREGLADO) Test insuficiente: en la interacción establecer
     * valores para todos los campos editables a la hora de crear para Account,
     * guardar estos datos en un objeto Account y verificar que dicho objeto
     * está entre los items de la tabla.
     */
    @Ignore
    @Test
    public void test_M_create_account_success() {
        int rowsBefore = table.getItems().size();
        uniqueName = "Test-" + System.currentTimeMillis();
        Double expCredit = 100.0;
        Double expBegin = 100.0;
        AccountType expType = AccountType.CREDIT;

        clickOn("#btnAddAccount");

        int rowAcc = table.getItems().size() - 1;
        int numCols = table.getColumns().size();

        Node cellDesc = lookup(".table-cell").nth(1).query();
        doubleClickOn(cellDesc);
        write(uniqueName);
        type(KeyCode.ENTER);

        Node cellType = lookup(".table-cell").nth(rowAcc * numCols + 2).query();
        clickOn(cellType);
        clickOn(cellType);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        Node cellCredit = lookup(".table-cell").nth(rowAcc * numCols + 4).query();
        doubleClickOn(cellCredit);
        write("100");
        type(KeyCode.ENTER);

        Node cellBegin = lookup(".table-cell").nth(rowAcc * numCols + 5).query();
        doubleClickOn(cellBegin);
        write("100");
        type(KeyCode.ENTER);

        clickOn("#btnAddAccount");
        verifyThat("#lblMessage", hasText("Account created."));
        clickOn("#btnRefresh");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        idAcc = null;
        for (Object item : table.getItems()) {
            Account acc = (Account) item;
            if (acc.getDescription().equals(uniqueName)) {
                idAcc = acc.getId();
                break;
            }
        }

        assertNotNull("La cuenta no se ha añadido.", idAcc);

        Account newAcc = new Account();
        newAcc.setId(idAcc);
        newAcc.setDescription(uniqueName);
        newAcc.setType(expType);
        newAcc.setCreditLine(expCredit);
        newAcc.setBeginBalance(expBegin);

        List<Account> accs = table.getItems();
        assertEquals("La cuenta no ha sido creada correctamente.", 1, accs.stream().filter(a -> a.equals(newAcc)).count());

        assertEquals("Las filas no están bien.", rowsBefore + 1, accs.size());
    }

    // =========================================================================
    // 5. GESTIÓN DE BORRADO DE CUENTAS
    // =========================================================================
    /**
     * Valida la regla de integridad: No se pueden borrar cuentas con
     * movimientos.
     */
    @Ignore
    @Test
    public void test_N_delete_account_fail_has_movements() {
        if (table.getItems().isEmpty()) {
            return;
        }

        int accMovRow = -1;

        for (int i = 0; i < table.getItems().size(); i++) {
            Account acc = (Account) table.getItems().get(i);
            if (acc.getMovements() != null && !acc.getMovements().isEmpty()) {
                accMovRow = i;
                break;
            }
        }

        if (accMovRow == -1) {
            return;
        }

        Node row = lookup(".table-row-cell").nth(accMovRow).query();

        clickOn(row);
        clickOn("#btnDeleteAccount");

        verifyThat("#lblMessage", hasText("Cannot delete account with existing movements."));
    }

    /**
     * Realiza el flujo completo de borrado exitoso de una cuenta recién creada.
     *
     * @fixme (ARREGLADO) Test insuficiente: verificar que el objeto Account
     * eliminado ya NO está entre los items de la tabla.
     */
    @Ignore
    @Test
    public void test_O_delete_new_account_success() {
        if (idAcc == null) {
            for (Object item : table.getItems()) {
                Account acc = (Account) item;
                if (acc.getMovements() == null || acc.getMovements().isEmpty()) {
                    idAcc = acc.getId();
                    break;
                }
            }
        }

        if (idAcc == null) {
            return;
        }

        int rowsCurrent = table.getItems().size();
        int targetRow = -1;

        for (int i = 0; i < table.getItems().size(); i++) {
            Account acc = (Account) table.getItems().get(i);
            if (acc.getId().equals(idAcc)) {
                targetRow = i;
                break;
            }
        }

        if (targetRow != -1) {
            Node rowNode = lookup(".table-row-cell").nth(targetRow).query();
            clickOn(rowNode);
            clickOn("#btnDeleteAccount");
            clickOn("Yes");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            verifyThat("#lblMessage", hasText("Account deleted."));
            assertEquals("La cuenta no se eliminó de la tabla.", rowsCurrent - 1, table.getItems().size());

            List<Account> accs = table.getItems();
            assertEquals("La cuenta no se ha eliminado de la base de datos.", accs.stream().filter(a -> a.getId().equals(idAcc)).count(), 0);
        } else {
            fail("No se encontró la fila con el ID: " + idAcc + " y nombre: " + uniqueName);
        }
    }

    // =========================================================================
    // 6. CIERRE DE SESIÓN
    // =========================================================================
    /**
     * Valida que el proceso de Log Out devuelve al usuario a la pantalla de
     * login.
     */
    @Ignore
    @Test
    public void test_P_logout_process() {
        clickOn("#btnLogOut");
        clickOn("Yes");
        verifyThat("#btnSignIn", isVisible());
    }
}
