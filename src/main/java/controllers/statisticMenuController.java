/*

package controllers;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import ClientWork.Connect;
import Salon.Order;
import Salon.StaffTimetable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Dialog;


public class statisticMenuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private ChoiceBox<Integer> MonthList;

    @FXML
    private ChoiceBox<Integer> YearList;

    @FXML
    private Button diagramButton;

    @FXML
    private Button listButton;

    @FXML
    private RadioButton monthrb;

    @FXML
    private RadioButton yearrb;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    void initialize() {
        // Заполняем ChoiceBox для месяцев
        List<Integer> months = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        MonthList.getItems().addAll(months);

        // Заполняем ChoiceBox для лет (например, от 2023 до 2028)
        List<Integer> years = Arrays.asList(2023, 2024, 2025, 2026, 2027, 2028);
        YearList.getItems().addAll(years);

        // Initialize ToggleGroup for RadioButtons
        toggleGroup = new ToggleGroup();
        monthrb.setToggleGroup(toggleGroup);
        yearrb.setToggleGroup(toggleGroup);

        // Установка слушателей для ChoiceBox
        MonthList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateListButtonState());
        YearList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateListButtonState());

        listButton.setOnAction(event -> handlelistButtonAction(event));
        BackUpButton.setOnAction(event -> handleBackButtonAction(event));
        diagramButton.setOnAction(event -> handleDiagramButtonClick(event));

        updateVisibility();
        toggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> updateVisibility());
        updateListButtonState(); // Изначальная проверка состояния кнопки
    }

    private void updateListButtonState() {
        boolean isMonthSelected = MonthList.getValue() != null;
        boolean isYearSelected = YearList.getValue() != null;
        listButton.setDisable(!(isMonthSelected && isYearSelected));
    }

    @FXML
    private void handleDiagramButtonClick(ActionEvent event) {
        try {
            // Проверяем, выбраны ли год и хотя бы один RadioButton
            Integer selectedYear = YearList.getValue();
            boolean isYearSelected = selectedYear != null;
            boolean isRadioButtonSelected = monthrb.isSelected() || yearrb.isSelected();

            if (!isYearSelected || !isRadioButtonSelected) {
                showAlert(Alert.AlertType.WARNING, "Предупреждение",
                        "Пожалуйста, выберите год и тип отчета.");
                return;
            }

            // Создаем новый объект StaffTimetable
            StaffTimetable data = new StaffTimetable();
            int day = monthrb.isSelected() ? 0 : 1; // Устанавливаем день на 0 или 1 в зависимости от выбора

            Integer selectedMonth = 0; // Изначально устанавливаем месяц в 0
            if (monthrb.isSelected()) {
                selectedMonth = MonthList.getValue(); // Проверяем и получаем месяц только если выбрана monthrb
                if (selectedMonth == null) {
                    showAlert(Alert.AlertType.WARNING, "Предупреждение",
                            "Пожалуйста, выберите месяц.");
                    return; // Прерываем выполнение, если месяц не выбран
                }
            }

            String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, day);
            data.setDate(date); // Устанавливаем дату

            System.out.println("Selected date: " + date);

            Connect.client.sendMessage("GET_DATA_DIAGRAM");
            Connect.client.sendObject(data);
            if(monthrb.isSelected()) loadScene("/diagramInMonth.fxml", "Диаграмма занятости сотрудников");
            else loadScene("/diagramInYear.fxml", "Диаграмма занятости сотрудников");
        } catch (Exception e) {
            showErrorAlert("Ошибка при получении данных: " + e.getMessage());
        }
    }

    private void updateVisibility() {//скрытие ненужных элементов интерфейса
        if (monthrb.isSelected()) {
            YearList.setVisible(true);
            MonthList.setVisible(true);
            // Дополнительные действия для отображения
        } else if (yearrb.isSelected()) {
            YearList.setVisible(true);
            MonthList.setVisible(false);
            // Дополнительные действия для отображения
        }
    }

    private void handlelistButtonAction(ActionEvent event) {
        try {
            // Проверяем, выбраны ли год и хотя бы один RadioButton
            Integer selectedYear = YearList.getValue();
            boolean isYearSelected = selectedYear != null;
            boolean isRadioButtonSelected = monthrb.isSelected() || yearrb.isSelected();

            if (!isYearSelected || !isRadioButtonSelected) {
                showAlert(Alert.AlertType.WARNING, "Предупреждение",
                        "Пожалуйста, выберите год и тип отчета.");
                return;
            }

            // Создаем новый объект StaffTimetable
            StaffTimetable data = new StaffTimetable();
            int day = monthrb.isSelected() ? 0 : 1; // Устанавливаем день на 0 или 1 в зависимости от выбора

            Integer selectedMonth = 0; // Изначально устанавливаем месяц в 0
            if (monthrb.isSelected()) {
                selectedMonth = MonthList.getValue(); // Проверяем и получаем месяц только если выбрана monthrb
                if (selectedMonth == null) {
                    showAlert(Alert.AlertType.WARNING, "Предупреждение",
                            "Пожалуйста, выберите месяц.");
                    return; // Прерываем выполнение, если месяц не выбран
                }
            }

            String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, day);
            data.setDate(date); // Устанавливаем дату

            System.out.println("Selected date: " + date);

            // Использование FileChooser для выбора места сохранения
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить файл CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("EmployeeWorkTime_" + selectedYear + (monthrb.isSelected() ? "_" + selectedMonth : "") + ".csv");

            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
            data.setNameService(file.getAbsolutePath());

                if (file != null) {
                    Connect.client.sendMessage("exportToCSV");
                    Connect.client.sendObject(data);

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
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Информация", "Сохранение отменено.");
            }
        } catch (Exception e) {
            showErrorAlert("Ошибка при получении данных: " + e.getMessage());
        }
    }


    private void handleBackButtonAction(ActionEvent event) {

        loadScene("/menuAdmin.fxml", "Меню администратора");
    }


   private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage currentStage = (Stage) BackUpButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибки: можно показать сообщение пользователю
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
*/


