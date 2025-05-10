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

public class MenuEmployeeController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private Button accountSignUpButton;

    @FXML
    private Button orderSignUpButton;

    @FXML
    private Button searchClientSignUpButton;

    @FXML
    private Button searchEmployeeSignUpButton;

    @FXML
    private Button viewScheduleSignUpButton;

    @FXML
    void initialize() {
        // Переход назад
        BackUpButton.setOnAction(event -> loadScene("/START.fxml", "Косметический салон"));

        // Переход на другие экраны
        accountSignUpButton.setOnAction(event -> loadScene("/AccountEmployee.fxml", "Личный кабинет"));


        viewScheduleSignUpButton.setOnAction(event -> handleScheduleSignUpButtonAction(event));
        searchClientSignUpButton.setOnAction(event -> handleSearchClientSignUpButtonAction(event));
        searchEmployeeSignUpButton.setOnAction(event -> handleSearchEmployeeSignUpButtonAction(event));
        orderSignUpButton.setOnAction(event -> handleOrderListSignUpButtonAction(event));
    }

    private void handleScheduleSignUpButtonAction(ActionEvent event) { /*Connect.client.sendMessage("GET_SCHEDULE_BY_EMPLOYEE");*/
        loadScene("/workSchedule.fxml", "Просмотр графика работы");
    }
    private void handleSearchClientSignUpButtonAction(ActionEvent event) {

        loadScene("/searchClient.fxml", "Поиск клиента");
    }

    private void handleOrderListSignUpButtonAction(ActionEvent event) {
/*        Connect.client.sendMessage("GET_ORDERS_BY_EMPLOYEE");*/
        loadScene("/OrderListEmployee.fxml", "Просмотр заявок");
    }
    private void handleSearchEmployeeSignUpButtonAction(ActionEvent event) {

        loadScene("/searchEmployee.fxml", "Поиск сотрудника");
    }
    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage currentStage = (Stage) BackUpButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки: можно показать сообщение пользователю
        }
    }

}
