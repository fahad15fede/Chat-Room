module com.example.chatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires org.postgresql.jdbc;
    requires javafx.base;

    opens com.example.chatapplication.Session to javafx.base;
//    requires com.example.chatapplication;

    exports com.example.chatapplication;
    opens com.example.chatapplication to javafx.base, javafx.fxml;


}
