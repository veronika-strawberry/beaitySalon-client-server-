package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientWork.Connect;
import Salon.BeautyService;
import Salon.Employee;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.Dialog;
import util.WindowChanger;

import static util.Dialog.showAlert;


public class AddEditServiceController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private TextField ID_edit;

    @FXML
    private Button addButton;
    @FXML
    private TextField lengthOfTime_field1;
    @FXML
    private DatePicker date_picker;

    @FXML
    private Button editButton;

    @FXML
    private ChoiceBox<String> employee_choicebox;

    @FXML
    private ComboBox<String> time_comboBox;

    @FXML
    private TextField name_field;

    @FXML
    private TextField price_field;

    @FXML
    private TextField type_field;

    ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private ObservableList<BeautyService> deletedServices = FXCollections.observableArrayList();

    private static final String ERROR_STYLE = "-fx-border-color: #FF0000; -fx-border-width: 2px;";
    private static final String DEFAULT_STYLE = "";


    @FXML
    void initialize() {


        addButton.setOnAction(event -> handleAddService());
        editButton.setOnAction(event -> handleUpdateService());
        loadEmployeeData();
        populateEmployeeChoiceBox();
        populateTimeComboBox();

        //d
        setupTooltips();

        employee_choicebox.setOnAction(event -> {
            String selectedEmployee = employee_choicebox.getSelectionModel().getSelectedItem();
            System.out.println("Выбран сотрудник: " + selectedEmployee);
        });

        //d
        setupFieldListeners();

    }
//d
private void setupTooltips() {
    name_field.setTooltip(new Tooltip("Название услуги (не может быть пустым)"));
    type_field.setTooltip(new Tooltip("Тип услуги (не может быть пустым)"));
    price_field.setTooltip(new Tooltip("Цена услуги (только числа, разделитель - точка)"));
    lengthOfTime_field1.setTooltip(new Tooltip("Длительность в минутах (целое число)"));
    date_picker.setTooltip(new Tooltip("Выберите дату оказания услуги"));
    time_comboBox.setTooltip(new Tooltip("Выберите время оказания услуги"));
    employee_choicebox.setTooltip(new Tooltip("Выберите сотрудника"));
}

    private void setupFieldListeners() {
        name_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(name_field));
        type_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(type_field));
        price_field.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(price_field));
        lengthOfTime_field1.textProperty().addListener((obs, oldVal, newVal) -> resetFieldStyle(lengthOfTime_field1));
    }

    private void resetFieldStyle(TextField field) {
        field.setStyle(DEFAULT_STYLE);
    }

    private void resetAllFieldStyles() {
        name_field.setStyle(DEFAULT_STYLE);
        type_field.setStyle(DEFAULT_STYLE);
        price_field.setStyle(DEFAULT_STYLE);
        lengthOfTime_field1.setStyle(DEFAULT_STYLE);
    }

    private void highlightErrorFields(boolean isNameEmpty, boolean isTypeEmpty,
                                      boolean isPriceInvalid, boolean isLengthInvalid) {
        if (isNameEmpty) name_field.setStyle(ERROR_STYLE);
        if (isTypeEmpty) type_field.setStyle(ERROR_STYLE);
        if (isPriceInvalid) price_field.setStyle(ERROR_STYLE);
        if (isLengthInvalid) lengthOfTime_field1.setStyle(ERROR_STYLE);
    }
