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

public class MenuAdminController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private Button DBSignUpButton;

    @FXML
    private Button employeeScheduleSignUpButton;

    @FXML
    private Button commentSignUpButton;

    @FXML
    private Button statisticsSignUpButton;

    @FXML
    void initialize() {
        // Переход назад
        BackUpButton.setOnAction(event -> loadScene("/START.fxml", "Косметический салон"));

        // Обработчики для других кнопок
        DBSignUpButton.setOnAction(event -> loadScene("/workWithDB.fxml", "Работа с БД"));
        commentSignUpButton.setOnAction(event -> loadScene("/viewComment.fxml", "Просмотр отзывов"));
        statisticsSignUpButton.setOnAction(event -> loadScene("/statisticMenu.fxml", "Статистика салона"));
        employeeScheduleSignUpButton.setOnAction(event -> loadScene("/workSchedule.fxml", "График сотрудников"));
    }


    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Получаем текущее окно
            Stage currentStage = (Stage) BackUpButton.getScene().getWindow();

            // Создаем новую сцену
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            // Вы можете добавить сообщение для пользователя о том, что не удалось загрузить сцену
        }
    }

}
