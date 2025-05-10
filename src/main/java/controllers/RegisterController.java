package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import ClientWork.Connect;
import Salon.Consumer;
import Salon.Role;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import util.Dialog;

public class RegisterController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private TextField email_field;

    @FXML
    private TextField lastName_field;

    @FXML
    private TextField login_field;

    @FXML
    private TextField name_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button registerSignInButton;

    // Константы для стилей
    private static final String ERROR_STYLE = "-fx-border-color: #FF0000; -fx-border-width: 2px;";
    private static final String DEFAULT_STYLE = "";

    @FXML
    void initialize() {
        // Установка подсказок для полей ввода
        setupTooltips();

        // Сброс подсветки при изменении текста
        setupFieldListeners();

        // переход назад
        BackUpButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/START.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) BackUpButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Обработка регистрации
        registerSignInButton.setOnAction(event -> registerNewClient());
    }

    private void setupTooltips() {
        // Подсказки для полей ввода
        name_field.setTooltip(new Tooltip("Введите имя на русском языке (только буквы)"));
        lastName_field.setTooltip(new Tooltip("Введите фамилию на русском языке (только буквы)"));
        email_field.setTooltip(new Tooltip("Введите email в формате example@domain.com"));
        login_field.setTooltip(new Tooltip("Логин должен содержать от 4 до 20 символов (буквы, цифры, _)"));
        password_field.setTooltip(new Tooltip("Пароль должен содержать минимум 6 символов"));
    }

    private void setupFieldListeners() {
        // Слушатели для сброса подсветки при изменении текста
        name_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(name_field));
        lastName_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(lastName_field));
        email_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(email_field));
        login_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(login_field));
        password_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(password_field));
    }

    private void resetFieldStyle(TextField field) {
        field.setStyle(DEFAULT_STYLE);
    }

    private void resetFieldStyle(PasswordField field) {
        field.setStyle(DEFAULT_STYLE);
    }

    private void registerNewClient() {
        resetAllFieldStyles();

        if (validateInput()) {
            try {
                Consumer client = new Consumer();
                client.setSurname(lastName_field.getText().trim());
                client.setName(name_field.getText().trim());
                client.setEmail(email_field.getText().trim());
                client.setLogin(login_field.getText().trim());
                client.setPassword(password_field.getText().trim());

                Connect.client.sendMessage("registrationConsumer");
                Connect.client.sendObject(client);
                System.out.println("Запись отправлена");

                String response = Connect.client.readMessage();
                if (response.startsWith("SUCCESS")) {
                    Role r = (Role) Connect.client.readObject();
                    Connect.id = r.getId();
                    Connect.role = "Consumer";

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuClient.fxml"));
                    Parent root = loader.load();
                    Stage newStage = new Stage();
                    newStage.setScene(new Scene(root));
                    newStage.show();

                    Platform.runLater(() -> {
                        Stage currentStage = (Stage) registerSignInButton.getScene().getWindow();
                        currentStage.close();
                    });
                } else {

                        Dialog.showAlertWarning("Предупреждение при регистрации", "Логин уже существует. Пожалуйста, выберите другой.");

                }
            } catch (IOException e) {
                Dialog.showAlert("Ошибка соединения", "Ошибка соединения с сервером");
                e.printStackTrace();
            }
        }
    }


    private boolean validateInput() {
        boolean isValid = true;

        if (name_field.getText().isEmpty() || !Pattern.matches("^[А-ЯЁ][а-яё]+$", name_field.getText().trim())) {
            name_field.setStyle(ERROR_STYLE);
            isValid = false;
        }

        if (lastName_field.getText().isEmpty() || !Pattern.matches("^[А-ЯЁ][а-яё]+$", lastName_field.getText().trim())) {
            lastName_field.setStyle(ERROR_STYLE);
            isValid = false;
        }

        if (email_field.getText().isEmpty() ||
                !Pattern.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$", email_field.getText().trim())) {
            email_field.setStyle(ERROR_STYLE);
            isValid = false;
        }

        if (login_field.getText().isEmpty() ||
                !Pattern.matches("^[a-zA-Z0-9_]{1,20}$", login_field.getText().trim())) {
            login_field.setStyle(ERROR_STYLE);
            isValid = false;
        }

        if (password_field.getText().isEmpty() || password_field.getText().length() < 6) {
            password_field.setStyle(ERROR_STYLE);
            isValid = false;
        }

        if (!isValid) {
            Dialog.showAlert("Ошибка ввода", "Пожалуйста, проверьте введенные данные.\nНекорректно заполненные поля выделены красным.");
        }

        return isValid;
    }
    private void resetAllFieldStyles() {
        name_field.setStyle(DEFAULT_STYLE);
        lastName_field.setStyle(DEFAULT_STYLE);
        email_field.setStyle(DEFAULT_STYLE);
        login_field.setStyle(DEFAULT_STYLE);
        password_field.setStyle(DEFAULT_STYLE);
    }


}







