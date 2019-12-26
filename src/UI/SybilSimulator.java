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

public class SybilSimulator extends Application implements Constants {
    @FXML
    AnchorPane anchorPane;
    @FXML
    Button sendPacketBtn;
    @FXML
    Button createNode;
    @FXML
    Button sybilAttack;

    static SybilSimulator instance;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainGUI.fxml"));
        stage.setTitle(Constants.WINDOW_TITLE);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        Debug.registerKeyEvents(stage.getScene(), instance);
        instance = null;
        stage.show();
    }

    public SybilSimulator() {
        instance = this;
    }

    public void onCreateNode() {
        Circle displayedNode = create();
        Router createdRouter = new Router();
        anchorPane.getChildren().add(displayedNode);
        displayedNode.relocate(createdRouter.getLocation().getX(), createdRouter.getLocation().getY());
        createNode.setDisable(Network.getNumOfNodes() >= MAX_NODES);
        //Label nodeLabel = new Label(createdNode.addressToString());
        //anchorPane.getChildren().add(nodeLabel);
        //nodeLabel.relocate(createdNode.getLocation().getX()-13,createdNode.getLocation().getY()+20);
    }

    public void onStartSybilAttack(ActionEvent actionEvent) {
        /*for(int i=0;i<3;i++){
            Rectangle displayedSybilNode = createRectangle();
            Node createdSNode = new Node();
            anchorPane.getChildren().add(displayedSybilNode);
            displayedSybilNode.relocate((createdSNode.getLocation().getX()),createdSNode.getLocation().getY());
        }*/
    }

    public void onCreateAttackNode(ActionEvent actionEvent) {
        Circle displayedANode = create();
        displayedANode.setFill(Color.web("#e84723"));
        Router createdARouter = new Router();
        anchorPane.getChildren().add((displayedANode));
        displayedANode.relocate(createdARouter.getLocation().getX(), createdARouter.getLocation().getY());
        Label nodeLabel = new Label(createdARouter.getAddress().getHostAddress());
        anchorPane.getChildren().add(nodeLabel);
        nodeLabel.relocate(createdARouter.getLocation().getX()-15, createdARouter.getLocation().getY()+20);
        Label nodeLabel2 = new Label("Attacknod");
        anchorPane.getChildren().add(nodeLabel2);
        nodeLabel2.relocate(createdARouter.getLocation().getX()-20, createdARouter.getLocation().getY()-20);
    }

    public Circle create() {
        Circle circle = new Circle();
        circle.setFill(Color.web("#7ac5cd"));
        circle.setRadius(10);
        circle.setStroke(Color.BLACK);
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
