package UI;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import network.*;

import java.io.IOException;

/**
 * Klass innehållandes metoder enbart för att felsöka programmet
 */
public class Debug implements Constants {

    private static Rectangle[] rectangles;
    private static boolean areaActive = false;
    private static Pane pane;
    public static void registerKeyEvents(Scene scene, SybilSimulator sybilSimulator) {
        createRectangles();
        pane = sybilSimulator.anchorPane;
        scene.setOnKeyPressed((key) -> {
            switch (key.getCode()) {
                case A:
                    displayAreas();
                    areaActive ^= true;
                    break;
                case S:
                    sendPacket();
                    break;

            }
        });
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

    private static void sendPacket() {
        Router router = Network.getArea(1).getFirstRouter();
        if (router == null)
            return;
        Network.sendPacket(router.getAddress(),router.getAddress(), getEmptyPacket(router));
}

    private static HelloPacket getEmptyPacket(Router router) {
        try {
            network.OSPFHeader ospfHeader = new OSPFHeader(OSPFPacketType.Hello, 0, router.getAreaId(), router.getAddress());
            return new HelloPacket(null, ospfHeader, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
