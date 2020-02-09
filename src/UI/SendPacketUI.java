package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static network.Constants.Protocol.ADDRESS_LENGTH;
import static network.Constants.GUI.PACKET_GUI_Y;
import static network.Constants.GUI.PACKET_GUI_X;

public class SendPacketUI {
    @FXML
    ChoiceBox<String> sourceAddress;
    @FXML
    ChoiceBox<String> destAddress;
    @FXML
    TextField msgBox;

    private ObservableList<String> addressStrings;

    public void start(Stage stage) throws Exception {
        if (Network.getNumOfNodes() == 0) {
            new Alert(Alert.AlertType.ERROR, "The number of nodes is zero, it is not possible to send packets", ButtonType.OK).show();
            Event.fireEvent(stage, new WindowEvent(stage,WindowEvent.WINDOW_CLOSE_REQUEST));
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("PacketGUI.fxml"));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setX(PACKET_GUI_X);
        stage.setY(PACKET_GUI_Y);
        stage.show();
    }

    public void showUI(EventHandler<WindowEvent> winCloseCallBack) throws Exception {
        Stage stage = new Stage();
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, winCloseCallBack);
        start(stage);
    }

    public void fillSrcAddresses() {
        if (addressStrings == null)
            createAddressCollection();
        if (sourceAddress.getItems().isEmpty()) {
            sourceAddress.setItems(addressStrings);
            // En "work around" för att inte tvinga användaren att trycka två gånger första gången för att se listan över alternativ
            sourceAddress.hide();
            sourceAddress.show();
        }
    }

    public void fillDestAddresses() {
        if (addressStrings == null)
            createAddressCollection();
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
        String[] srcAddress = sourceAddress.getSelectionModel().getSelectedItem().split("\\.");
        String[] destination = destAddress.getSelectionModel().getSelectedItem().split("\\.");
        short[] src = stringsToShorts(srcAddress);
        short[] dest = stringsToShorts(destination);
        TFTPPacket tftpPacket = PacketGenerator.generateTFTPPacket(src, dest, msg);
        Objects.requireNonNull(Network.getNode(src)).receivePacket(tftpPacket);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Packet sent!", ButtonType.OK);
        alert.setHeaderText("Shipment of packet");
        alert.show();
    }

    private boolean emptyTextField() {
        return sourceAddress.getSelectionModel().getSelectedIndex() == -1 || destAddress.getSelectionModel().getSelectedIndex() == -1;
    }

    private void createAddressCollection() {
        Collection<Node> nodeList = Network.getNodeList();
        addressStrings = FXCollections.observableArrayList();
        short[][] listOfAddresses = new short[ADDRESS_LENGTH][nodeList.size()];
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
