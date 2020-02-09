package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.stage.Stage;
import network.Network;
import network.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class SelectNodeGUI {
    @FXML
    ChoiceBox<String> choiceBox1;

    private ObservableList<String> addressStrings;
    private static NodeSelectedListener listener;
    private static ArrayList<Node> blackList;
    private static Stage stage; 

    public void start() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SelectNodeGUI.fxml"));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public SelectNodeGUI() {
    }

    public SelectNodeGUI(ArrayList<Node> blackList) {
        SelectNodeGUI.blackList = blackList;
        if (SelectNodeGUI.blackList == null)
            SelectNodeGUI.blackList = new ArrayList<>();
    }
    
    public void showUI(NodeSelectedListener listener) throws Exception {
        SelectNodeGUI.listener = listener;
        stage = new Stage(); 
        start();
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
            if (blackList.contains(node))
                continue;
            String addressString = Arrays.toString(node.getAddress()).replace(", ", ".");
            addressStrings.add(addressString.substring(1, addressString.length()-1));
        }
    }

    public void onNodeSelected() {
        if (choiceBox1.getSelectionModel().getSelectedIndex() > -1) {
            String[] strAddr = choiceBox1.getSelectionModel().getSelectedItem().split("\\.");
            short[] addr = stringsToShorts(strAddr);
            Node node =  Objects.requireNonNull(Network.getNode(addr));
            listener.onNodeSelected(node);
            stage.close();
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
    public interface NodeSelectedListener {
        void onNodeSelected(Node node);
    }
}
