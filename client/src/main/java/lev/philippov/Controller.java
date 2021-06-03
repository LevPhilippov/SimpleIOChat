package lev.philippov;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField msgField, loginField, passField;
    @FXML
    private TextArea textArea;
    @FXML
    private HBox signInArea;
    @FXML
    private VBox area;
    @FXML
    private HBox changeNickBox;
    @FXML
    private TextField newnicknamefield;

    private Network network;

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
        //передача в меод идет парно по принципу ключ, значение;
        network.sendSrvsMsg("AUTH","false","login",loginField.getText().trim(), "password", DigestUtils.md5Hex(passField.getText()));
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
        textArea.appendText(network.loadHistory());
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
        changeNickBox.setDisable(true);
        changeNickBox.setVisible(false);
    }

    public void logOut() {
        changeAuthFlag();
        HIDupdate();
    }

    public void openChangeNicknamePanel(ActionEvent actionEvent) {
        changeNickBox.setDisable(false);
        changeNickBox.setVisible(true);
        newnicknamefield.requestFocus();
    }

    public void changeNick(ActionEvent actionEvent) {
        network.sendSrvsMsg("nickName",newnicknamefield.getText());
        changeNickBox.setDisable(true);
        changeNickBox.setVisible(false);

    }
}
