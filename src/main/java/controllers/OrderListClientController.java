package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import ClientWork.Connect;
import Salon.BeautyService;
import Salon.Order;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import util.WindowChanger;

public class OrderListClientController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private TableColumn<Order, Integer> IDColumn;

    @FXML
    private TableColumn<Order, String> MasterColumn;

    @FXML
    private TableColumn<Order, String> NameColumn;

    @FXML
    private TableColumn<Order, String> ConfirmColumn;

    @FXML
    private TableColumn<Order, String> TimeColumn;

    @FXML
    private TableColumn<Order, String> TypeColumn;

    @FXML
    private TableView<Order> orderTable;

    @FXML
    private ComboBox<String> listSort;

    @FXML
    private TextField searchField;
    ObservableList<Order> orderList = FXCollections.observableArrayList();

    @FXML
    public void backToMenu(ActionEvent actionEvent) throws IOException {
        WindowChanger.changeWindow(getClass(), BackUpButton, "/serviceList.fxml", "Просмотр услуг", false);

    }

    @FXML
    void initialize() {

        IDColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getId()));
        NameColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getName()));
        TypeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getType()));
        MasterColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getEmployeeName()));
        TimeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getTime()));

        ConfirmColumn.setCellValueFactory(field -> {
            boolean confirmed = field.getValue().isConfirmation();
            return new SimpleStringProperty(confirmed ? "подтверждено" : "не подтверждено");
        });

        populateListSort(); // Заполнение списка фильтров
        loadServiceData(); // Вызов метода для загрузки данных
        // Обработчик для фильтрации
        listSort.setOnAction(event -> filterTable());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());

    }
    private void filterTable() {
       String selectedOption =  listSort.getValue();
        String searchTerm = searchField.getText().toLowerCase(); // Получаем текст для поиска

        // Создаем список для отфильтрованных данных
        ObservableList<Order> filteredList = FXCollections.observableArrayList();

        // Фильтрация по типу услуги и имени услуги
        for (Order order : orderList) {
            boolean matchesType = selectedOption == null || selectedOption.equals("Все") || order.getType().equals(selectedOption);
            boolean matchesName = order.getName().toLowerCase().contains(searchTerm);

            // Если оба условия соответствуют, добавляем сервис в список
            if (matchesType && matchesName) {
                filteredList.add(order);
            }
        }


        // Устанавливаем отфильтрованные данные в таблицу
        orderTable.setItems(filteredList);
    }

    private void loadServiceData() {
        try {
            System.out.println("loadServiceData инициализирован.");
            Connect.client.sendMessage("GET_ORDERS_BY_CLIENT"); // Новый запрос к серверу
            Connect.client.sendObject(Connect.id); // Отправьте идентификатор клиента
            Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

            if (receivedObject instanceof String && receivedObject.equals("NO_DATA")) {
                showAlert(Alert.AlertType.WARNING, "Нет данных", "Нет доступных заявок для отображения.");
            } else if (receivedObject instanceof List<?>) {
                List<Order> loadedOrder = (List<Order>) receivedObject; // Правильный кастинг
                orderList.clear();
                orderList.addAll(loadedOrder);
                if (orderTable != null) {
                    orderTable.setItems(orderList);
                    System.out.println("Данные успешно загружены: " + orderList.size() + " заявок.");
                } else {
                    System.out.println("orderTable не инициализирован.");
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


    // Метод для заполнения списка сортировки
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
    private ObservableList<Order> getOrders() {
        ObservableList<Order> serviceList = FXCollections.observableArrayList();
        System.out.println("Попытка получить заявки...");
        try {
            List<Order> services = (List<Order>) Connect.client.readObject();
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

        orderTable.setItems(orderList); // Устанавливайте список только один раз.
        return serviceList;
    }


}
