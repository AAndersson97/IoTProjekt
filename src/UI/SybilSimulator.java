package UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import network.*;

import java.util.ArrayList;
import java.util.Objects;


public class SybilSimulator extends Application implements Constants {
    @FXML
    public AnchorPane anchorPane;
    @FXML
    Button simPacketsButton;
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

        Communication.addPacketListener((packet) -> {
            System.out.println("Packet Added!");
        });
    }

    public void onCreateNode(ActionEvent actionEvent) {
        Circle displayedNode = create();
        Node createdNode = new Node();
        anchorPane.getChildren().add(displayedNode);
        displayedNode.relocate(createdNode.getLocation().getX(),createdNode.getLocation().getY());
        createNode.setDisable(NodeList.getInstance().numOfNodes() >= MAX_NODES);
        Label nodeLabel = new Label(createdNode.addressToString());
        anchorPane.getChildren().add(nodeLabel);
        nodeLabel.relocate(createdNode.getLocation().getX()-13,createdNode.getLocation().getY()+20);
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

    public void onSimPackets(ActionEvent actionEvent) {
        for(Node node:NodeList.getInstance().getNodeList().values()){
            ArrayList<Node> neighbours = ProximitySearcher.getInstance().findClosestNeighbours(node);
            for(Node neighbour : neighbours){
                packetLine(node.getLocation().getX()+10, node.getLocation().getY()+10,neighbour.getLocation().getX()+10,neighbour.getLocation().getY()+10);

            }
        }

    }
    public Line packetLine(int startX,int startY,int endX,int endY){
        Line line = new Line(startX,startY,endX,endY);
        anchorPane.getChildren().add(line);
        return line;
    }
}
