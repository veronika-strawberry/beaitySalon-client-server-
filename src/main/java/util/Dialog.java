package util;

import javafx.scene.control.Alert;

public class Dialog {
    static public void showAlertWithNullInput() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Ввод данных");
        alert.setContentText("Заполните пустые поля");
        alert.show(); // Используем show() вместо showAndWait()
    }

    static public void showAlertWithExistLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Регистрация");
        alert.setContentText("Такой пользователь уже существует");
        alert.show(); // Здесь тоже show()
    }

    static public void showAlertWithNoLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Введите правильно логин или пароль");
        alert.setContentText("Такой пользователь не найден в системе");
        alert.show(); // Здесь тоже show()
    }

    static public void showAlertWithData() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Сбой задачи");
        alert.setContentText("Проверьте введенные параметры");
        alert.show(); // Здесь тоже show()
    }

    static public void correctOperation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Корректно");
        alert.setHeaderText(null);
        alert.setContentText("Операция прошла успешно");
        alert.show(); // Здесь тоже show()
    }

    static public void showAlertWithDouble() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка 500: Ввод двоичных чисел");
        alert.setContentText("Заполните правильно цену/вес");
        alert.show(); // Здесь тоже show()
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // Здесь тоже show()
    }

    public static void showAlertWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // Здесь тоже show()
    }

    public static void showAlertWithException(Exception e) {
    }

    public static void showAlertWithUpdateService() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Услуга не обновлена");
        alert.setContentText("Данные некорректны");
        alert.show(); // Здесь тоже show()
    }
}