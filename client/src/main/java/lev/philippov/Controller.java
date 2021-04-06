package lev.philippov;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextField msgField, loginField, passField;
    @FXML
    TextArea textArea;
    @FXML
    HBox signInArea;
    @FXML
    VBox area;

    Network network;

    private boolean authFlag=false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HIDupdate();
    }


    public void sendMsg(ActionEvent actionEvent) {
        network.sendObj(msgField.getText());
        msgField.clear();
        msgField.requestFocus();
    }

    public void receiveMsg(String msg) {
        textArea.appendText(msg + "\n");
    }


    public void getConnectiontoServer(ActionEvent actionEvent) {
        this.network = new Network(this);
        network.sendAuthMsg(loginField.getText().trim(),passField.getText() );
    }

    public void logIn(){
        List<Node> list = signInArea.getChildren();
        for (Node node : list) {
            if(node instanceof TextField) {
                ((TextField) node).clear();
            }
        }
        changeAuthFlag();
        HIDupdate();
    }

    public void changeAuthFlag() {
        authFlag=!authFlag;
    }

    private void HIDupdate(){
        textArea.setDisable(!authFlag);
        msgField.setDisable(!authFlag);
        signInArea.setVisible(!authFlag);
        signInArea.setManaged(!authFlag);
        area.setVisible(authFlag);
        area.setManaged(authFlag);
    }

    public void logOut() {
        changeAuthFlag();
        HIDupdate();
    }
}
