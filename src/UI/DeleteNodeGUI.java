package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import network.Network;
import network.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class DeleteNodeGUI {
    @FXML
    Button deleteBtn;
    @FXML
    Label deleteNodeLabel;
    @FXML
    ChoiceBox<String> choiceBox1;

    private ObservableList<String> addressStrings;
    private static RemoveListener listener;
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("DeleteNodeGUI.fxml"));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public DeleteNodeGUI() {
    }

    public void showUI(RemoveListener listener) throws Exception {
        DeleteNodeGUI.listener = listener;
        start(new Stage());
    }

    public void fillChoicebox() {
        if (addressStrings == null)
            createAddressCollection();
        if (choiceBox1.getItems().isEmpty()) {
            choiceBox1.setItems(addressStrings);
            choiceBox1.hide();
            choiceBox1.show();
        }
    }

    private void createAddressCollection() {
        Collection<Node> nodeList = Network.getNodeList();
        addressStrings = FXCollections.observableArrayList();
        for (Node node : nodeList) {
            String addressString = Arrays.toString(node.getAddress()).replace(", ", ".");
            addressStrings.add(addressString.substring(1, addressString.length()-1));
        }
    }

    public void onDeleteNode() {
        if (choiceBox1.getSelectionModel().getSelectedIndex() > -1) {
            String[] strAddr = choiceBox1.getSelectionModel().getSelectedItem().split("\\.");
            short[] addr = stringsToShorts(strAddr);
            Node node =  Objects.requireNonNull(Network.getNode(addr));
            node.remove();
            if (listener != null)
                listener.onRemove(node);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Node deleted!", ButtonType.OK);
            alert.setHeaderText("Deletion of node");
            alert.show();
            choiceBox1.getItems().remove(choiceBox1.getSelectionModel().getSelectedItem());
        }
    }

    public short[] stringsToShorts(String[] strings) {
        short[] numbers = new short[strings.length];
        for (int i = 0; i < strings.length; i++) {
            numbers[i] = Short.parseShort(strings[i]);
        }
        return numbers;
    }

    @FunctionalInterface
    public interface RemoveListener {
        void onRemove(Node node);
    }
}
