package Salon;

import java.io.Serializable;

public class RecordService implements Serializable {
    private int id;                 // Уникальный идентификатор услуги
    private String name;            // Название услуги
    private String time;            // Время услуги
    private String type;            // Тип услуги (например, стрижка, макияж)
    private double price;           // Цена услуги

    public RecordService() {
        // Конструктор по умолчанию
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "BeautyService{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", time='" + this.time + '\'' +
                ", type='" + this.type + '\'' +
                ", price=" + this.price +
                '}';
    }
}