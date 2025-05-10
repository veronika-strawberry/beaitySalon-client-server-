

package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import ClientWork.Connect;
import Salon.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import util.Dialog;

public class AccountCLientController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private Button email_change;

    @FXML
    private TextField email_field;

    @FXML
    private Button login_change;

    @FXML
    private TextField login_field;

    @FXML
    private Button name_change;

    @FXML
    private TextField name_field;

    @FXML
    private Button password_change;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button surname_change;

    @FXML
    private TextField surname_field;

    // Стили для подсветки
    private static final String ERROR_STYLE = "-fx-border-color: #FF0000; -fx-border-width: 2px;";
    private static final String DEFAULT_STYLE = "";

    @FXML
    void initialize() {
        setupTooltips();
        setupFieldListeners();

        email_change.setOnAction(event -> updateField("email"));
        login_change.setOnAction(event -> updateField("login"));
        name_change.setOnAction(event -> updateField("name"));
        password_change.setOnAction(event -> updateField("password"));
        surname_change.setOnAction(event -> updateField("surname"));
    }

    private void setupTooltips() {
        name_field.setTooltip(new Tooltip("Введите имя на русском языке (только буквы)"));
        surname_field.setTooltip(new Tooltip("Введите фамилию на русском языке (только буквы)"));
        email_field.setTooltip(new Tooltip("Введите email в формате example@domain.com"));
        login_field.setTooltip(new Tooltip("Логин должен содержать от 4 до 20 символов (буквы, цифры, _)"));
        password_field.setTooltip(new Tooltip("Пароль должен содержать минимум 6 символов"));
    }

    private void setupFieldListeners() {
        name_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(name_field));
        surname_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(surname_field));
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

    private boolean validateField(String fieldType, String value) {
        switch (fieldType) {
            case "name":
            case "surname":
                return Pattern.matches("^[А-ЯЁ][а-яё]+$", value);
            case "email":
                return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", value);
            case "login":
                return Pattern.matches("^[a-zA-Z0-9_]{4,20}$", value);
            case "password":
                return value.length() >= 6;
            default:
                return false;
        }
    }

    private String getValidationMessage(String fieldType, String value) {
        if (value.isEmpty()) {
            return "Поле не может быть пустым";
        }

        switch (fieldType) {
            case "name":
                return "Имя должно начинаться с заглавной буквы и содержать только русские буквы";
            case "surname":
                return "Фамилия должна начинаться с заглавной буквы и содержать только русские буквы";
            case "email":
                return "Email должен быть в формате example@domain.com";
            case "login":
                return "Логин должен содержать от 4 до 20 символов (буквы, цифры, _)";
            case "password":
                return "Пароль должен содержать минимум 6 символов";
            default:
                return "Некорректное значение поля";
        }
    }

    private void updateField(String field) {
        resetAllFieldStyles();

        String value = "";
        boolean isValid = true;
        String errorMessage = "";

        switch (field) {
            case "email":
                value = email_field.getText();
                if (value.isEmpty() || !validateField(field, value)) {
                    email_field.setStyle(ERROR_STYLE);
                    isValid = false;
                    errorMessage = getValidationMessage(field, value);
                }
                break;
            case "login":
                value = login_field.getText();
                if (value.isEmpty() || !validateField(field, value)) {
                    login_field.setStyle(ERROR_STYLE);
                    isValid = false;
                    errorMessage = getValidationMessage(field, value);
                }
                break;
            case "name":
                value = name_field.getText();
                if (value.isEmpty() || !validateField(field, value)) {
                    name_field.setStyle(ERROR_STYLE);
                    isValid = false;
                    errorMessage = getValidationMessage(field, value);
                }
                break;
            case "password":
                value = password_field.getText();
                if (value.isEmpty() || !validateField(field, value)) {
                    password_field.setStyle(ERROR_STYLE);
                    isValid = false;
                    errorMessage = getValidationMessage(field, value);
                }
                break;
            case "surname":
                value = surname_field.getText();
                if (value.isEmpty() || !validateField(field, value)) {
                    surname_field.setStyle(ERROR_STYLE);
                    isValid = false;
                    errorMessage = getValidationMessage(field, value);
                }
                break;
        }

        if (!isValid) {
            Dialog.showAlert("Ошибка валидации", "Некорректное значение поля '" + getFieldName(field) + "':\n" + errorMessage);
            return;
        }

        Consumer consumer = new Consumer();
        consumer.setId(Connect.id);

        switch (field) {
            case "email": consumer.setEmail(value); break;
            case "login": consumer.setLogin(value); break;
            case "name": consumer.setName(value); break;
            case "password": consumer.setPassword(value); break;
            case "surname": consumer.setSurname(value); break;
        }

        System.out.println("Обновляем информацию для клиента с ID: " + consumer.getId());
        Connect.client.sendMessage("updateConsumer");
        Connect.client.sendObject(consumer);

        String mes = "";
        try {
            mes = Connect.client.readMessage();
        } catch (IOException ex) {
            System.out.println("Error in reading");
        }

        if (mes.equals("Incorrect Data")) {
            Dialog.showAlertWithExistLogin();
        } else {
            Dialog.correctOperation();
        }
    }

    private String getFieldName(String fieldType) {
        switch (fieldType) {
            case "name": return "Имя";
            case "surname": return "Фамилия";
            case "email": return "Email";
            case "login": return "Логин";
            case "password": return "Пароль";
            default: return fieldType;
        }
    }

    private void resetAllFieldStyles() {
        name_field.setStyle(DEFAULT_STYLE);
        surname_field.setStyle(DEFAULT_STYLE);
        email_field.setStyle(DEFAULT_STYLE);
        login_field.setStyle(DEFAULT_STYLE);
        password_field.setStyle(DEFAULT_STYLE);
    }

    @FXML
    void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuClient.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) BackUpButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}