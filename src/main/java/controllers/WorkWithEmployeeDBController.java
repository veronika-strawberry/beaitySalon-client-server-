package controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientWork.Connect;

import Salon.Employee;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.*;
import util.Dialog;


public class WorkWithEmployeeDBController {


    @FXML
    private Button backUpButton; // Changed to match Java naming conventions

    @FXML
    private Button addEditButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, String> ID_field;

    @FXML
    private TableColumn<Employee, String> fatherNameColumn;

    @FXML
    private TableColumn<Employee, String> firstNameColumn;


    @FXML
    private TableColumn<Employee, String> lastNameColumn;

    @FXML
    private TableColumn<Employee, String> loginColumn;

    @FXML
    private TableColumn<Employee, String> phoneColumn;

    @FXML
    private TableColumn<Employee, String> positionColumn;

    @FXML
    private TableColumn<Employee, Double> salaryColumn;

    @FXML
    private TextField search_field;
    ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        System.out.println("Controller инициализирован.");
        ID_field.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject().asString());

        loginColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLogin()));

        firstNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        lastNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSurname()));

        fatherNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPatronymic()));

        phoneColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPhone()));

        positionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPosition()));

        salaryColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getSalary()).asObject());
        loadEmployeeData();
        search_field.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        backUpButton.setOnAction(event -> goBack());
        deleteButton.setOnAction(event -> deleteEmployee());
        addEditButton.setOnAction(event -> addEditEmployee());
    }

    private void filterTable() {//улучшить!
        String searchTerm = search_field.getText().toLowerCase(); // Получаем текст для поиска

        // Создаем список для отфильтрованных данных
        ObservableList<Employee> filteredList = FXCollections.observableArrayList();

        // Фильтрация по логину, имени, фамилии и отчеству
        for (Employee empl : employeeList) {
            boolean matchesLogin = empl.getLogin() != null && empl.getLogin().toLowerCase().contains(searchTerm);
            boolean matchesName = empl.getName() != null && empl.getName().toLowerCase().contains(searchTerm);
            boolean matchesSurname = empl.getSurname() != null && empl.getSurname().toLowerCase().contains(searchTerm);
            boolean matchesPatronymic = empl.getPatronymic() != null && empl.getPatronymic().toLowerCase().contains(searchTerm);

            // Если хотя бы одно из условий соответствуют, добавляем сотрудника в список
            if (matchesLogin || matchesName || matchesSurname || matchesPatronymic) {
                filteredList.add(empl);
            }
        }

        // Устанавливаем отфильтрованные данные в таблицу
        employeeTable.setItems(filteredList);
    }
private void loadEmployeeData() {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(() -> {
        try {
            System.out.println("loadEmployeeData инициализирован.");
            Connect.client.sendMessage("GET_ALL_EMPLOYEE");
            Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

            // Обработка в основном потоке
            Platform.runLater(() -> {
                if (receivedObject instanceof List<?>) {
                    List<Employee> loadedEmployee = (List<Employee>) receivedObject;
                    employeeList.clear();
                    employeeList.addAll(loadedEmployee);
                    employeeTable.setItems(employeeList);
                    System.out.println("Данные успешно загружены: " + employeeList.size() + " сотрудников.");
                } else {
                    throw new ClassCastException("Полученный объект не является списком.");
                }
            });
        } catch (Exception e) {
            // Показать сообщение об ошибке в основном потоке
            Platform.runLater(() -> showErrorAlert("Ошибка при получении данных: " + e.getMessage()));
        }
    });
    executor.shutdown();
}
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/workWithDB.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backUpButton.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Ошибка при загрузке окна работы с БД");
        }
    }

    private void addEditEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addEditEmployee.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) addEditButton.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Ошибка при загрузке окна работы с БД");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Ошибка", message);
    }


    @FXML
    void deleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem(); // Получаем выделенного сотрудника

        if (selectedEmployee != null) { // Проверяем, выбран ли сотрудник
            Connect.client.sendMessage("DELETE_EMPLOYEE"); // Отправляем команду на удаление
            Connect.client.sendObject(selectedEmployee); // Отправляем объект выбранного сотрудника
            System.out.println("Запись отправлена");

            try {
                String response = Connect.client.readMessage(); // Получаем ответ от сервера
                System.out.println(response);
                if (response.equals("Ошибка при удалении сотрудника")) {
                    Dialog.showAlertWithData(); // Показываем сообщение об ошибке

                } else {
                    Dialog.correctOperation(); // Сообщение об успешном удалении
                    loadEmployeeData(); // Обновляем таблицу сотрудников
                }
            } catch (IOException ex) {
                System.out.println("Ошибка при чтении ответа от сервера: " + ex.getMessage());
                //Dialog.showErrorAlert("Ошибка при удалении сотрудника.");
            }
        } else {
            Dialog.showAlert("Ошибка", "Пожалуйста, выберите сотрудника из таблицы."); // Всплывающее окно для уведомления
        }
    }




}