////d
    private void printDeletedServices() {
        getDeletedServices();
        System.out.println("Удаленные услуги:");
        for (BeautyService service : deletedServices) {
            System.out.println("ID: " + service.getId() +
                    ", Название: " + service.getName() +
                    ", Тип: " + service.getType() +
                    ", Цена: " + service.getPrice() +
                    ", Время: " + service.getTime());
        }
    }
    //---
    // Метод для получения списка удаленных услуг
    public ObservableList<BeautyService> getDeletedServices() {
        return deletedServices;
    }
    public void setDeletedServices(ObservableList<BeautyService> deletedServices) {
        this.deletedServices = deletedServices;
    }
    //---
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
                        System.out.println("Данные успешно загружены: " + employeeList.size() + " сотрудников.");

                        // Заполнить выбор сотрудников (вызываем здесь)
                        populateEmployeeChoiceBox();
                    } else {
                        throw new ClassCastException("Полученный объект не является списком.");
                    }
                });
            } catch (Exception e) {
                // Сообщение об ошибке
                // Platform.runLater(() -> showErrorAlert("Ошибка при получении данных: " + e.getMessage()));
            }
        });
        executor.shutdown();
    }
    private void populateEmployeeChoiceBox() {
        // Очистить существующие элементы
        employee_choicebox.getItems().clear();


        // Добавьте ФИО всех сотрудников в ChoiceBox
        for (Employee employee : employeeList) {
            String fullName =employee.getId()+ " " +employee.getSurname() + " " + employee.getName() + " " + employee.getPatronymic();
            employee_choicebox.getItems().add(fullName);
        }
    }
    private int getSelectedEmployeeId() {
        String selectedEmployee = employee_choicebox.getValue();
        if (selectedEmployee != null) {
            String[] parts = selectedEmployee.split(" ");
            return Integer.parseInt(parts[0]); // ID сотрудника - это первый элемент
        }
        return -1; // Или обрабатывайте ошибку так, как нужно
    }
    private void populateTimeComboBox() {
        // Пример добавления временных интервалов
        time_comboBox.getItems().addAll(
                "08:00", "08:30", "09:00", "09:30", "10:00",
                "10:30", "11:00", "11:30", "12:00", "12:30",
                "13:00", "13:30", "14:00", "14:30", "15:00",
                "15:30", "16:00", "16:30", "17:00", "17:30",
                "18:00", "18:30", "19:00", "19:30", "20:00",
                "20:30", "21:00", "21:30"
        );
    }
   private void handleUpdateService() {
       if (checkInput_update()) {
           Dialog.showAlertWithNullInput();
           return;
       }

       // Получаем и проверяем входные данные
       int employeeId = getSelectedEmployeeId();
       if (employeeId == -1) {
           Dialog.showAlertWithNullInput();
           return;
       }

       BeautyService service = new BeautyService();
       try {
           // Инициализация с проверками
           service.setId(Integer.parseInt(ID_edit.getText()));
           service.setName(name_field.getText());
           service.setType(type_field.getText());
           service.setPrice(Double.parseDouble(price_field.getText()));
           service.setLengthOfTime(Integer.parseInt(lengthOfTime_field1.getText()));
           // Объединяем дату и время в одну строку
           String selectedDate = date_picker.getValue().toString();
           String selectedTime = time_comboBox.getValue();
           if (selectedTime == null) {
               Dialog.showAlertWithNullInput();
               return;
           }
           String timeWithSeconds = selectedDate + " " + selectedTime + ":00";
           service.setTime(timeWithSeconds); // Устанавливаем время
           service.setEmployeeName(Integer.toString(employeeId)); // Используем ID как строку
       } catch (NumberFormatException e) {
           Dialog.showAlertWithData(); // Пример вызова диалога об ошибке
           return;
       }

       System.out.println("Реадактируем услугу "+ID_edit.getText()+" "+service);
       // Отправка информации на сервер
       Connect.client.sendMessage("updateService");
       Connect.client.sendObject(service);

       // Обработка ответа от сервера
       try {
           String mes = Connect.client.readMessage();
           if (mes.equals("Incorrect Data")) {
               Dialog.showAlertWithUpdateService();
           } else {
               Dialog.correctOperation();
           }
       } catch (IOException ex) {
           System.out.println("Error in reading");
       }
   }
    private boolean checkInput_update() {
        try {
            return ID_edit.getText().isEmpty() ||
                    name_field.getText().isEmpty() ||
                    type_field.getText().isEmpty() ||
                    price_field.getText().isEmpty() ||
                    date_picker.getValue() == null ||
                    employee_choicebox.getValue() == null ||
                    time_comboBox.getValue() == null || // Проверка на пустое время
                    time_comboBox.getValue().isEmpty(); // Проверка на пустую строку
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return true;
        }
    }
