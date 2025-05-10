package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientWork.Connect;
import Salon.Order;
import Salon.StaffTimetable;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.WindowChanger;

public class WorkScheduleController {

    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private Button BackUpButton;
    @FXML private TableColumn<StaffTimetable, String> DateColumn;
    @FXML private TableColumn<StaffTimetable, Integer> ID_field;
    @FXML private TableColumn<StaffTimetable, Integer> MinuteColumn;
    @FXML private TableColumn<StaffTimetable, String> NameColumn;
    @FXML private TableColumn<StaffTimetable, String> lastNameColumn;
    @FXML private TableColumn<StaffTimetable, String> ServiceColumn;
    @FXML private TableColumn<StaffTimetable, String> TimeColumn;
    @FXML private TableView<StaffTimetable> employeeTable;
    @FXML private TextField findEmployee;
    @FXML private DatePicker StartData;
    @FXML private DatePicker EndData;
    @FXML private Button clearFilterButton;
    @FXML private Button dateFilterButton;

    private ObservableList<StaffTimetable> scheduleList = FXCollections.observableArrayList();
    private FilteredList<StaffTimetable> filteredData;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void backToMenu(ActionEvent actionEvent) throws IOException {
        String fxmlFile = Connect.role.equals("Admin") ? "/menuAdmin.fxml" : "/menuEmployee.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) BackUpButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    @FXML
    void initialize() {
        setupTableColumns();

        // Устанавливаем подсказку в зависимости от роли
        if(Connect.role.equals("Admin")) {
            findEmployee.setPromptText("Введите ФИО сотрудника");
            loadOrderData();
        } else {
            findEmployee.setPromptText("Введите название услуги");
            loadOrderDataForEmployee();
        }


        findEmployee.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        setupDateFilter();
    }

    private void setupTableColumns() {
        ID_field.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getId()));
        lastNameColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getLastnameEmployee()));
        NameColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getNameEmployee()));
        ServiceColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getNameService()));
        TimeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getTime()));
        DateColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getDate()));
        MinuteColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getLengthOfTime()));
    }

    private void setupDateFilter() {
        filteredData = new FilteredList<>(scheduleList, p -> true);

        SortedList<StaffTimetable> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(employeeTable.comparatorProperty());
        employeeTable.setItems(sortedData);

        dateFilterButton.setOnAction(event -> applyDateFilter());
        clearFilterButton.setOnAction(event -> clearFilters());
    }
   private void applyDateFilter() {
       LocalDate startDate = StartData.getValue();
       LocalDate endDate = EndData.getValue();

       if (startDate == null || endDate == null) {
           showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите обе даты для фильтрации");
           return;
       }

       if (startDate.isAfter(endDate)) {
           showAlert(Alert.AlertType.WARNING, "Предупреждение", "Начальная дата не может быть позже конечной");
           return;
       }

       filteredData.setPredicate(item -> {
           try {
               LocalDate itemDate = LocalDate.parse(item.getDate(), dateFormatter);

               return !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate);
           } catch (Exception e) {
               return false; // Если произошла ошибка, не показывать этот элемент
           }
       });
   }

private void clearFilters() {
    StartData.setValue(null);
    EndData.setValue(null);
    findEmployee.clear();
    filteredData.setPredicate(item -> true); // Показываем все записи
    filterTable(); // Обновляем таблицу
}

    private void filterTable() {
        String searchTerm = findEmployee.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            filteredData.setPredicate(item -> true);
            return;
        }

        filteredData.setPredicate(item -> {
            if (item == null) return false;

            if (Connect.role.equals("Admin")) {
                // Для администратора ищем по ФИО
                return (item.getNameEmployee() != null && item.getNameEmployee().toLowerCase().contains(searchTerm)) ||
                        (item.getLastnameEmployee() != null && item.getLastnameEmployee().toLowerCase().contains(searchTerm));
            } else {
                // Для сотрудника ищем по названию услуги
                return (item.getNameService() != null && item.getNameService().toLowerCase().contains(searchTerm));
            }
        });
    }
    private void loadOrderData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                Connect.client.sendMessage("GET_SCHEDULE");
                Object receivedObject = Connect.client.readObject();

                Platform.runLater(() -> {
                    if (receivedObject instanceof List<?>) {
                        List<StaffTimetable> loadedEmployee = (List<StaffTimetable>) receivedObject;
                        scheduleList.setAll(loadedEmployee);

                        if (loadedEmployee.isEmpty()) {
                            showAlert(Alert.AlertType.WARNING, "Нет данных", "Нет данных для отображения.");
                        }
                    } else {
                        showErrorAlert("Ошибка: полученный объект не является списком.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showErrorAlert("Ошибка при получении данных: " + e.getMessage()));
            }
        });
        executor.shutdown();
    }

    private void loadOrderDataForEmployee() {

        try {
            Connect.client.sendMessage("GET_SCHEDULE_BY_EMPLOYEE");
            Connect.client.sendObject(Connect.id);

            Object receivedObject = Connect.client.readObject();

            if (receivedObject instanceof String && receivedObject.equals("NO_DATA")) {
                showAlert(Alert.AlertType.WARNING, "Нет данных", "Нет доступных заявок для отображения.");
            } else if (receivedObject instanceof List<?>) {
                List<StaffTimetable> loadedEmployee = (List<StaffTimetable>) receivedObject;
                scheduleList.setAll(loadedEmployee);
                employeeTable.setItems(scheduleList);
            } else {
                throw new ClassCastException("Полученный объект не является списком.");
            }
        } catch (Exception e) {
            showErrorAlert("Ошибка при получении данных: " + e.getMessage());
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
