package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import ClientWork.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import util.WindowChanger;

public class WorkWithDBController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private Button workWithemployeeButton;

    @FXML
    private Button workWithclientButton;

    @FXML
    private Button workWithservicesButton;

    @FXML
    void initialize() {
        BackUpButton.setOnAction(event -> goBack());

    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) BackUpButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось вернуться: " + e.getMessage());
        }
    }

    @FXML
    void openWorkWithClientWindow(ActionEvent event) {
        System.out.println("openWorkWithClientWindow вызывается");
        try {

            WindowChanger.changeWindow(getClass(), workWithemployeeButton, "/WorkWithClientDB.fxml", "Работа с клиентами", false);
        } catch (IOException e) {
            System.out.println("Ошибка при открытии окна работы с клиентами: " + e.getMessage());
            e.printStackTrace();
        }
    }

  @FXML
  void openWorkWithEmployeeWindow(ActionEvent event) {
      System.out.println("openWorkWithEmployeeWindow вызывается");
      try {

          WindowChanger.changeWindow(getClass(), workWithemployeeButton, "/workWithEmployeeDB.fxml", "Работа с сотрудниками", false);
      } catch (IOException e) {
          System.out.println("Ошибка при открытии окна работы с сотрудниками: " + e.getMessage());
          e.printStackTrace();
      }
  }
    @FXML
    void openWorkWithServicesWindow(ActionEvent event) throws IOException {
        /*Connect.client.sendMessage("GET_ALL_SERVICE");*/
        WindowChanger.changeWindow(getClass(), workWithservicesButton, "/workWithServiceDB.fxml", "", false);
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
