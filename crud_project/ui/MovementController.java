/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui;

import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author cynthia
 */
public class MovementController {
    @FXML 
    Button btnDelete;
    @FXML
    Button btnBack;
    @FXML
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private Stage stage;
    
    
    
    
    
    
    
    
    
    
    
    
    public void initStage(Stage stage, Parent root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        this.stage = stage;
        LOGGER.info("Initializing window");
        stage.setTitle("Main");
        stage.setResizable(false);
        LOGGER.info("Initializing window");
        stage.setTitle("Movement");
        stage.setResizable(false);
        stage.show();
        
        
    }
}
