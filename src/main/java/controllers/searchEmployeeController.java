/*
package controllers;


import java.io.IOException;
import java.net.URL;
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
import javafx.stage.Stage;

public class searchEmployeeController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;


    @FXML
    private Button backUpButton;

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
    private TableColumn<Employee, String> phoneColumn;

    @FXML
    private TableColumn<Employee, String> positionColumn;

    @FXML
    private ChoiceBox<String> position_choiceBox;

    @FXML
    private TextField search_field;
    ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private ObservableList<String> positionList = FXCollections.observableArrayList();
    @FXML
    private void initialize(){
        System.out.println("Controller инициализирован.");
        ID_field.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject().asString());

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

        loadEmployeeData();

        search_field.textProperty().addListener((observable, oldValue, newValue) -> filterTable());


    }
    private void loadEmployeeData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                System.out.println("loadEmployeeData инициализирован.");
                // Запрос на сервер для получения данных о сотрудниках
                Connect.client.sendMessage("GET_ALL_EMPLOYEE"); // Команда для получения сотрудников
                Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

                // Обработка в основном потоке
                Platform.runLater(() -> {
                    if (receivedObject instanceof List<?>) {
                        List<Employee> loadedEmployees = (List<Employee>) receivedObject;
                        employeeList.clear();
                        employeeList.addAll(loadedEmployees);
                        employeeTable.setItems(employeeList);
                        System.out.println("Данные успешно загружены: " + employeeList.size() + " сотрудников.");

// Загрузка должностей из списка сотрудников
                        loadPositionsFromEmployees(loadedEmployees);
                    } else {
                        throw new ClassCastException("Полученный объект не является списком сотрудников.");
                    }
                });
            } catch (Exception e) {
                // Показать сообщение об ошибке в основном потоке
                Platform.runLater(() -> showErrorAlert("Ошибка при получении данных: " + e.getMessage()));
            }
        });
        executor.shutdown();
    }
    private void loadPositionsFromEmployees(List<Employee> employees) {
        positionList.clear(); // Очистка текущего списка должностей
        for (Employee employee : employees) {
            String position = employee.getPosition(); // Предполагается, что есть метод getPosition()
            if (position != null && !positionList.contains(position)) {
                positionList.add(position); // Добавляем должность, если её еще нет в списке
            }
        }
        position_choiceBox.setItems(positionList); // Установка данных в ChoiceBox
        System.out.println("Должности успешно загружены: " + positionList.size() + " должностей.");

        // Добавление слушателя для изменения выбора
        position_choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filterTable());
    }

    private void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Ошибка", message);
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void filterTable() {
        String searchTerm = search_field.getText().toLowerCase(); // Получаем текст для поиска
        String selectedPosition = position_choiceBox.getValue(); // Получаем выбранную должность

        // Создаем список для отфильтрованных данных
        ObservableList<Employee> filteredList = FXCollections.observableArrayList();

        // Фильтрация по логину, имени, фамилии, отчеству и должности
        for (Employee empl : employeeList) {
            boolean matchesLogin = empl.getLogin() != null && empl.getLogin().toLowerCase().contains(searchTerm);
            boolean matchesName = empl.getName() != null && empl.getName().toLowerCase().contains(searchTerm);
            boolean matchesSurname = empl.getSurname() != null && empl.getSurname().toLowerCase().contains(searchTerm);
            boolean matchesPatronymic = empl.getPatronymic() != null && empl.getPatronymic().toLowerCase().contains(searchTerm);
            boolean matchesPosition = selectedPosition == null || empl.getPosition().equals(selectedPosition);

            // Если хотя бы одно из условий соответствуют, добавляем сотрудника в список
            if ((matchesLogin || matchesName || matchesSurname || matchesPatronymic) && matchesPosition) {
                filteredList.add(empl);
            }
        }

        // Устанавливаем отфильтрованные данные в таблицу
        employeeTable.setItems(filteredList);
    }
    @FXML
    void goBack(ActionEvent event) throws IOException {
        System.out.println("Получена роль: " + Connect.role );
        String fxmlFile = Connect.role.equals("Consumer") ? "/menuClient.fxml" : "/menuEmployee.fxml"; // Check user role
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) backUpButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }


}

*/

