package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientWork.Connect;
import Salon.EmployeeWorkTime;
import Salon.Order;
import Salon.StaffTimetable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import util.WindowChanger;

public class diagramInMonthController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BarChart<String, Number> diagramAria;

    @FXML
    private Button BackUpButton;
    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private final String[] COLORS = {
            "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0",
            "#9966FF", "#FF9F40", "#8AC24A", "#607D8B"
    };

    ObservableList<EmployeeWorkTime> workTimes = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        diagramAria.setTitle("Загруженность сотрудников в месяц");
        xAxis.setLabel("Сотрудник"); // Установка заголовка для оси X
        yAxis.setLabel("Часы"); // Установка заголовка для оси Y
        xAxis.setTickLabelsVisible(false); // Скрываем подписи сотрудников
        xAxis.setTickMarkVisible(false); // Скрываем метки на оси
        loadServiceData(); // Загружаем данные при инициализации

    }

    @FXML
    void BackMenuDB(ActionEvent event) throws IOException {
        WindowChanger.changeWindow(getClass(), BackUpButton, "/statisticMenu.fxml", "Статистика салона", false);
    }


    public void updateChart(List<EmployeeWorkTime> data) {
        diagramAria.getData().clear();
        diagramAria.setLegendVisible(true); // Оставляем легенду видимой

        for (int i = 0; i < data.size(); i++) {
            EmployeeWorkTime entry = data.get(i);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(entry.getFio()); // Имя для легенды

            // Используем пустую строку вместо ФИО для подписей на оси X
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>("", entry.getTotalHours());
            series.getData().add(dataPoint);
            diagramAria.getData().add(series);

            // Установка цвета и подсказки
            int finalI = i;
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    String color = COLORS[finalI % COLORS.length];
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                    Tooltip.install(newNode,
                            new Tooltip(String.format("%s\n%.1f часов", entry.getFio(), entry.getTotalHours())));
                }
            });
        }
    }
/*    public void updateChart1(List<EmployeeWorkTime> data) {
        // Очистка старых данных
        diagramAria.getData().clear();

        // Создание серии данных
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Добавление данных сотрудников в серию
        for (EmployeeWorkTime entry : data) {
            series.getData().add(new XYChart.Data<>(entry.getFio(), entry.getTotalHours()));
        }

        // Добавление серии в диаграмму
        diagramAria.getData().add(series);
    }*/

    private void loadServiceData() {
        try {
            System.out.println("loadServiceData инициализирован.");
            Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

            if (receivedObject instanceof String && receivedObject.equals("NO_DATA")) {
                showAlert(Alert.AlertType.WARNING, "Нет данных", "Нет доступных сотрудников для отображения.");
            } else if (receivedObject instanceof List<?>) {
                List<EmployeeWorkTime> loadedOrder = (List<EmployeeWorkTime>) receivedObject; // Правильный кастинг
                workTimes.clear();
                workTimes.addAll(loadedOrder);

                System.out.println("Данные успешно загружены: " + workTimes.size() + " сотрудников");

                // Обновляем диаграмму с новыми данными
                updateChart(workTimes); // Замените на нужный год и месяц
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
}