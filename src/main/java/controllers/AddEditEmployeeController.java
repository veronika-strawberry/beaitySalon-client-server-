
package controllers;


import ClientWork.Connect;
import Salon.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.Dialog;
import util.WindowChanger;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class AddEditEmployeeController {

    @FXML private TextField ID_edit;
    @FXML private TextField name_field;
    @FXML private TextField lastName_field;
    @FXML private TextField fathername_field;
    @FXML private TextField phonenumber_field;
    @FXML private TextField position_field;
    @FXML private TextField salary_field;
    @FXML private TextField login_field;
    @FXML private PasswordField password_field;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button BackUpButton;


    @FXML
    void initialize() {
      phonenumber_field.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^\\+?[0-9\\- ]*$")) {  // Разрешаем +, цифры, дефисы, пробелы
                return change;
            }
            return null;  // Блокируем недопустимые символы
        }));

        addButton.setOnAction(event -> handleAddEmployee());
        editButton.setOnAction(event -> handleUpdateEmployee());

    }

    private void handleUpdateEmployee() {
        if (checkInput_update())
            Dialog.showAlertWithNullInput();
        else {
            Employee employee = new Employee();
            employee.setId(Integer.parseInt(ID_edit.getText()));
            employee.setName(name_field.getText());
            employee.setSurname(lastName_field.getText());
            employee.setPatronymic(fathername_field.getText());
            employee.setPhone(phonenumber_field.getText());
            employee.setPosition(position_field.getText());
            employee.setSalary(Double.parseDouble(salary_field.getText()));
            employee.setLogin(login_field.getText());
            employee.setPassword(password_field.getText());
            System.out.println("Обновляем сотрудника с ID: " + employee.getId());
            Connect.client.sendMessage("updateEmployee");
            Connect.client.sendObject(employee);
            System.out.println("Запись отправлена");

            String mes = "";
            try {
                mes = Connect.client.readMessage();
            } catch(IOException ex){
                System.out.println("Error in reading");
            }
            if (mes.equals("Incorrect Data"))
                Dialog.showAlertWithExistLogin();
            else {
                Dialog.correctOperation();
            }
        }
    }

    private boolean checkInput_update() {
        try {
            String phone = phonenumber_field.getText();
            return ID_edit.getText().isEmpty() ||  // Проверка ID
                    name_field.getText().isEmpty() ||
                    lastName_field.getText().isEmpty() ||
                    fathername_field.getText().isEmpty() ||
                    position_field.getText().isEmpty() ||
                    phone.isEmpty() ||
                    !phone.matches("^\\+?[0-9\\- ]+$") ||  // Проверка формата
                    phone.replaceAll("[^0-9]", "").length() < 9 ||  // Минимум 7 цифр
                    salary_field.getText().isEmpty() ||
                    login_field.getText().isEmpty() ||
                    password_field.getText().isEmpty();
        } catch (Exception e) {
            System.out.println("Error in validation: " + e.getMessage());
            return true;
        }
    }
    private void handleAddEmployee() {
        if (checkInput_add())
            Dialog.showAlertWithNullInput();
        else {
            Employee employee = new Employee();
            employee.setName(name_field.getText());
            employee.setSurname(lastName_field.getText());
            employee.setPatronymic(fathername_field.getText());
            employee.setPhone(phonenumber_field.getText());
            employee.setPosition(position_field.getText());
            employee.setSalary(Double.parseDouble(salary_field.getText()));
            employee.setLogin(login_field.getText());
            employee.setPassword(password_field.getText());
            Connect.client.sendMessage("registrationEmployee");
            Connect.client.sendObject(employee);
            System.out.println("Запись отправлена");

            String mes = "";
            try {
                mes = Connect.client.readMessage();
            } catch(IOException ex){
                System.out.println("Error in reading");
            }
            if (mes.equals("Incorrect Data")){
                Dialog.showAlertWithExistLogin();
            goBack();}
            else {
                Dialog.correctOperation();
                goBack();
            }
        }
    }

    private boolean checkInput_add() {
        try {
            String phone = phonenumber_field.getText();
            return name_field.getText().isEmpty() ||
                    lastName_field.getText().isEmpty() ||
                    fathername_field.getText().isEmpty() ||
                    position_field.getText().isEmpty() ||
                    phone.isEmpty() ||
                    !phone.matches("^\\+?[0-9\\- ]+$") ||  // Проверка формата
                    phone.replaceAll("[^0-9]", "").length() < 9 ||  // Минимум 7 цифр
                    salary_field.getText().isEmpty() ||
                    login_field.getText().isEmpty() ||
                    password_field.getText().isEmpty();
        } catch (Exception e) {
            System.out.println("Error in validation: " + e.getMessage());
            return true;
        }
    }

    @FXML
     void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/workWithEmployeeDB.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) BackUpButton.getScene().getWindow();

            /*Connect.client.sendMessage("GET_ALL_EMPLOYEE");*/

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось вернуться: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
