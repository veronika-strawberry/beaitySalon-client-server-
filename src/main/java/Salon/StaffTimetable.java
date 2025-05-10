/*
package Salon;

import java.io.Serializable;

public class StaffTimetable implements Serializable {//купленные услуги и подтвержденные мастером
 //НЕ ПРИВАТНЫЕ ПОЛЯ
    int id;
    int employeeId;
    String workTime;       // Время работы

    private int idUser;             // Идентификатор клиента
    private int idRecordingService;  // Идентификатор услуги
    private boolean confirmation;     // Подтверждение заказа
    private String name;             // Название услуги
    private String type;             // Тип услуги
    private String employeeName;     // Имя мастера
    private String time;             // Время выполнения услуги


    // Конструктор
    public StaffTimetable() {
        // Инициализация по умолчанию
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    // Геттер для workTime
    public String getWorkTime() {
        return workTime;
    }

    // Сеттер для workTime
    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    // Переопределение метода toString
    @Override
    public String toString() {
        return "StaffTimetable{" +
                "staffId=" + id +
                ", idEmployee='" + employeeId + '\'' +
                ", workTime='" + workTime + '\'' +
                '}';
    }
}*/
package Salon;

import java.io.Serializable;
import java.util.Objects;

public class StaffTimetable implements Serializable {
   private static final long serialVersionUID = 1L;

   private int id;
   private int idOrder;
   private String nameService;
   private String lastnameEmployee;
   private String nameEmployee;
   private String time;
   private String date;
   private int lengthOfTime;

   // Конструктор по умолчанию
   public StaffTimetable() {
   }

   // Геттеры и сеттеры
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getIdOrder() {
      return idOrder;
   }

   public void setIdOrder(int idOrder) {
      this.idOrder = idOrder;
   }

   public String getNameService() {
      return nameService;
   }

   public void setNameService(String nameService) {
      this.nameService = nameService;
   }

   public String getLastnameEmployee() {
      return lastnameEmployee;
   }

   public void setLastnameEmployee(String lastnameEmployee) {
      this.lastnameEmployee = lastnameEmployee;
   }

   public String getNameEmployee() {
      return nameEmployee;
   }

   public void setNameEmployee(String nameEmployee) {
      this.nameEmployee = nameEmployee;
   }

   public String getTime() {
      return time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public int getLengthOfTime() {
      return lengthOfTime;
   }

   public void setLengthOfTime(int lengthOfTime) {
      this.lengthOfTime = lengthOfTime;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      StaffTimetable that = (StaffTimetable) o;
      return id == that.id &&
              idOrder == that.idOrder &&
              time == that.time &&
              date == that.date &&
              lengthOfTime == that.lengthOfTime &&
              Objects.equals(nameService, that.nameService) &&
              Objects.equals(lastnameEmployee, that.lastnameEmployee) &&
              Objects.equals(nameEmployee, that.nameEmployee);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, idOrder, nameService, lastnameEmployee,
              nameEmployee, time, date, lengthOfTime);
   }

   @Override
   public String toString() {
      return "StaffTimetable{" +
              "id=" + id +
              ", idOrder=" + idOrder +
              ", nameService='" + nameService + '\'' +
              ", employee='" + lastnameEmployee + " " + nameEmployee + '\'' +
              ", time=" + time +
              ", date=" + date +
              ", lengthOfTime=" + lengthOfTime +
              '}';
   }
}