package lev.philippov;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextField msgField;
    @FXML
    TextArea textArea;

    Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.network = new Network(this);
    }


    public void sendMsg(ActionEvent actionEvent) {
        network.sendMsg(msgField.getText());
        msgField.clear();
        msgField.requestFocus();
    }

    public void receiveMsg(String msg) {
        textArea.appendText(msg + "\n");
    }


}
