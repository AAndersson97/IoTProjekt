package UI;

import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import network.*;
import static network.Constants.GUI.*;
import static network.Constants.Node.NUM_OF_SYBIL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class SybilSimulator extends Application {

    @FXML
    MenuBar menuDisplay;
    @FXML
    MenuItem TAMenu;
    @FXML
    MenuItem TAMenu2;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Button sendPacketBtn;
    @FXML
    Button createNode;
    @FXML
    Button sybilAttack;

    private static ArrayList<Circle> taCircles;


    private static ArrayList<Label> addressLabels = new ArrayList<>();

    public static ArrayList<Label> getAddressLabels() {
        return addressLabels;
    }

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
        anchorPane.getChildren().add(taCircle);
    }

    public void onStartSybilAttack() {
        AttackNode createdNode = new AttackNode(NUM_OF_SYBIL);
        anchorPane.getChildren().add(createNodeCircle(createdNode, Color.web("#db3a42")));
        Circle aTACircle = createTACircle(createdNode);
        anchorPane.getChildren().add(aTACircle);
        for (SybilNode node : createdNode.getSybilNodes()){
            anchorPane.getChildren().add(createNodeCircle(node, Color.web("#cfc7c0")));
            Circle taCircle = createTACircle(node);
            anchorPane.getChildren().add(taCircle);
        }

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
            SendPacketUI sPUI = new SendPacketUI();
            sendPacketBtn.disableProperty().bindBidirectional(sPUI.getIsActive());
            System.out.println("Open");
            sPUI.showUI();
            System.out.println("Closed");
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
    public void showTAs() {
        for (Circle circle : taCircles)
            circle.setVisible(true);
        TAMenu.setText("Show Transmission Areas");
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
        circle.setVisible(false);
        taCircles.add(circle);
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
