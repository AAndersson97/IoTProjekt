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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import network.Constants;
import network.Node;
import network.NodeList;
import network.SybilNode;

import java.util.Objects;


public class SybilSimulator extends Application implements Constants {
    @FXML
    public AnchorPane anchorPane;
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
        displayedNode.relocate(createdNode.getLocation().getX(),createdNode.getLocation().getY());
        createNode.setDisable(NodeList.getInstance().numOfNodes() >= MAX_NODES);
    }

    public void onStartSybilAttack(ActionEvent actionEvent) {
        for(int i=0;i<3;i++){
            Rectangle displayedSybilNode = createRectangle();
            Node createdSNode = new Node();
            anchorPane.getChildren().add(displayedSybilNode);
            displayedSybilNode.relocate((createdSNode.getLocation().getX()),createdSNode.getLocation().getY());
        }
    }

    public void onCreateAttackNode(ActionEvent actionEvent) {
        Circle displayedANode = create();
        displayedANode.setFill(Color.RED);
        Node createdANode = new Node();
        anchorPane.getChildren().add((displayedANode));
        displayedANode.relocate(createdANode.getLocation().getX(),createdANode.getLocation().getY());
        createAttackNode.setDisable(NodeList.getInstance().numOfNodes() >= MAX_NODES);
    }

    public Circle create() {
        Circle circle = new Circle();
        circle.setFill(Color.BLUE);
        circle.setRadius(10);
        circle.setStroke(Color.BLACK);
        return circle;
    }
    public Rectangle createRectangle(){
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(Color.INDIANRED);
        rectangle.setHeight(20);
        rectangle.setWidth(20);
        rectangle.setStroke(Color.BLACK);
        return rectangle;
    }
}
