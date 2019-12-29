package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import network.*;
import network.IPHeader;
import network.old.IPPacket;
import network.old.TCPHeader;
import network.old.TCPPacket;

import java.io.IOException;
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
        stage.show();
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
        String msg = msgBox.getText().trim();
        int msgLength = msg.length();
        String[] srcAddress = sourceAddress.getSelectionModel().getSelectedItem().split("\\.");
        String[] destination = destAddress.getSelectionModel().getSelectedItem().split("\\.");
        short[] src = stringsToShorts(srcAddress);
        short[] dest = stringsToShorts(destination);
        IPHeader ipHeader = null;

        /*try {
            ipHeader = new IPHeader(msgLength, src,
                    dest, TCP_PROTOCOL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TCPHeader tcpHeader = new TCPHeader(0,0,0,0, DEFAULT_WIN_SIZE);

        TCPPacket packet = new TCPPacket(tcpHeader, msg.getBytes());
        IPPacket ipPacket = new IPPacket(ipHeader, packet);
        Network.sendPacket(dest, src, ipPacket);*/
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
