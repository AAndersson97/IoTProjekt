package UI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import network.Network;
import network.Router;

import java.util.ArrayList;

public class SendPacketUI {
    @FXML
    ChoiceBox sourceAddress;
    @FXML
    ChoiceBox destAddress;
    @FXML
    TextField msgBox;

    private ObservableList<Router> destAddresses;

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PacketGUI.fxml"));
        stage.setScene(new Scene(root));
        destAddresses = FXCollections.observableArrayList();
        //fillChoiceBoxes();
        stage.show();
    }

    public void showUI() throws Exception {
        start(new Stage());
    }

    private void fillChoiceBoxes() {
        ArrayList<Router> routerList = Network.getNodeList();
        destAddresses.addAll(routerList);
        destAddress.setItems(FXCollections.observableArrayList(routerList));
    }
}
