package UI;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.*;
import static network.Constants.GUI.*;
import static network.Constants.Node.NUM_OF_SYBIL;

import java.util.ArrayList;
import java.util.Arrays;

public class SybilSimulator extends Application {

    @FXML
    Slider slider;
    @FXML
    MenuBar menuDisplay;
    @FXML
    MenuItem TAMenu;
    @FXML
    MenuItem IPMenu;
    @FXML
    MenuItem animate;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Button sendPacketBtn;
    @FXML
    Button createNode;
    @FXML
    Button sybilAttack;

    private static ArrayList<Circle> taCircles;
    private static boolean TAVisible; // true om överföringsareor är synliga
    private static boolean IPVisible;
    public static int packetTransportDelay;

    private static ArrayList<Label> addressLabels = new ArrayList<>();

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
        TAMenu.setDisable(false);
        IPMenu.setDisable(false);
        Node createdNode = new Node();
        Label nodeLabel = createAddresslabel(createdNode);
        addressLabels.add(nodeLabel);
        Circle taCircle = createTACircle(createdNode);
        anchorPane.getChildren().addAll(createNodeCircle(createdNode, Color.web("#7ac5cd")),taCircle, nodeLabel);
    }

    public void onStartSybilAttack() {
        TAMenu.setDisable(false);
        IPMenu.setDisable(false);
        AttackNode createdNode = new AttackNode(NUM_OF_SYBIL);
        anchorPane.getChildren().add(createNodeCircle(createdNode, Color.web("#db3a42")));
        Circle tACircle = createTACircle(createdNode);
        anchorPane.getChildren().add(tACircle);
        for (SybilNode node : createdNode.getSybilNodes()){
            addressLabels.add(createAddresslabel(node));
            Circle taCircle = createTACircle(node);
            anchorPane.getChildren().addAll(createNodeCircle(node, Color.web("#cfc7c0")), taCircle);
        }
    }

    /**
     * Skapar ett Label-objekt innehållandes en sträng med nodens IP-address
     * @param node Noden vars IP-address ska sparas i ett Label-objekt
     * @return
     */
    private Label createAddresslabel(Node node) {
        String address = Arrays.toString(node.getAddress()).replace(", ", ".");
        Label nodeLabel = new Label(address.substring(1, address.length()-1));
        nodeLabel.relocate(node.getLocation().getX()-23,node.getLocation().getY()+12);
        nodeLabel.setVisible(IPVisible);
        return nodeLabel;
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
            sendPacketBtn.setDisable(true);
            sPUI.showUI(windowEvent -> {
                toggleIPAddresses();
                sendPacketBtn.setDisable(false);
            });
            showIPAddresses();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Visa överföringsområde för varje nod om de är dolda annars dölj områden
     */
    public void toggleTAs() {
        TAVisible ^= true;
        for (Circle circle : taCircles)
            circle.setVisible(TAVisible);
        TAMenu.setText(TAVisible ? "Hide Transmission Areas" : "Show Transmission Areas" );
    }

    public void toggleIPAddresses() {
        IPVisible ^= true;
        for(Label nL : addressLabels)
            nL.setVisible(IPVisible);
        IPMenu.setText(IPVisible ? "Hide IP addresses" : "Show IP addresses");
    }

    public void showIPAddresses() {
        IPVisible = false;
        toggleIPAddresses();
    }

    private static Circle createTACircle(Node node) {
        Circle circle = new Circle();
        int circleRadius = node.getTransmissionRadius();
        circle.setFill(Color.web("#ffffff", 0.5));
        circle.setRadius(circleRadius);
        circle.setStroke(Color.web("#000000", 0.5));
        circle.relocate(node.getLocation().getX() - (circleRadius - 10) - CIRCLE_RADIUS, node.getLocation().getY() - (circleRadius - 10) - CIRCLE_RADIUS);
        circle.setViewOrder(1);
        circle.setVisible(TAVisible);
        taCircles.add(circle);
        return circle;
    }

    @Override
    public void stop() throws Exception {
        Network.shutdownNetwork();
        Simulator.shutdown();
        super.stop();
    }
    public void animatePath(){
        PacketLocator.registerListener((start, end)-> {
            Circle newCircle = new Circle(2, Color.BLUE);
            anchorPane.getChildren().add(newCircle);
            Line newLine = new Line();
            newLine.setStartX(start.getX());
            newLine.setStartY(start.getY());
            newLine.setEndX(end.getX());
            newLine.setEndY(end.getY());
            PathTransition transition = new PathTransition();
            transition.setNode(newCircle);
            transition.setDuration(Duration.millis(packetTransportDelay));
            transition.setPath(newLine);
            transition.setCycleCount(1);
            transition.play();
        });

    }

    public void animateDroppedPath(){
        Circle newCircle = new Circle(2, Color.RED);
        ArrayList<Node> test = Network.getNodeList();
        anchorPane.getChildren().add(newCircle);
        Line newLine = new Line();
        newLine.setStartX(test.get(0).getLocation().getX());
        newLine.setStartY(test.get(0).getLocation().getY());
        newLine.setEndX(test.get(0).getLocation().getX());
        newLine.setEndY(test.get(0).getLocation().getY()+20);
        PathTransition transition = new PathTransition();
        transition.setNode(newCircle);
        transition.setDuration(Duration.millis(500));
        transition.setPath(newLine);
        transition.setCycleCount(1);
        FadeTransition fadeTransition =
                new FadeTransition(Duration.millis(1000), newCircle);
        fadeTransition.setFromValue(1.0f);
        fadeTransition.setToValue(0.0f);
        transition.play();
        fadeTransition.play();
    }
    public void sliderDragged(){
        packetTransportDelay = ((int)slider.getValue())*10;

    }
}
