package UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import network.Constants;
import network.Node;

import java.util.Objects;


public class SybilSimulator extends Application implements Constants {
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public Circle regNode;
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
        Circle displayedNode = create();
        Node createdNode = new Node();
        anchorPane.getChildren().add(displayedNode);
        displayedNode.relocate(createdNode.getLocation().getY(),createdNode.getLocation().getX());


    }

    public void onStartSybilAttack(ActionEvent actionEvent) {
    }

    public void onCreateAttackNode(ActionEvent actionEvent) {
        Circle displayedANode = create();
        displayedANode.setFill(Color.RED);
        Node createdANode = new Node();
        anchorPane.getChildren().add((displayedANode));
        displayedANode.relocate(createdANode.getLocation().getX(),createdANode.getLocation().getY());
    }

    public Circle create() {
        Circle circle = new Circle();
        circle.setFill(Color.BLUE);
        circle.setRadius(10);
        circle.setStroke(Color.BLACK);
        return circle;
    }
}
