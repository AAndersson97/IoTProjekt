package UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import network.AttackType;

public class SybilAttackTypeGUI {
    @FXML
    Button okBtn;
    @FXML
    RadioButton routingAttackRB;
    @FXML
    RadioButton votingAttackRB;

    private static AttackTypeSelectedListener listener;
    private static Stage stage;

    @FXML
    private void initialize() {
        ToggleGroup group = new ToggleGroup();
        routingAttackRB.setToggleGroup(group);
        votingAttackRB.setToggleGroup(group);
    }

    public void start() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("SybilAttackTypeGUI.fxml"));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public void showUI(AttackTypeSelectedListener listener) throws Exception {
        SybilAttackTypeGUI.listener = listener;
        stage = new Stage();
        start();
    }

    public void onOKBtnPressed () {
        listener.onAttackTypeSelected(routingAttackRB.isSelected() ? AttackType.ROUTING : AttackType.VOTING);
        stage.close();
    }

    @FunctionalInterface
    public interface AttackTypeSelectedListener {
        void onAttackTypeSelected(AttackType attackType);
    }
}
