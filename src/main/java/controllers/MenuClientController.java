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
import javafx.scene.control.Button;
import javafx.stage.Stage;
import util.WindowChanger;

public class MenuClientController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private Button accountSignUpButton;


    @FXML
    private Button reviewSignUpButton;

    @FXML
    private Button searchEmployeeSignUpButton;

    @FXML
    private Button viewServiceSignUpButton;

    @FXML
    void initialize() {

        BackUpButton.setOnAction(event -> handleBackButtonAction(event));

        accountSignUpButton.setOnAction(event -> handleAccountSignUpButtonAction(event));
        reviewSignUpButton.setOnAction(event -> handleReviewSignUpButtonAction(event));
        searchEmployeeSignUpButton.setOnAction(event -> handleSearchEmployeeSignUpButtonAction(event));

    }


   @FXML
    void showService(ActionEvent event) throws IOException {
        WindowChanger.changeWindow(getClass(), viewServiceSignUpButton, "/serviceList.fxml", "Просмотр услуг", false);
    }

    private void handleBackButtonAction(ActionEvent event) {
        navigateToScene("/START.fxml", "Косметический салон");
    }

    private void handleAccountSignUpButtonAction(ActionEvent event) {
        navigateToScene("/accountClient.fxml", "Личный кабинет клиента");
    }


    private void handleReviewSignUpButtonAction(ActionEvent event) {
        navigateToScene("/viewComment.fxml", "Оставить отзыв");
    }

    private void handleSearchEmployeeSignUpButtonAction(ActionEvent event) {
        navigateToScene("/searchEmployee.fxml", "Поиск сотрудника");
    }



    private void navigateToScene(String fxmlFilePath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();
            Stage currentStage = (Stage) BackUpButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