//deep

    //d
    private boolean validatePrice(String price) {
        try {
            double value = Double.parseDouble(price);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateLength(String length) {
        try {
            int value = Integer.parseInt(length);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
private void handleAddService() {
        //d
    resetAllFieldStyles();
    boolean isNameEmpty = name_field.getText().trim().isEmpty();
    boolean isTypeEmpty = type_field.getText().trim().isEmpty();
    boolean isPriceValid = validatePrice(price_field.getText().trim());
    boolean isLengthValid = validateLength(lengthOfTime_field1.getText().trim());
    boolean isDateValid = date_picker.getValue() != null;
    boolean isTimeValid = time_comboBox.getValue() != null;
    boolean isEmployeeSelected = employee_choicebox.getValue() != null;

    if (isNameEmpty || isTypeEmpty || !isPriceValid || !isLengthValid ||
            !isDateValid || !isTimeValid || !isEmployeeSelected) {

        highlightErrorFields(isNameEmpty, isTypeEmpty, !isPriceValid, !isLengthValid);

        StringBuilder errorMessage = new StringBuilder("Пожалуйста, исправьте следующие ошибки:\n");
        if (isNameEmpty) errorMessage.append("- Не указано название услуги\n");
        if (isTypeEmpty) errorMessage.append("- Не указан тип услуги\n");
        if (!isPriceValid) errorMessage.append("- Некорректная цена (должна быть числом, например: 1500.50)\n");
        if (!isLengthValid) errorMessage.append("- Некорректная длительность (должно быть целое число в минутах)\n");
        if (!isDateValid) errorMessage.append("- Не выбрана дата\n");
        if (!isTimeValid) errorMessage.append("- Не выбрано время\n");
        if (!isEmployeeSelected) errorMessage.append("- Не выбран сотрудник\n");

        Dialog.showAlert("Ошибка ввода", errorMessage.toString());
        return;
    }



    if (checkInput_add()) {
        Dialog.showAlertWithNullInput();
        return;
    }

    int employeeId = getSelectedEmployeeId();
    if (employeeId == -1) {
        Dialog.showAlertWithNullInput();
        return;
    }

    BeautyService service = new BeautyService();
    try {
        service.setName(name_field.getText());
        service.setType(type_field.getText());
        service.setPrice(Double.parseDouble(price_field.getText()));
        service.setLengthOfTime(Integer.parseInt(lengthOfTime_field1.getText()));

        String selectedDate = date_picker.getValue().toString();
        String selectedTime = time_comboBox.getValue();
        if (selectedTime == null) {
            Dialog.showAlertWithNullInput();
            return;
        }
        service.setTime(selectedDate + " " + selectedTime + ":00");
        service.setEmployeeName(Integer.toString(employeeId));
    } catch (NumberFormatException e) {
        Dialog.showAlertWithData();
        return;
    }

    try {
        Connect.client.sendMessage("addService");
        Connect.client.sendObject(service);

        String mes = Connect.client.readMessage();
        if (mes.equals("Incorrect Data")) {
            Dialog.showAlertWithUpdateService();
        } else {
            Dialog.correctOperation();
            goBack(); // Добавляем переход назад после успешного добавления
        }
    } catch (IOException ex) {
        System.out.println("Error in reading");
        //showAlert("Ошибка", "Ошибка связи с сервером");
    }
}

    private boolean checkInput_add() {
        try {
            return name_field.getText().isEmpty() ||
                    type_field.getText().isEmpty() ||
                    price_field.getText().isEmpty() ||
                    date_picker.getValue() == null ||
                    employee_choicebox.getValue() == null ||
                    time_comboBox.getValue() == null || // Проверка на пустое время
                    time_comboBox.getValue().isEmpty(); // Проверка на пустую строку
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return true;
        }
    }

    @FXML
    void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/workWithServiceDB.fxml"));
            Parent root = loader.load();

            // Получаем контроллер после загрузки FXML
            WorkWithServiceController controller = loader.getController();
            controller.setDeletedServices(deletedServices); // Передача удалённых услуг

            //--
            Stage stage = (Stage) BackUpButton.getScene().getWindow();
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