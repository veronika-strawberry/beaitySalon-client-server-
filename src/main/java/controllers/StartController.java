package controllers;

import java.util.HashMap;
import java.util.Map;

import Salon.Role;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import ClientWork.Connect;
import Salon.Authorization;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/*вывод сообщений*/
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import util.Dialog;


public class StartController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginSignUpButton;

    @FXML
    private PasswordField passwordSignUpButton;

    @FXML
    private Button registrSignUpButton;

    @FXML
    private Button entranceSignUpButton;


    @FXML
    void initialize() {

        /*регистрация*/
        registrSignUpButton.setOnAction( event -> openRegistration());

        /*вход, авторизация*/
        entranceSignUpButton.setOnAction(event -> {
            try {
                authorization(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void openRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/registration.fxml"));
            Parent root = loader.load();
            // Получаем текущее окно
            Stage currentStage = (Stage) registrSignUpButton.getScene().getWindow();
            // Создаем новое окно для администратора
            Stage adminStage = new Stage();
            adminStage.setTitle("Регистрация");
            adminStage.setScene(new Scene(root));
            // Закрываем текущее окно
            currentStage.close();
            adminStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void authorization(ActionEvent event) throws IOException {
        if (checkInput())

            Dialog.showAlertWithNullInput();
        else {
            Connect.client.sendMessage("authorization");
            Authorization auth = new Authorization();/*Создается объект Authorization (предположительно, это класс,
             который вы определили для передачи данных о пользователе, таких как логин и пароль).*/
            auth.setLogin(loginSignUpButton.getText());
            auth.setPassword(passwordSignUpButton.getText());
            Connect.client.sendObject(auth);// отправляется на сервер с помощью метода

            String mes = "";
            try {//прочитать ответ от сервера. Этот ответ может быть сообщением об успехе или ошибке. Если чтение не удается, выводится сообщение об ошибке.
                mes = Connect.client.readMessage();
            } catch (IOException ex) {
                System.out.println("Error in reading");
            }
            if (mes.equals("There is no data!"))//указывая на то, что логин или пароль были неверными.
                Dialog.showAlertWithNoLogin();
            else {
                Role r = (Role) Connect.client.readObject();//считаем объект роль
                // id и role сохраняются в статических полях класса Connect для дальнейшего использования.
                Connect.id = r.getId();
                Connect.role = r.getRole();
                entranceSignUpButton.getScene().getWindow().hide();
                System.out.println(Connect.role);
                FXMLLoader loader = new FXMLLoader();

                //Загружаем соответствующее меню
                if (Objects.equals(Connect.role, "Consumer")) {
                    loader.setLocation(getClass().getResource("/menuClient.fxml"));
                }
                else if(Objects.equals(Connect.role, "Employee"))
                    loader.setLocation(getClass().getResource("/menuEmployee.fxml"));
                else
                    loader.setLocation(getClass().getResource("/menuAdmin.fxml"));
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene((root)));
                stage.show();
            }
        }
    }


    private boolean checkInput() {
        try {
            return loginSignUpButton.getText().equals("") || passwordSignUpButton.getText().equals("");
        } catch (Exception e) {
            System.out.println("Error");
            return true;
        }
    }
}