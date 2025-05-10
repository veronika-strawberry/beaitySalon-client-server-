package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import ClientWork.Connect;
import Salon.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.Dialog;

public class AccountEmployeeController {

        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private Button BackUpButton;

        @FXML
        private Button editButton;

        @FXML
        private TextField fathername_field;

        @FXML
        private TextField lastName_field;

        @FXML
        private TextField login_field;

        @FXML
        private TextField name_field;

        @FXML
        private PasswordField password_field;

        @FXML
        private TextField phonenumber_field;

        @FXML
        private Text position;

        @FXML
        private Text salary;

        // Стили для подсветки
        private static final String ERROR_STYLE = "-fx-border-color: #FF0000; -fx-border-width: 2px;";
        private static final String DEFAULT_STYLE = "";

        @FXML
        void goBack(ActionEvent event) throws IOException {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuEmployee.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) BackUpButton.getScene().getWindow();
                stage.setScene(new Scene(root));
        }

        @FXML
        void initialize() {
                setupTooltips();
                setupFieldListeners();
                loadEmployeeData();
                editButton.setOnAction(event -> EmployeeUpdateEmployee());
        }

        private void setupTooltips() {
                lastName_field.setTooltip(new Tooltip("Фамилия (только русские буквы)"));
                name_field.setTooltip(new Tooltip("Имя (только русские буквы)"));
                fathername_field.setTooltip(new Tooltip("Отчество (только русские буквы)"));
                phonenumber_field.setTooltip(new Tooltip("Номер телефона (10-15 цифр)"));
                login_field.setTooltip(new Tooltip("Логин (4-20 символов: буквы, цифры, _)"));
                password_field.setTooltip(new Tooltip("Пароль (минимум 6 символов)"));
        }

        private void setupFieldListeners() {
                lastName_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(lastName_field));
                name_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(name_field));
                fathername_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(fathername_field));
                phonenumber_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(phonenumber_field));
                login_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(login_field));
                password_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(password_field));
        }

        private void resetFieldStyle(TextField field) {
                field.setStyle(DEFAULT_STYLE);
        }

        private void resetFieldStyle(PasswordField field) {
                field.setStyle(DEFAULT_STYLE);
        }

        private boolean validateFields() {
                boolean isValid = true;

                // Проверка фамилии (если не пустое)
                if (!lastName_field.getText().isEmpty() &&
                        !Pattern.matches("^[А-ЯЁ][а-яё]+$", lastName_field.getText())) {
                        lastName_field.setStyle(ERROR_STYLE);
                        isValid = false;
                }

                // Проверка имени (если не пустое)
                if (!name_field.getText().isEmpty() &&
                        !Pattern.matches("^[А-ЯЁ][а-яё]+$", name_field.getText())) {
                        name_field.setStyle(ERROR_STYLE);
                        isValid = false;
                }

                // Проверка отчества (если не пустое)
                if (!fathername_field.getText().isEmpty() &&
                        !Pattern.matches("^[А-ЯЁ][а-яё]+$", fathername_field.getText())) {
                        fathername_field.setStyle(ERROR_STYLE);
                        isValid = false;
                }

                // Проверка телефона (если не пустое)
                if (!phonenumber_field.getText().isEmpty() &&
                        !Pattern.matches("^(\\+[0-9]{10,15}|[0-9]{10,15})$", phonenumber_field.getText())) {
                        phonenumber_field.setStyle(ERROR_STYLE);
                        isValid = false;
                }

                // Проверка логина (если не пустое)
                if (!login_field.getText().isEmpty() &&
                        !Pattern.matches("^[a-zA-Z0-9_]{4,20}$", login_field.getText())) {
                        login_field.setStyle(ERROR_STYLE);
                        isValid = false;
                }

                // Проверка пароля (если не пустое)
                if (!password_field.getText().isEmpty() &&
                        password_field.getText().length() < 6) {
                        password_field.setStyle(ERROR_STYLE);
                        isValid = false;
                }

                return isValid;
        }

        private void loadEmployeeData() {
                Connect.client.sendMessage("getEmployeeById");
                System.out.println("работает loadEmployeeData");

                Employee e = new Employee();
                e.setId(Connect.id);
                Connect.client.sendObject(e);
                Employee employee = (Employee) Connect.client.readObject();

                if (employee != null) {
                        position.setText(employee.getPosition());
                        salary.setText(String.valueOf(employee.getSalary()));
                        lastName_field.setText(employee.getSurname());
                        name_field.setText(employee.getName());
                        fathername_field.setText(employee.getPatronymic());
                        phonenumber_field.setText(employee.getPhone());
                        login_field.setText(employee.getLogin());
                        System.out.println("Получен сотрудник "+lastName_field +" c "+salary);
                }
        }

        private void EmployeeUpdateEmployee() {
                // Сбрасываем подсветку перед проверкой
                resetAllFieldStyles();

                // Проверяем введенные данные
                if (!validateFields()) {
                        Dialog.showAlert("Ошибка", "Некоторые поля заполнены некорректно.\nПожалуйста, проверьте выделенные поля.");
                        return;
                }

                Employee employee = new Employee();
                employee.setId(Connect.id);

                String surname = lastName_field.getText();
                if (!surname.isEmpty()) {
                        employee.setSurname(surname);
                }

                String name = name_field.getText();
                if (!name.isEmpty()) {
                        employee.setName(name);
                }

                String patronymic = fathername_field.getText();
                if (!patronymic.isEmpty()) {
                        employee.setPatronymic(patronymic);
                }

                String phone = phonenumber_field.getText();
                if (!phone.isEmpty()) {
                        employee.setPhone(phone);
                }

                String login = login_field.getText();
                if (!login.isEmpty()) {
                        employee.setLogin(login);
                }

                String password = password_field.getText();
                if (!password.isEmpty()) {
                        employee.setPassword(password);
                }

                employee.setPosition(null);
                employee.setSalary(0);

                System.out.println("Обновляем сотрудника с ID: " + employee.getId());
                Connect.client.sendMessage("employeeUpdateEmployee");
                Connect.client.sendObject(employee);
                System.out.println("Запись отправлена");

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

        private void resetAllFieldStyles() {
                lastName_field.setStyle(DEFAULT_STYLE);
                name_field.setStyle(DEFAULT_STYLE);
                fathername_field.setStyle(DEFAULT_STYLE);
                phonenumber_field.setStyle(DEFAULT_STYLE);
                login_field.setStyle(DEFAULT_STYLE);
                password_field.setStyle(DEFAULT_STYLE);
        }
}