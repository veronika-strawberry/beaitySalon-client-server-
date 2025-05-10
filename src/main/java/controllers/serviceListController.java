package controllers;

import ClientWork.Connect;
import Salon.BeautyService;
import Salon.Consumer;
import Salon.Employee;
import Salon.Order;
import javafx.beans.property.SimpleObjectProperty;
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

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class serviceListController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private CheckBox Check;

    @FXML
    private TableColumn<BeautyService, String> MasterColumn;

    @FXML
    private TableColumn<BeautyService, String> NameColumn;

    @FXML
    private TableColumn<BeautyService, Double> PriceColumn;

    @FXML
    private TableColumn<BeautyService, String> TimeColumn;

    @FXML
    private TableColumn<BeautyService, String> TypeColumn;

    @FXML
    private TableView<BeautyService> serviceTable;

    @FXML
    private Button addToListOrderButton;

    @FXML
    private Button goToListOrderButton;

    @FXML
    private ComboBox<String> listSort;


    @FXML
    private TextField searchField;

    ObservableList<BeautyService> serviceList = FXCollections.observableArrayList();
    @FXML
    public void initialize() {

        NameColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getName()));
        TypeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getType()));
        MasterColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getEmployeeName()));
        TimeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getTime()));
        PriceColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getPrice()));
       // serviceTable.setItems(getServices());

        populateListSort(); // Заполнение списка фильтров
        loadServiceData(); // Вызов метода для загрузки данных

        // Обработчик для фильтрации
        listSort.setOnAction(event -> filterTable());
        Check.setOnAction(event -> {
            System.out.println("Checkbox is selected: " + Check.isSelected());
            filterTable();
        });// Добавление обработчика для чекбокса

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());

        goToListOrderButton.setOnAction(event -> goToListOrder());
        addToListOrderButton.setOnAction(event -> addListOrder());
    }
    private void filterTable() {
        String selectedOption = listSort.getValue();
        String searchTerm = searchField.getText().toLowerCase(); // Получаем текст для поиска

        // Создаем список для отфильтрованных данных
        ObservableList<BeautyService> filteredList = FXCollections.observableArrayList();

        // Фильтрация по типу услуги и имени услуги
        for (BeautyService service : serviceList) {
            boolean matchesType = selectedOption == null || selectedOption.equals("Все") || service.getType().equals(selectedOption);
            boolean matchesName = service.getName().toLowerCase().contains(searchTerm);

            // Если оба условия соответствуют, добавляем сервис в список
            if (matchesType && matchesName) {
                filteredList.add(service);
            }
        }

        // Если CheckBox отмечен, сортируем по цене
        if (Check.isSelected()) {
            filteredList.sort(Comparator.comparingDouble(BeautyService::getPrice));
        }

        // Устанавливаем отфильтрованные данные в таблицу
        serviceTable.setItems(filteredList);
    }

private void loadServiceData() {
    try {
        System.out.println("loadServiceData инициализирован.");
        Connect.client.sendMessage("GET_ALL_SERVICE");
        Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

        if (receivedObject instanceof String && receivedObject.equals("NO_DATA")) {
            showAlert(Alert.AlertType.WARNING, "Нет данных", "Нет доступных услуг для отображения.");
        } else if (receivedObject instanceof List<?>) {
            List<BeautyService> availableServices = (List<BeautyService>) receivedObject; // Правильный кастинг
            serviceList.clear();
            serviceList.addAll(availableServices);
            if (serviceTable != null) {
                serviceTable.setItems(serviceList);
                System.out.println("Данные успешно загружены: " + serviceList.size() + " услуг.");
            } else {
                System.out.println("serviceTable не инициализирован.");
            }
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
        showAlert(Alert.AlertType.ERROR, "Ошибка", message);
    }


    private void populateListSort() {
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
                "Маникюр",
                "Макияж",
                "Прическа",
                "Педикюр",
                "Спа-процедуры",
                "Депиляция",
                "Уход за лицом",
                "Массаж",
                "Брови",
                "Ресницы",
                "Все"
        );
        listSort.setItems(sortOptions);
    }
    private ObservableList<BeautyService> getServices() {
        ObservableList<BeautyService> serviceList = FXCollections.observableArrayList();
        System.out.println("Попытка получить услуги...");
        try {
            List<BeautyService> services = (List<BeautyService>) Connect.client.readObject();
            System.out.println(services);
            serviceList.addAll(services);
           if (services != null) {
                System.out.println("Данные получены от сервера: " + services.size());
               // serviceList.addAll(services);
            } else {
                System.out.println("Получены пустые данные от сервера.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
        }

        serviceTable.setItems(serviceList); // Устанавливайте список только один раз.
        return serviceList;
    }

@FXML
void backToMenu(ActionEvent event) throws IOException {
    if (Connect.role.equals("Employee"))
        WindowChanger.changeWindow(getClass(), BackUpButton, "/menuEmployee.fxml", "", false);
    else if(Connect.role.equals("Consumer"))
        WindowChanger.changeWindow(getClass(), BackUpButton, "/menuClient.fxml", "", false);
}

    private void goToListOrder() {
        try {
           // Connect.client.sendMessage("GET_ALL_ORDER");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderListClient.fxml")); // Обновите, если имя файла другое
            Parent root = loader.load();
            Stage stage = (Stage) goToListOrderButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private void addListOrder() {//БЕЗ УДАЛЕНИЯ
        // Проверяем, выбран ли какой-либо объект в таблице
        BeautyService selectedService = serviceTable.getSelectionModel().getSelectedItem();
        if (selectedService == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Пожалуйста, выберите услугу для добавления в заказ.");
            return; // Выходим из метода, если ничего не выбрано
        }

        // Создаем окно подтверждения
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Подтверждение покупки");
        confirmationAlert.setHeaderText("Вы уверены в покупке?");
        confirmationAlert.setContentText("Выбранная услуга: " + selectedService.getName() +
                "\nЦена услуги: " + selectedService.getPrice() + " руб.");

        // Добавляем кнопки "Да" и "Нет"
        ButtonType yesButton = new ButtonType("Да");
        ButtonType noButton = new ButtonType("Нет");
        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        // Отображаем окно и ждем ответа пользователя
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            Consumer consumer = new Consumer();
            consumer.setId(Connect.id);

            // Создаем новый объект заказа с пустыми значениями для дополнительных полей
            Order newOrder = new Order();
            newOrder.setIdUser(Connect.id);
            newOrder.setIdRecordingService(selectedService.getId()); // Предполагается, что у BeautyService есть метод getId()
            newOrder.setConfirmation(false); // Установим подтверждение заказа в false

            // Устанавливаем остальные поля в нулевые значения
            newOrder.setName(null); // Или "" в зависимости от вашей логики
            newOrder.setType(null);
            newOrder.setEmployeeName(null);
            newOrder.setTime(null);

            // Добавляем новый заказ в список
            Connect.client.sendMessage("addOrder"); // покупка
            Connect.client.sendObject(newOrder);
            System.out.println("Запись отправлена");

            String mes = "";
            try {
                mes = Connect.client.readMessage();
            } catch (IOException ex) {
                System.out.println("Error in reading");
            }
           /* if (mes.equals("Incorrect Data")) {
                util.Dialog.showAlertWithExistLogin();
            } else {
                Dialog.correctOperation();
            }*/

            // Выводим сообщение об успешном добавлении
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Услуга успешно добавлена в заказ.");
        } else {
            // Если пользователь нажал "Нет", ничего не делаем
            System.out.println("Покупка отменена.");
        }
    }

}





