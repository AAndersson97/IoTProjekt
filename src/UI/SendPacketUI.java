package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import network.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class SendPacketUI implements Constants {
    @FXML
    ChoiceBox<String> sourceAddress;
    @FXML
    ChoiceBox<String> destAddress;
    @FXML
    TextField msgBox;

    SendPacketUI instance;

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("PacketGUI.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public SendPacketUI() {
        instance = this;
    }

    public void showUI() throws Exception {
        start(new Stage());
    }

    public void fillSrcAddresses() {
        if (sourceAddress.getItems().isEmpty()) {
            ArrayList<Router> routerList = Network.getNodeList();
            ObservableList<Router> srcAddresses = FXCollections.observableArrayList(routerList);
            sourceAddress.setItems(createAddressCollection(srcAddresses));
            // En "work around" för att inte tvinga användaren att trycka två gånger första gången för att se listan över alternativ
            sourceAddress.hide();
            sourceAddress.show();
        }
    }

    public void fillDestAddresses() {
        if (destAddress.getItems().isEmpty()) {
            ArrayList<Router> routerList = Network.getNodeList();
            ObservableList<Router> destAddresses = FXCollections.observableArrayList(routerList);
            destAddress.setItems(createAddressCollection(destAddresses));
            // En "work around" för att inte tvinga användaren att trycka två gånger första gången för att se listan över alternativ
            destAddress.hide();
            destAddress.show();
        }
    }

    public void onSendPacket() {
        String msg = msgBox.getText().trim();
        int msgLength = msg.length();
        String[] srcAddress = sourceAddress.getSelectionModel().getSelectedItem().split(".");
        String[] destination = destAddress.getSelectionModel().getSelectedItem().split(".");
        byte[] srcBytes = stringsToBytes(srcAddress);
        IPHeader ipHeader = null;
        TCPHeader tcpHeader = null;
        try {
            ipHeader = new IPHeader(msgLength, srcBytes,
                    stringsToBytes(destination), TCP_PROTOCOL);
            tcpHeader = new TCPHeader(0,0,0,0, DEFAULT_WIN_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TCPPacket packet = new TCPPacket(tcpHeader, msg.getBytes());
        //Network.sendPacket(InetAddress.getByAddress(srcBytes),,packet);
    }

    private ObservableList<String> createAddressCollection(List<Router> routerList) {
        ObservableList<String> addresses = FXCollections.observableArrayList();
        for (Router router : routerList)
            addresses.add(router.getAddress().getHostAddress());
        return addresses;
    }

    public byte[] stringsToBytes(String[] strings) {
        byte[] bytes = new byte[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Byte.decode(strings[i]);
        }
        return bytes;
    }
}