package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientWork.Connect;
import Salon.Employee;
import javafx.application.Platform;
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
import javafx.stage.Stage;

public class searchEmployeeController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backUpButton;

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
    private TableColumn<Employee, String> phoneColumn;

    @FXML
    private TableColumn<Employee, String> positionColumn;

    @FXML
    private ChoiceBox<String> position_choiceBox;

    @FXML
    private TextField search_field;

    ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private ObservableList<String> positionList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        System.out.println("Controller инициализирован.");
        ID_field.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject().asString());

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

        loadEmployeeData();

        search_field.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
    }

    private void loadEmployeeData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                System.out.println("loadEmployeeData инициализирован.");
                // Запрос на сервер для получения данных о сотрудниках
                Connect.client.sendMessage("GET_ALL_EMPLOYEE"); // Команда для получения сотрудников
                Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

                // Обработка в основном потоке
                Platform.runLater(() -> {
                    if (receivedObject instanceof List<?>) {
                        List<Employee> loadedEmployees = (List<Employee>) receivedObject;
                        employeeList.clear();
                        employeeList.addAll(loadedEmployees);
                        employeeTable.setItems(employeeList);
                        System.out.println("Данные успешно загружены: " + employeeList.size() + " сотрудников.");

                        // Загрузка должностей из списка сотрудников
                        loadPositionsFromEmployees(loadedEmployees);
                    } else {
                        throw new ClassCastException("Полученный объект не является списком сотрудников.");
                    }
                });
            } catch (Exception e) {
                // Показать сообщение об ошибке в основном потоке
                Platform.runLater(() -> showErrorAlert("Ошибка при получении данных: " + e.getMessage()));
            }
        });
        executor.shutdown();
    }

    private void loadPositionsFromEmployees(List<Employee> employees) {
        positionList.clear(); // Очистка текущего списка должностей

        // Добавляем вариант "Все" в список должностей
        positionList.add("Все");

        for (Employee employee : employees) {
            String position = employee.getPosition(); // Предполагается, что есть метод getPosition()
            if (position != null && !positionList.contains(position)) {
                positionList.add(position); // Добавляем должность, если её еще нет в списке
            }
        }
        position_choiceBox.setItems(positionList); // Установка данных в ChoiceBox

        // Устанавливаем "Все" как выбранный элемент по умолчанию
        position_choiceBox.setValue("Все");

        System.out.println("Должности успешно загружены: " + positionList.size() + " должностей.");

        // Добавление слушателя для изменения выбора
        position_choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filterTable());
    }

    private void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Ошибка", message);
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void filterTable() {
        String searchTerm = search_field.getText().toLowerCase(); // Получаем текст для поиска
        String selectedPosition = position_choiceBox.getValue(); // Получаем выбранную должность

        // Создаем список для отфильтрованных данных
        ObservableList<Employee> filteredList = FXCollections.observableArrayList();

        // Фильтрация по логину, имени, фамилии, отчеству и должности
        for (Employee empl : employeeList) {
            boolean matchesSearch = (empl.getLogin() != null && empl.getLogin().toLowerCase().contains(searchTerm)) ||
                    (empl.getName() != null && empl.getName().toLowerCase().contains(searchTerm)) ||
                    (empl.getSurname() != null && empl.getSurname().toLowerCase().contains(searchTerm)) ||
                    (empl.getPatronymic() != null && empl.getPatronymic().toLowerCase().contains(searchTerm));

            boolean matchesPosition = selectedPosition == null || selectedPosition.equals("Все") || empl.getPosition().equals(selectedPosition);

            // Если хотя бы одно из условий соответствуют, добавляем сотрудника в список
            if (matchesSearch && matchesPosition) {
                filteredList.add(empl);
            }
        }

        // Устанавливаем отфильтрованные данные в таблицу
        employeeTable.setItems(filteredList);
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        System.out.println("Получена роль: " + Connect.role);
        String fxmlFile = Connect.role.equals("Consumer") ? "/menuClient.fxml" : "/menuEmployee.fxml"; // Check user role
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) backUpButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
