package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import ClientWork.Connect;
import Salon.EmployeeWorkTime;
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

public class diagramInYearController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private BarChart<String, Number> diagramAria;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    ObservableList<EmployeeWorkTime> workTimes = FXCollections.observableArrayList();

    @FXML
    void BackMenuDB(ActionEvent event) throws IOException {
        WindowChanger.changeWindow(getClass(), BackUpButton, "/statisticMenu.fxml", "Статистика салона", false);
    }

    @FXML
    void initialize() {
        // Убираем поворот подписей на оси X
        xAxis.setTickLabelRotation(0);
        // Дополнительные настройки для лучшего отображения
        xAxis.setTickMarkVisible(false);
        xAxis.setTickLabelsVisible(false); // Скрываем подписи на оси X

        loadServiceData();
    }



    public void updateChart(List<EmployeeWorkTime> data) {
        diagramAria.getData().clear();
        diagramAria.setAnimated(true); // Включение анимации

        String[] colors = {"#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"};

        for (int i = 0; i < data.size(); i++) {
            EmployeeWorkTime entry = data.get(i);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(entry.getFio());
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(entry.getFio(), entry.getTotalHours());
            series.getData().add(dataPoint);

            diagramAria.getData().add(series);

            // Установка цвета после появления столбца
            int finalI = i;
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle(String.format("-fx-bar-fill: %s;", colors[finalI % colors.length]));
                    Tooltip.install(newNode, new Tooltip(String.format("%s: %.1f часов",
                            entry.getFio(), entry.getTotalHours())));
                }
            });
        }
    }
 /*   public void updateChart(List<EmployeeWorkTime> data) {
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

            if (receivedObject instanceof String) {
                String response = (String) receivedObject;
                if (response.equals("NO_DATA")) {
                    showAlert(Alert.AlertType.WARNING, "Нет данных", "Нет доступных сотрудников для отображения.");
                } else if (response.equals("INVALID_DATA_TYPE")) {
                    showAlert(Alert.AlertType.ERROR, "Ошибка", "Получены данные неверного типа.");
                }
            } else if (receivedObject instanceof List<?>) {
                List<EmployeeWorkTime> loadedOrder = (List<EmployeeWorkTime>) receivedObject;
                workTimes.clear();
                workTimes.addAll(loadedOrder);
                updateChart(workTimes); // Обновляем диаграмму
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
