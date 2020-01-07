package UI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import network.Network;
import network.Node;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static network.Constants.Protocol.ADDRESS_LENGTH;

public class SybilAttackUI {
    @FXML
    Button attackBtn;
    @FXML
    ChoiceBox<String> nodeAddrs;

    private ObservableList<String> addressStrings;
    private short[][] listOfAddresses;
    private static ResultListener listener;

    public void start(Stage stage) throws Exception{
        if (Network.getNumOfNodes() == 0) {
            new Alert(Alert.AlertType.ERROR, "The number of nodes is zero, it is not possible to start an sybil attack", ButtonType.OK).show();
            return;
        }
        Parent root = FXMLLoader.load(getClass().getResource("SybilAttackUI.fxml"));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Metoden blockerar tråden som anropar den för att undvika att metoden getResult anropas innan det finns
     * ett resultat (result == null eller innehåller ett gammalt värde)
     * @throws Exception
     */
    public void showUI(ResultListener listener) throws Exception {
        if (listener == null)
            throw new NullPointerException("Listener must not be null");
        start(new Stage());
        SybilAttackUI.listener = listener;
    }

    public void onStartAttack() {
        listener.resultGenerated(listOfAddresses[nodeAddrs.getSelectionModel().getSelectedIndex()]);
        listener = null;
    }

    public void fillAddresses() {
        if (addressStrings == null)
            createAddressCollection();
        if (nodeAddrs.getItems().isEmpty()) {
            nodeAddrs.setItems(addressStrings);
        }
    }

    public void choiceboxShown() {
        fillAddresses();
        if (nodeAddrs.getSelectionModel().getSelectedIndex() < 0)
            nodeAddrs.getSelectionModel().selectFirst();
        nodeAddrs.hide();
        nodeAddrs.show();
        attackBtn.setDisable(false);
    }

    private void createAddressCollection() {
        Collection<Node> nodeList = Network.getNodeList().values();
        addressStrings = FXCollections.observableArrayList();
        listOfAddresses = new short[ADDRESS_LENGTH][nodeList.size()];
        int count = 0;
        for (Node node : nodeList) {
            String addressString = Arrays.toString(node.getAddress()).replace(", ", ".");
            addressStrings.add(addressString.substring(1, addressString.length()-1));
            listOfAddresses[count] = node.getAddress();
        }
    }

    @FunctionalInterface
    public interface ResultListener {
        void resultGenerated(short[] result);
    }
}
