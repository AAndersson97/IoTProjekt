package UI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import network.*;
import static network.Constants.GUI.*;
import static network.Constants.Node.NUM_OF_SYBIL;

import java.util.Arrays;

public class SybilSimulator extends Application {
    @FXML
    AnchorPane anchorPane;
    @FXML
    Button sendPacketBtn;
    @FXML
    Button createNode;
    @FXML
    Button sybilAttack;

    // TA BORT
    static SybilSimulator instance;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.setResizable(false);
        Debug.registerKeyEvents(stage.getScene(), instance);
        stage.show();
    }

    public SybilSimulator() {
        if (instance == null) instance = this;
    }

    public void onCreateNode() {
        Node createdNode = new Node();
        anchorPane.getChildren().add(createNodeCircle(createdNode, Color.web("#7ac5cd")));
        //Label nodeLabel = new Label(createdNode.addressToString());
        //anchorPane.getChildren().add(nodeLabel);
        //nodeLabel.relocate(createdNode.getLocation().getX()-13,createdNode.getLocation().getY()+20);

    }

    public void onStartSybilAttack(ActionEvent actionEvent) {
        AttackNode createdNode = new AttackNode(NUM_OF_SYBIL);
        anchorPane.getChildren().add(createNodeCircle(createdNode, Color.web("#db3a42")));
        for (SybilNode node : createdNode.getSybilNodes())
            anchorPane.getChildren().add(createNodeCircle(node, Color.web("#cfc7c0")));
    }

    public Circle createNodeCircle(Node node, Color fill) {
        Circle circle = new Circle();
        circle.setFill(fill);
        circle.setRadius(CIRCLE_RADIUS);
        circle.setStroke(Color.BLACK);
        circle.relocate(node.getLocation().getX() - CIRCLE_RADIUS, node.getLocation().getY() - CIRCLE_RADIUS);
        createNode.setDisable(Network.getNumOfNodes() >= Constants.Node.MAX_NODES);
        return circle;
    }

    public void onSendPacket() {
        try {
            new SendPacketUI().showUI();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        Network.shutdownNetwork();
        Simulator.shutdown();
        super.stop();
    }

    public void packetLine(int startX, int startY, int endX, int endY){
        Line line = new Line(startX,startY,endX,endY);
        anchorPane.getChildren().add(line);
        line.toBack();

    }
}
