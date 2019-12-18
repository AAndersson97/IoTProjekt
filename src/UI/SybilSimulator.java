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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import network.*;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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

        Communication.getInstance().addPacketListener((packet) -> {
            System.out.println("Packet Added!");
        });

    }

    public void onCreateNode(ActionEvent actionEvent) {
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

        Rectangle rectangle = new Rectangle(0,0,WINDOW_WIDTH/3, (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2);
        Rectangle rectangle2 = new Rectangle(0,(WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2,WINDOW_WIDTH/3, (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2);
        Rectangle rectangle3 = new Rectangle(WINDOW_WIDTH/3,0,WINDOW_WIDTH/3, (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2);
        Rectangle rectangle4 = new Rectangle(WINDOW_WIDTH/3,(WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2,WINDOW_WIDTH/3, (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2);
        Rectangle rectangle5 = new Rectangle((WINDOW_WIDTH*2)/3,0,WINDOW_WIDTH/3, (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2);
        Rectangle rectangle6 = new Rectangle((WINDOW_WIDTH*2)/3,(WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2,WINDOW_WIDTH/3, (WINDOW_HEIGHT - CIRCLE_RADIUS*2)/2);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle2.setStroke(Color.BLACK);
        rectangle2.setFill(Color.TRANSPARENT);
        rectangle3.setStroke(Color.BLACK);
        rectangle3.setFill(Color.TRANSPARENT);
        rectangle4.setStroke(Color.BLACK);
        rectangle4.setFill(Color.TRANSPARENT);
        rectangle5.setStroke(Color.BLACK);
        rectangle5.setFill(Color.TRANSPARENT);
        rectangle6.setStroke(Color.BLACK);
        rectangle6.setFill(Color.TRANSPARENT);
        anchorPane.getChildren().addAll(rectangle, rectangle2, rectangle3, rectangle4, rectangle5, rectangle6);
    }

    public void onCreateAttackNode(ActionEvent actionEvent) {
        Circle displayedANode = create();
        displayedANode.setFill(Color.RED);
        Router createdARouter = new Router();
        anchorPane.getChildren().add((displayedANode));
        displayedANode.relocate(createdARouter.getLocation().getX(), createdARouter.getLocation().getY());
        createAttackNode.setDisable(Network.getNumOfNodes() >= MAX_NODES);
        Label nodeLabel = new Label(createdARouter.getAddress().getHostAddress());
        anchorPane.getChildren().add(nodeLabel);
        nodeLabel.relocate(createdARouter.getLocation().getX()-15, createdARouter.getLocation().getY()+20);
        Label nodeLabel2 = new Label("Attacknod");
        anchorPane.getChildren().add(nodeLabel2);
        nodeLabel2.relocate(createdARouter.getLocation().getX()-20, createdARouter.getLocation().getY()-20);
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
        //Thread thread = new Thread(()->{});
            /*for (Node node : Area.getInstance().getNodeList().values()) {
                ArrayList<Node> neighbours = ProximitySearcher.getInstance().findClosestNeighbours(node);
                for (Node neighbour : neighbours) {
                    packetLine(node.getLocation().getX() + 10, node.getLocation().getY() + 10, neighbour.getLocation().getX() + 10, neighbour.getLocation().getY() + 10);

                }
            }*/

        Socket socket;
        PrintWriter printWriter;
            try {
                socket = new Socket(InetAddress.getByAddress(new byte[]{(byte)150,0,0,0}), 9090);
                printWriter = new PrintWriter(socket.getOutputStream(),true);
                printWriter.println("HEJ");
            } catch (Exception e) {

            }



    }
    public void packetLine(int startX, int startY, int endX, int endY){
        Line line = new Line(startX,startY,endX,endY);
        anchorPane.getChildren().add(line);
        line.toBack();

    }
}
