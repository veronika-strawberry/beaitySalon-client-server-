package main;

import ClientWork.Connect;
import Salon.Consumer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/START.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Косметический салон");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Connect.client = new ClientWork.Client("127.0.0.2", "9006");
            System.out.println("Клиент запущен");
            Consumer consumer = new Consumer();
/*            consumer.setSurname("1");
            consumer.setName("1");
            consumer.setLogin("1");
            consumer.setPassword("1");*/
            Connect.client.sendObject(consumer);
            launch();
        } catch (Exception e) {
            System.out.println("Ошибка при запуске клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }

}