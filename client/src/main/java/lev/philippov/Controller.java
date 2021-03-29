package lev.philippov;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextField msgField;
    @FXML
    TextArea textArea;
    @FXML
    HBox signInArea;

    Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}


    public void sendMsg(ActionEvent actionEvent) {
        network.sendMsg(msgField.getText());
        msgField.clear();
        msgField.requestFocus();
    }

    public void receiveMsg(String msg) {
        textArea.appendText(msg + "\n");
    }


    public void getConnectiontoServer(ActionEvent actionEvent) {
        this.network = new Network(this);
        List<Node> list = signInArea.getChildren();
        for (Node node : list) {
            if(node instanceof TextField) {
                ((TextField) node).clear();
            }
        }
        signInArea.setDisable(true);
    }
}
