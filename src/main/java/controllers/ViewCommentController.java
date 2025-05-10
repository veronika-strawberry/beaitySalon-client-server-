package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import ClientWork.Connect;
import Salon.Comment;
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

public class ViewCommentController {

    @FXML
    private Button BackUpButton;

    @FXML
    private TableColumn<Comment, Double> MarkColumn;

    @FXML
    private TableColumn<Comment, String> NameColumn;

    @FXML
    private Button confirmButton;

    @FXML
    private TableColumn<Comment, Integer> idColumn;

    @FXML
    private ChoiceBox<Integer> markOfService;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<Comment, String> typeColumn;

    @FXML
    private ChoiceBox<String> typeOfService;

    @FXML
    private TableView<Comment> tableComment;

    ObservableList<Comment> commentList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        idColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getId()));
        NameColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getName()));
        typeColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getType()));
        MarkColumn.setCellValueFactory(field -> new SimpleObjectProperty<>(field.getValue().getResult_mark()));
        // Добавьте форматирование для колонки с оценкой
        MarkColumn.setCellFactory(column -> new TableCell<Comment, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Округляем до сотых и форматируем вывод
                    setText(String.format("%.2f", item));
                }
            }
        });
        populateListSort(); // Заполнение списка фильтров
        loadCommentData(); // Вызов метода для загрузки данных
        populateMarkChoices(); // Заполнение выпадающего списка оценок

        // Обработчик для фильтрации
        typeOfService.setOnAction(event -> filterTable());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTable());

        // Слушатель для активации кнопки "Подтвердить"
        markOfService.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateConfirmButtonState());
        tableComment.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateConfirmButtonState());
    }

    @FXML
    void confirmRating(ActionEvent event) {
        Comment selectedComment = tableComment.getSelectionModel().getSelectedItem();
        Integer selectedMark = markOfService.getValue();

        if (selectedComment != null && selectedMark != null) {
            double r_mark=addMark(selectedComment, selectedMark); // Добавляем оценку в историю
            selectedComment.setResult_mark(r_mark);



            Connect.client.sendMessage("UPDATE_COMMENT");
            Connect.client.sendObject(selectedComment);//обновили переменную и отправляем
            System.out.println("Запись отправлена");

            try {
                String response = Connect.client.readMessage();
                System.out.println(response);
                if (response.equals("Ошибка при удалении заявки")) {
                    util.Dialog.showAlertWithData(); // Показываем сообщение об ошибке
                } else {
                    util.Dialog.correctOperation(); // Сообщение об успешном удалении
                    loadCommentData();
                }
            } catch (IOException ex) {
                System.out.println("Ошибка при чтении ответа от сервера: " + ex.getMessage());
                //Dialog.showErrorAlert("Ошибка при удалении сотрудника.");
            }

            System.out.println("Оценка обновлена для комментария с ID: " + selectedComment.getId());
            System.out.println("История оценок: " + selectedComment.getMarkHistory());
        }
    }

    public double addMark(Comment selectedComment, Integer selectedMark) {
        String currentHistory = selectedComment.getMarkHistory();
        System.out.println("Все оценки до добавления: " + currentHistory);

        // Добавление новой оценки в историю
        if (currentHistory == null || currentHistory.isEmpty()) {
            selectedComment.setMarkHistory(String.valueOf(selectedMark));
        } else {
            selectedComment.setMarkHistory(currentHistory + " " + selectedMark);
        }

        double resultMark = 0.0;
        int count = 0;
        double sum = 0.0;

        // Объединяем текущую историю и новую оценку
        String[] marks = (currentHistory == null ? "" : currentHistory).split(" ");

        // Добавляем новую оценку в массив
        String[] allMarks = new String[marks.length + 1];
        System.arraycopy(marks, 0, allMarks, 0, marks.length);
        allMarks[marks.length] = String.valueOf(selectedMark);

        // Подсчет суммы и количества оценок, игнорируя 0
        for (String markStr : allMarks) {
            try {
                int mark = Integer.parseInt(markStr.trim());
                if (mark > 0) { // Игнорируем оценки, равные 0
                    sum += mark;
                    count++;
                }
            } catch (NumberFormatException e) {
                System.err.println("Некорректная оценка в истории: " + markStr);
            }
        }

        // Подсчет среднего и округление до сотых
        resultMark = count > 0 ? sum / count : 0.0;
        resultMark = Math.round(resultMark * 100) / 100.0;  // Округление до сотых

        return resultMark;
    }
   /* public double addMark(Comment selectedComment, Integer selectedMark) {
        String currentHistory = selectedComment.getMarkHistory();
        System.out.println("Все оценки до добавления: " + currentHistory);

        // Добавление новой оценки в историю
        if (currentHistory == null || currentHistory.isEmpty()) {
            selectedComment.setMarkHistory(String.valueOf(selectedMark));
        } else {
            selectedComment.setMarkHistory(currentHistory + " " + selectedMark);
        }

        double resultMark = 0.0;
        int count = 0;
        double sum = 0.0;

        // Объединяем текущую историю и новую оценку
        String[] marks = (currentHistory == null ? "" : currentHistory).split(" ");

        // Добавляем новую оценку в массив
        String[] allMarks = new String[marks.length + 1];
        System.arraycopy(marks, 0, allMarks, 0, marks.length);
        allMarks[marks.length] = String.valueOf(selectedMark);

        // Подсчет суммы и количества оценок, игнорируя 0
        for (String markStr : allMarks) {
            try {
                int mark = Integer.parseInt(markStr.trim());
                if (mark > 0) { // Игнорируем оценки, равные 0
                    sum += mark;
                    count++;
                }
            } catch (NumberFormatException e) {
                System.err.println("Некорректная оценка в истории: " + markStr);
            }
        }

        // Подсчет среднего
        resultMark = count > 0 ? sum / count : 0.0;
        return resultMark;
    }*/

    private void loadCommentData() {
        try {
            System.out.println("loadCommentData инициализирован.");
            Connect.client.sendMessage("GET_ALL_COMMENTS");
            Object receivedObject = Connect.client.readObject(); // Чтение данных от сервера

            if (receivedObject instanceof String && receivedObject.equals("NO_DATA")) {
                showAlert(Alert.AlertType.WARNING, "Нет данных", "Нет доступных отзывов для отображения.");
            } else if (receivedObject instanceof List<?>) {
                List<Comment> availableServices = (List<Comment>) receivedObject; // Правильный кастинг
                commentList.clear();
                commentList.addAll(availableServices);
                if (tableComment != null) {
                    tableComment.setItems(commentList);
                    System.out.println("Данные успешно загружены: " + commentList.size() + " услуг.");
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

    private void filterTable() {
        String selectedOption = typeOfService.getValue();
        String searchTerm = searchField.getText().toLowerCase(); // Получаем текст для поиска

        // Создаем список для отфильтрованных данных
        ObservableList<Comment> filteredList = FXCollections.observableArrayList();

        // Фильтрация по типу услуги и имени услуги
        for (Comment service : commentList) {
            boolean matchesType = selectedOption == null || selectedOption.equals("Все") || service.getType().equals(selectedOption);
            boolean matchesName = service.getName().toLowerCase().contains(searchTerm);

            // Если оба условия соответствуют, добавляем сервис в список
            if (matchesType && matchesName) {
                filteredList.add(service);
            }
        }

        // Устанавливаем отфильтрованные данные в таблицу
        tableComment.setItems(filteredList);
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
        typeOfService.setItems(sortOptions);
    }

    private void populateMarkChoices() {
        ObservableList<Integer> marks = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        markOfService.setItems(marks);
    }

    private void updateConfirmButtonState() {
        boolean isDisabled = markOfService.getValue() == null || tableComment.getSelectionModel().isEmpty();
        confirmButton.setDisable(isDisabled);
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        String fxmlFile = Connect.role.equals("Admin") ? "/menuAdmin.fxml" : "/menuClient.fxml"; // Check user role
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = (Stage) BackUpButton.getScene().getWindow();
        stage.setScene(new Scene(root));
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