package controllers;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import ClientWork.Connect;
import Salon.Order;
import Salon.StaffTimetable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.Dialog;

public class statisticMenuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackUpButton;

    @FXML
    private ChoiceBox<Integer> MonthList;

    @FXML
    private ChoiceBox<Integer> YearList;

    @FXML
    private Button diagramButton;

    @FXML
    private Button listButton;

    @FXML
    private RadioButton monthrb;

    @FXML
    private RadioButton yearrb;

    @FXML
    private ToggleGroup toggleGroup;


    @FXML
    private Label monthSelectionLabel;

    @FXML
    void initialize() {
        // Заполняем ChoiceBox для месяцев
        List<Integer> months = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        MonthList.getItems().addAll(months);

        // Заполняем ChoiceBox для лет (например, от 2023 до 2028)
        List<Integer> years = Arrays.asList(2023, 2024, 2025, 2026, 2027, 2028);
        YearList.getItems().addAll(years);

        // Initialize ToggleGroup for RadioButtons
        toggleGroup = new ToggleGroup();
        monthrb.setToggleGroup(toggleGroup);
        yearrb.setToggleGroup(toggleGroup);

        // Установка слушателей для ChoiceBox и RadioButtons
        MonthList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateButtonsState());
        YearList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateButtonsState());
        toggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            updateVisibility();
            updateButtonsState();
        });

        listButton.setOnAction(event -> handlelistButtonAction(event));
        BackUpButton.setOnAction(event -> handleBackButtonAction(event));
        diagramButton.setOnAction(event -> handleDiagramButtonClick(event));

        updateVisibility();
        updateButtonsState(); // Изначальная блокировка кнопок
    }

    private void updateButtonsState() {
        boolean isYearSelected = YearList.getValue() != null;
        boolean isRadioButtonSelected = monthrb.isSelected() || yearrb.isSelected();

        // Для кнопки "Получить отчет" нужен и год, и месяц (если выбран месяц)
        boolean isListButtonEnabled = isYearSelected && isRadioButtonSelected &&
                (!monthrb.isSelected() || MonthList.getValue() != null);

        // Для кнопки "Получить диаграмму" нужен год и выбор типа (месяц/год)
        boolean isDiagramButtonEnabled = isYearSelected && isRadioButtonSelected &&
                (!monthrb.isSelected() || MonthList.getValue() != null);

        listButton.setDisable(!isListButtonEnabled);
        diagramButton.setDisable(!isDiagramButtonEnabled);
    }

    private void updateVisibility() {
        if (monthrb.isSelected()) {
            YearList.setVisible(true);
            MonthList.setVisible(true);
        } else if (yearrb.isSelected()) {
            YearList.setVisible(true);
            MonthList.setVisible(false);
        }
    }

    @FXML
    private void handleDiagramButtonClick(ActionEvent event) {
        try {
            Integer selectedYear = YearList.getValue();
            Integer selectedMonth = monthrb.isSelected() ? MonthList.getValue() : 0;

            StaffTimetable data = new StaffTimetable();
            int day = monthrb.isSelected() ? 0 : 1;
            String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, day);
            data.setDate(date);

            System.out.println("Selected date: " + date);

            Connect.client.sendMessage("GET_DATA_DIAGRAM");
            Connect.client.sendObject(data);
            if(monthrb.isSelected())
                loadScene("/diagramInMonth.fxml", "Диаграмма занятости сотрудников");
            else
                loadScene("/diagramInYear.fxml", "Диаграмма занятости сотрудников");
        } catch (Exception e) {
            showErrorAlert("Ошибка при получении данных: " + e.getMessage());
        }
    }

    private void handlelistButtonAction(ActionEvent event) {
        try {
            Integer selectedYear = YearList.getValue();
            Integer selectedMonth = monthrb.isSelected() ? MonthList.getValue() : 0;

            StaffTimetable data = new StaffTimetable();
            int day = monthrb.isSelected() ? 0 : 1;
            String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, day);
            data.setDate(date);

            System.out.println("Selected date: " + date);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить файл CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("EmployeeWorkTime_" + selectedYear +
                    (monthrb.isSelected() ? "_" + selectedMonth : "") + ".csv");

            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) {
                data.setNameService(file.getAbsolutePath());
                Connect.client.sendMessage("exportToCSV");
                Connect.client.sendObject(data);

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
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Информация", "Сохранение отменено.");
            }
        } catch (Exception e) {
            showErrorAlert("Ошибка при получении данных: " + e.getMessage());
        }
    }

    private void handleBackButtonAction(ActionEvent event) {
        loadScene("/menuAdmin.fxml", "Меню администратора");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage currentStage = (Stage) BackUpButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
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