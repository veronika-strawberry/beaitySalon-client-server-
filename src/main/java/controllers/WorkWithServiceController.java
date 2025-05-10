package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientWork.Connect;
import Salon.BeautyService;
import javafx.application.Platform;
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

public class WorkWithServiceController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private TableColumn<BeautyService, Integer> IDColumn;

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
    private Button deleteServiceButton;

    @FXML
    private Button addEditServiceButton;

    @FXML
    private ComboBox<String> listSort;

    @FXML
    private TextField searchField;

    ObservableList<BeautyService> serviceList = FXCollections.observableArrayList();
    ObservableList<BeautyService> deletedServices = FXCollections.observableArrayList(); // Новый список для удалённых услуг

    @FXML
    public void initialize() {
        IDColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getId()));



        System.out.println("serviceListController инициализирован.");
        IDColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getId()));
        NameColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getName()));
        TypeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getType()));
        MasterColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getEmployeeName()));
        TimeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getTime()));
        PriceColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getPrice()));

        populateListSort(); // Заполнение списка фильтров
        loadServiceData(); // Вызов метода для загрузки данных

        // Обработчик для фильтрации
        listSort.setOnAction(event -> filterTable());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());

        deleteServiceButton.setOnAction(event -> deleteService());
        addEditServiceButton.setOnAction(event -> addEditService());
    }

    private void addEditService() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addEditService.fxml"));
            Parent root = loader.load();

            AddEditServiceController controller = loader.getController();
            controller.setDeletedServices(deletedServices);

            Stage stage = (Stage) addEditServiceButton.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Ошибка при загрузке окна работы с БД");
        }
    }

    public void setDeletedServices(ObservableList<BeautyService> deletedServices) {
        this.deletedServices = deletedServices;
        if (serviceList != null) {
            ObservableList<BeautyService> filteredList = FXCollections.observableArrayList(serviceList);
            filteredList.removeAll(deletedServices);
            serviceTable.setItems(filteredList);
        }
    }
    private void deleteService() {
        BeautyService selectedService = serviceTable.getSelectionModel().getSelectedItem();

        if (selectedService != null) { // Проверяем, выбрана ли услуга
            Connect.client.sendMessage("DELETE_SERVICE"); // Отправляем команду на удаление
            Connect.client.sendObject(selectedService); // Отправляем объект выбранной услуги

            System.out.println("Запись отправлена на удаление");

            try {
                String response = Connect.client.readMessage(); // Получаем ответ от сервера
                System.out.println(response);
                if (response.equals("OK")) {
                    // Заново загружаем данные с сервера, чтобы обновить serviceList
                    /*loadServiceData();*/
                    serviceList.remove(selectedService);
                    util.Dialog.correctOperation();
                } else {
                    util.Dialog.showAlertWithData();
                }

            } catch (IOException ex) {
                System.out.println("Ошибка при чтении ответа от сервера: " + ex.getMessage());
            }
        } else {
            Dialog.showAlert("Ошибка", "Пожалуйста, выберите услугу из таблицы."); // Всплывающее окно для уведомления
        }
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

        // Устанавливаем отфильтрованные данные в таблицу
        serviceTable.setItems(filteredList);
    }

    private void loadServiceData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                System.out.println("loadServiceData инициализирован.");

                Connect.client.sendMessage("GET_ALL_SERVICE");
                Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

                // Обработка в основном потоке JavaFX
                Platform.runLater(() -> {
                    try {
                        if (receivedObject instanceof List<?>) {
                            List<BeautyService> loadedServices = (List<BeautyService>) receivedObject;
                            serviceList.clear();
                            serviceList.addAll(loadedServices);

/*                            // Фильтруем удалённые услуги
                            serviceList.removeAll(deletedServices);*/

                            serviceTable.setItems(serviceList);
                            System.out.println("Данные успешно загружены: " + serviceList.size() + " услуг.");
                        } else {
                            throw new ClassCastException("Полученный объект не является списком.");
                        }
                    } catch (Exception e) {
                        showErrorAlert("Ошибка обработки данных: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        showErrorAlert("Неизвестная ошибка: " + e.getMessage()));
            } finally {
                executor.shutdown();
            }
        });
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

    @FXML
    void backToMenu(ActionEvent event) throws IOException {
        WindowChanger.changeWindow(getClass(), BackUpButton, "/workWithDB.fxml", "", false);
    }
}