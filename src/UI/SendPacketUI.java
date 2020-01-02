package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import network.*;
import network.IPHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static network.Constants.Node.ADDRESS_LENGTH;

public class SendPacketUI {
    @FXML
    ChoiceBox<String> sourceAddress;
    @FXML
    ChoiceBox<String> destAddress;
    @FXML
    TextField msgBox;

    private short[][] listOfAddresses;
    private ObservableList<String> addressStrings;

    public void start(Stage stage) throws Exception {
        if (Network.getNumOfNodes() == 0) {
            new Alert(Alert.AlertType.ERROR, "The number of nodes is zero, it is not possible to send packets", ButtonType.OK).show();
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("PacketGUI.fxml"));
        stage.setScene(new Scene(root));
        stage.setX(1092);
        stage.setY(160);
        stage.show();
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,
                new ExitHandler());

    }

    class ExitHandler implements EventHandler<WindowEvent> {
        public void handle(WindowEvent event) {
            for(Label nL : SybilSimulator.getAddressLabels()){
                nL.setVisible(false);
            }
        }
    }

    public void showUI() throws Exception {
        start(new Stage());

    }

    public void fillSrcAddresses() {
        if (addressStrings == null)
            createAddressCollections();
        if (sourceAddress.getItems().isEmpty()) {
            sourceAddress.setItems(addressStrings);
            // En "work around" för att inte tvinga användaren att trycka två gånger första gången för att se listan över alternativ
            sourceAddress.hide();
            sourceAddress.show();
        }
    }

    public void fillDestAddresses() {
        if (addressStrings == null)
            createAddressCollections();
        if (destAddress.getItems().isEmpty()) {
            destAddress.setItems(addressStrings);
            // En "work around" för att inte tvinga användaren att trycka två gånger första gången för att se listan över alternativ
            destAddress.hide();
            destAddress.show();
        }
    }

    public void onSendPacket() {
        if (emptyTextField()) {
            new Alert(Alert.AlertType.ERROR, "A sender and a receiver must be specified", ButtonType.OK).show();
            return;
        }
        String msg = msgBox.getText().trim();
        int msgLength = msg.length();
        String[] srcAddress = sourceAddress.getSelectionModel().getSelectedItem().split("\\.");
        String[] destination = destAddress.getSelectionModel().getSelectedItem().split("\\.");
        short[] src = stringsToShorts(srcAddress);
        short[] dest = stringsToShorts(destination);
        IPHeader ipHeader = null;
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Packet sent!", ButtonType.OK);
        alert.setHeaderText("Shipment of packet");
        alert.show();
    }

    private boolean emptyTextField() {
        return sourceAddress.getSelectionModel().getSelectedIndex() == -1 || destAddress.getSelectionModel().getSelectedIndex() == -1;
    }

    private void createAddressCollections() {
        List<Node> nodeList = Network.getNodeList();
        addressStrings = FXCollections.observableArrayList();
        listOfAddresses = new short[ADDRESS_LENGTH][nodeList.size()];
        int count = 0;
        for (Node node : nodeList) {
            String addressString = Arrays.toString(node.getAddress()).replace(", ", ".");
            addressStrings.add(addressString.substring(1, addressString.length()-1));
            listOfAddresses[count] = node.getAddress();
        }
    }

    public short[] stringsToShorts(String[] strings) {
        short[] numbers = new short[strings.length];
        for (int i = 0; i < strings.length; i++) {
            numbers[i] = Short.parseShort(strings[i]);
        }
        return numbers;
    }
}
