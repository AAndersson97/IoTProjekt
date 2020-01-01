package UI;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import network.*;
import static network.Constants.GUI.*;

import java.util.ArrayList;

/**
 * Klass innehållandes metoder enbart för att felsöka programmet
 */
public class Debug {

    private static Rectangle[] rectangles;
    private static Circle[] circles;
    private static boolean areaActive = false;
    private static boolean showTA = false;
    private static Pane pane;
    public static void registerKeyEvents(Scene scene, SybilSimulator sybilSimulator) {
        pane = sybilSimulator.anchorPane;
        scene.setOnKeyPressed((key) -> {
            switch (key.getCode()) {
                case T:
                    showTA ^= true;
                    displayTransmissionAreas();
                    break;

            }
        });
    }

    private static void displayTransmissionAreas() {
        if (showTA) {
            pane.getChildren().addAll(circles);
        } else {
            pane.getChildren().removeAll(circles);
        }
    }

    private static void createTACircles() {
        ArrayList<Node> nodes = Network.getNodeList();
        circles = new Circle[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            Circle circle = new Circle();
            int circleRadius = nodes.get(i).getTransmissionRadius();
            circle.setFill(Color.web("#ffffff", 0.5));
            circle.setRadius(circleRadius);
            circle.setStroke(Color.web("#000000", 0.5));
            circle.relocate(nodes.get(i).getLocation().getX() - (circleRadius - 10), nodes.get(i).getLocation().getY() - (circleRadius - 10));
            circles[i] = circle;
        }
    }

    private static void displayAreas() {
        if (areaActive)
            pane.getChildren().removeAll(rectangles);
        else
            pane.getChildren().addAll(rectangles);
    }

    private static void createRectangles() {
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
        rectangles = new Rectangle[]{rectangle, rectangle2, rectangle3, rectangle4, rectangle5, rectangle6};
    }

}
