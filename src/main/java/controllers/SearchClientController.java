package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientWork.Connect;
import Salon.Consumer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SearchClientController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<Consumer, String> ID_field;

    @FXML
    private TableColumn<Consumer, String> NameColumn;

    @FXML
    private Button backUpButton;


    @FXML
    private TableColumn<Consumer, String> emailColumn;

    @FXML
    private TableView<Consumer> consumerTable;

    @FXML
    private TableColumn<Consumer, String> lastNameColumn;

    @FXML
    private TableColumn<Consumer, String> loginColumn;

    @FXML
    private TextField search_field;

    ObservableList<Consumer> consumerList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        System.out.println("Controller инициализирован.");
        ID_field.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject().asString());

        loginColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLogin()));

        NameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        lastNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSurname()));


        emailColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));
       // consumerList.clear();
        consumerList.clear();
        loadConsumerData();
        search_field.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        backUpButton.setOnAction(event -> goBack());

    }
    //deep
    private void filterTable() {
        String searchTerm = search_field.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            consumerTable.setItems(consumerList); // Показываем все данные при пустом поиске
            return;
        }

        ObservableList<Consumer> filteredList = consumerList.filtered(c ->
                (c.getLogin() != null && c.getLogin().toLowerCase().contains(searchTerm)) ||
                        (c.getName() != null && c.getName().toLowerCase().contains(searchTerm)) ||
                        (c.getSurname() != null && c.getSurname().toLowerCase().contains(searchTerm))
        );

        consumerTable.setItems(filteredList);
    }
    private void loadConsumerData() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                System.out.println("loadConsumerData инициализирован.");
                Connect.client.sendMessage("GET_ALL_CONSUMER");
                Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

                // Обработка в основном потоке
                Platform.runLater(() -> {
                    if (receivedObject instanceof List<?>) {
                        List<Consumer> loadedCon = (List<Consumer>) receivedObject;
                        consumerList.clear();
                        consumerList.addAll(loadedCon);
                        consumerTable.setItems(consumerList);
                        System.out.println("Данные успешно загружены: " + consumerList.size() + " клиентов.");
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
            String fxmlFile = Connect.role.equals("Admin") ? "/workWithDB.fxml" : "/menuEmployee.fxml"; // Check user role
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) backUpButton.getScene().getWindow();
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

}
