package UI;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.*;
import static network.Constants.GUI.*;
import static network.Constants.Node.MAX_NODES;
import static network.Constants.Node.NUM_OF_SYBIL;
import java.util.*;
import java.util.function.Predicate;

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
    MenuItem helloPacketMenu;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Button sendPacketBtn;
    @FXML
    Button createNode;
    @FXML
    Button sybilAttack;
    @FXML
    Button deleteNode;

    private static ArrayList<javafx.scene.shape.Circle> taCircles;
    private static ArrayList<javafx.scene.shape.Circle> nodeCircles;
    private static javafx.scene.shape.Circle taVisible; // true om överföringsareor är synliga
    private static boolean IPVisible;
    private static boolean taMsgShown;
    public static int packetTransportDelay;
    public static boolean showHelloPackets;
    public static boolean ongoingAttack;
    private static ArrayList<Label> addressLabels = new ArrayList<>();
    private static SybilSimulator fxmlInstance;
    private static AnchorPane root;
    private static AttackNode attackNode;

    // En metod som ska köras när användaren vill visa överföringsräckvidd för varje nod
    public static EventHandler<MouseEvent> showTA = (event) -> {
        for (int i = 0; i < nodeCircles.size(); i++) {
            if (nodeCircles.get(i).isHover()) {
                if (taVisible != null)
                    taVisible.setVisible(false);
                taVisible = taCircles.get(i);
                taVisible.setVisible(true);
            }
        }
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainGUI.fxml"));
        root = fxmlLoader.load();
        fxmlInstance = fxmlLoader.getController();
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.setResizable(false);
        stage.show();
        taCircles = new ArrayList<>();
        nodeCircles = new ArrayList<>();
        packetTransportDelay = 3000;
        showHelloPackets = true;
        PacketLocator.registerLocationListener(createLocationCallback());
        PacketLocator.registerPacketDroppedListener(createDroppedPacketCallback());
    }

    public void onCreateNode() {
        sybilAttack.setDisable(false);
        deleteNode.setDisable(false);
        if (helloPacketMenu.isDisable())
            helloPacketMenu.setDisable(false);
        if (Network.getNumOfNodes() >= MAX_NODES - 5)
            createNode.setDisable(true);
        TAMenu.setDisable(false);
        IPMenu.setDisable(false);
        Node createdNode = new Node();
        Label nodeLabel = createAddressLabel(createdNode);
        addressLabels.add(nodeLabel);
        javafx.scene.shape.Circle taCircle = createTACircle(createdNode);
        anchorPane.getChildren().addAll(createNodeCircle(createdNode, Color.web("#7ac5cd")), taCircle, nodeLabel);
    }

    public void onDeleteNode() {
        if (Network.getNumOfNodes() > 0)
            sybilAttack.setDisable(false);
        try {
            showIPAddresses();
            new DeleteNodeGUI().showUI((node) ->
                    fxmlInstance.anchorPane.getChildren().removeIf(removePaneNode(node)));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "An error occured while trying to show the delete node GUI", ButtonType.OK).show();
        }
        deleteNode.setDisable(Network.getNumOfNodes() == 0);
    }

    private Predicate<javafx.scene.Node> removePaneNode(Node node) {
        return (paneNode) -> (paneNode.getId() != null && paneNode.getId().equalsIgnoreCase(node.getAddress()[3] + ""));
    }

    public void onStartSybilAttack() {
        if (ongoingAttack) {
            stopSybilAttack();
            return;
        } else {
            sybilAttack.setText("Stop Sybil Attack");
        }
        try {
            new SybilAttackTypeGUI().showUI(this::startSybilAttack);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "An error occured while trying to show the select attack type GUI", ButtonType.OK).show();
        }
        sybilAttack.setDisable(Network.getNumOfNodes() == 0);
    }

    private void startSybilAttack(AttackType attackType) {
        attackNode = new AttackNode(NUM_OF_SYBIL, attackType);
        anchorPane.getChildren().add(createNodeCircle(attackNode, Color.web("#db3a42")));
        javafx.scene.shape.Circle tACircle = createTACircle(attackNode);
        Label nodeLabel = createAddressLabel(attackNode);
        anchorPane.getChildren().addAll(tACircle, nodeLabel);
        addressLabels.add(nodeLabel);
        for (SybilNode node : attackNode.getSybilNodes()){
            addressLabels.add(createAddressLabel(node));
            nodeLabel = createAddressLabel(node);
            anchorPane.getChildren().addAll(createNodeCircle(node, Color.web("#cfc7c0")), nodeLabel);
            addressLabels.add(nodeLabel);
        }
        if (attackType == AttackType.VOTING) {
            try {
                ArrayList<Node> blackList = new ArrayList<>();
                blackList.add(attackNode);
                blackList.addAll(Arrays.asList(attackNode.getSybilNodes()));
                new SelectNodeGUI(blackList).showUI(this::showVoteMessage);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "An error occured while trying to show the select node GUI", ButtonType.OK).show();
                stopSybilAttack();
            }
        }
        else {
            new Alert(Alert.AlertType.INFORMATION, "The sybil nodes will now disrupt the packet routing.", ButtonType.OK).show();
            ongoingAttack = true;
        }
    }

    private void stopSybilAttack() {
        attackNode.shutdown();
        sybilAttack.setText("Start Sybil Attack");
        ongoingAttack = false;
        int attackNodeid = attackNode.getAddress()[3];
        int[] sybilIds = attackNode.getSybilNodeIds();
        anchorPane.getChildren().removeIf(node ->  node.getId() != null && (node.getId().equalsIgnoreCase(attackNodeid + "") || findId(node.getId(), sybilIds)));
        attackNode = null;
    }

    private boolean findId(String id, int[] ids) {
        for (int num : ids)
            if (id.equalsIgnoreCase(num + ""))
                return true;

        return false;
    }

    private void showVoteMessage(Node node) {
        MisbehaviourVoting voting = new MisbehaviourVoting();
        MisbehaviourVoting.VotingResult votingResult = voting.startVoting(node);
        String message = "Number of votes in favor of excluding the node " + node.getAddressString() +
                " from the network as a consequence of misbehaviour: " + votingResult.numOfAgree + "\nNumber of votes against: " + votingResult.numOfDisagree;
        if (votingResult.numOfAgree > votingResult.numOfDisagree) {
            message += "\nThe node will be excluded from the network, the majority of the nodes are in favor of the exclusion.";
            Simulator.scheduleTask(node::disconnect);
            anchorPane.getChildren().removeIf(paneNode -> paneNode.getId() != null && paneNode.getId().equalsIgnoreCase(node.getAddress()[3] + ""));
        } else if (votingResult.numOfAgree == votingResult.numOfDisagree) {
            message += "\nThe node will not be excluded from the network, no majority was either against or in favor of the exclusion.";
        }
        else
            message += "\nThe node will not be excluded from the network, the majority of the nodes are against the exclusion.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText("Voting about misbehaviour");
        alert.show();
        stopSybilAttack();
    }

    /**
     * Skapar ett Label-objekt innehållandes en sträng med nodens IP-address
     * @param node Noden vars IP-address ska sparas i ett Label-objekt
     * @return
     */
    private Label createAddressLabel(Node node) {
        String address = Arrays.toString(node.getAddress()).replace(", ", ".");
        Label nodeLabel = new Label(address.substring(1, address.length()-1));
        nodeLabel.setId(node.getAddress()[3] + "");
        nodeLabel.relocate(node.getLocation().getX()-23,node.getLocation().getY()+12);
        nodeLabel.setVisible(IPVisible);
        return nodeLabel;
    }

    public javafx.scene.shape.Circle createNodeCircle(Node node, Color fill) {
        javafx.scene.shape.Circle circle = new Circle();
        circle.setFill(fill);
        circle.setRadius(CIRCLE_RADIUS);
        circle.setStroke(Color.BLACK);
        circle.relocate(node.getLocation().getX() - CIRCLE_RADIUS, node.getLocation().getY() - CIRCLE_RADIUS);
        circle.setId(node.getAddress()[3] + "");
        if (!fill.equals(Color.web("#cfc7c0")))
            nodeCircles.add(circle);
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
        if (!taMsgShown) {
            new Alert(Alert.AlertType.INFORMATION, "Hover the mouse over the node for which you want to display the transmission area", ButtonType.OK).show();
            taMsgShown = true;
        }
        if (taVisible == null) {
            anchorPane.setOnMouseMoved(showTA);
        } else {
            taVisible.setVisible(false);
            taVisible = null;
            anchorPane.setOnMouseMoved(null);
        }
        TAMenu.setText(TAMenu.getText().equals("Show Transmission Area")? "Hide Transmission Area" : "Show Transmission Area" );
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

    private static void removeAttackNodeCircles(short[] attackNode) {
        Platform.runLater(() -> {
            Iterator<javafx.scene.Node> iterator = root.getChildren().iterator();
            String address = Arrays.toString(attackNode).replace(", ", ".");
            while (iterator.hasNext()) {
                javafx.scene.Node node = iterator.next();
                if (node instanceof javafx.scene.shape.Circle) {
                    javafx.scene.shape.Circle circle = (javafx.scene.shape.Circle) node;
                    if (circle.getFill().equals(Color.web("#db3a42")) || circle.getFill().equals(Color.web("#cfc7c0"))) {
                        iterator.remove();
                        iterator.next();
                        iterator.remove();
                    }
                } else if (node instanceof Label) {
                    Label label = (Label) node;
                    if (label.getText().equals(address.substring(1, address.length() - 1)))
                        iterator.remove();
                }
            }
            ongoingAttack = false;
        });
    }

    private static javafx.scene.shape.Circle createTACircle(Node node) {
        javafx.scene.shape.Circle circle = new Circle(node.getAddress()[3]);
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
        LogWriter.getInstance().writeToLog();
        System.exit(0);
        super.stop();
    }

    public PacketLocator.LocationListener createLocationCallback() {
        return (start, end, packet) -> Platform.runLater(() -> {
            javafx.scene.shape.Circle newCircle = new javafx.scene.shape.Circle(2, Color.BLUE);
            root.getChildren().add(newCircle);
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
            if (packet instanceof TFTPPacket) {
                newCircle.setRadius(4);
                newCircle.setFill(Color.DARKSEAGREEN);
                Line textLine = new Line();
                textLine.setStartX(start.getX() - 15);
                textLine.setStartY(start.getY() - 15);
                textLine.setEndX(end.getX() - 15);
                textLine.setEndY(end.getY() - 15);
                PathTransition textTransition = new PathTransition();
                textTransition.setDuration(Duration.millis(packetTransportDelay));
                textTransition.setPath(textLine);
                textTransition.setCycleCount(1);
                textTransition.play();
            }
            transition.play();
            Simulator.scheduleFutureTask(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> root.getChildren().remove(newCircle));
                }
            }, packetTransportDelay);
        });
    }

    public PacketLocator.PacketDroppedListener createDroppedPacketCallback() {
        return ((node) -> Platform.runLater(() -> {
            javafx.scene.shape.Circle newCircle = new javafx.scene.shape.Circle(2, Color.RED);
            root.getChildren().add(newCircle);
            Line newLine = new Line();
            newLine.setStartX(node.getLocation().getX());
            newLine.setStartY(node.getLocation().getY());
            newLine.setEndX(node.getLocation().getX());
            newLine.setEndY(node.getLocation().getY()+20);
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
            Simulator.scheduleFutureTask(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> root.getChildren().remove(newCircle));
                }
            }, packetTransportDelay);
        }));
    }
    public void sliderDragged(){
        packetTransportDelay = (int)slider.getValue();
    }

    public void toggleHelloPackets() {
        showHelloPackets ^= true;
        helloPacketMenu.setText((showHelloPackets? "Hide" : "Show") + " Hello Packets");
    }
}
