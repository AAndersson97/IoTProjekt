package UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import network.Constants;

public class SybilSimulator extends Application implements Constants {
    @FXML
    Button createNode;
    @FXML
    Button createAttackNode;
    @FXML
    Button sybilAttack;
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        stage.setTitle("Sybil Simulator");
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));

        stage.show();
    }

    public void onCreateNode(ActionEvent actionEvent) {

    }

    public void onStartSybilAttack(ActionEvent actionEvent) {
    }

    public void onCreateAttackNode(ActionEvent actionEvent) {
    }
}
