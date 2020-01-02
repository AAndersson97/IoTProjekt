package UI;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import network.*;
import static network.Constants.GUI.*;
import static network.Constants.Node.NUM_OF_SYBIL;

import java.util.ArrayList;
import java.util.Arrays;

public class SybilSimulator extends Application {
    @FXML
    MenuBar menuDisplay;
    @FXML
    MenuItem TAMenu;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Button sendPacketBtn;
    @FXML
    Button createNode;
    @FXML
    Button sybilAttack;

    private static ArrayList<Circle> taCircles;
    private ArrayList<Label> addressLabels = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.setResizable(false);
        stage.show();
        taCircles = new ArrayList<>();
    }

    public void onCreateNode() {
        Node createdNode = new Node();
        anchorPane.getChildren().add(createNodeCircle(createdNode, Color.web("#7ac5cd")));
        String address = Arrays.toString(createdNode.getAddress()).replace(",", ".");
        Label nodeLabel = new Label(address.substring(1, address.length()-1));
        anchorPane.getChildren().add(nodeLabel);
        nodeLabel.relocate(createdNode.getLocation().getX()-29,createdNode.getLocation().getY()+12);
        nodeLabel.setVisible(false);
        addressLabels.add(nodeLabel);
        Circle taCircle = createTACircle(createdNode);
        taCircle.setVisible(false);
        taCircles.add(taCircle);
        anchorPane.getChildren().add(taCircle);
    }

    public void onStartSybilAttack() {
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
            for(Label nL : addressLabels){
                nL.setVisible(true);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Visa överföringsområde för varje nod om de är dolda annars dölj områden
     */
    public void displayTAs() {
        for (Circle circle : taCircles)
            circle.setVisible(true);
        TAMenu.setText("Display Transmission Areas");
    }
    public void hideTAs(){
        for(Circle circle : taCircles)
            circle.setVisible(false);
        TAMenu2.setText("Hide Transmission Areas");
    }

    private static Circle createTACircle(Node node) {
        Circle circle = new Circle();
        int circleRadius = node.getTransmissionRadius();
        circle.setFill(Color.web("#ffffff", 0.5));
        circle.setRadius(circleRadius);
        circle.setStroke(Color.web("#000000", 0.5));
        circle.relocate(node.getLocation().getX() - (circleRadius - 10) - CIRCLE_RADIUS, node.getLocation().getY() - (circleRadius - 10) - CIRCLE_RADIUS);
        circle.setViewOrder(1);
        return circle;
    }

    @Override
    public void stop() throws Exception {
        Network.shutdownNetwork();
        Simulator.shutdown();
        super.stop();
    }

    /*public void packetLine(int startX, int startY, int endX, int endY) {
        Line line = new Line(startX,startY,endX,endY);
        anchorPane.getChildren().add(line);
        line.toBack();
    }*/
}
