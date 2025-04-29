module com.github.lillanudde.mokkihelvetti 
{
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;


    opens com.github.lillanudde.mokkihelvetti to javafx.fxml;
    exports com.github.lillanudde.mokkihelvetti;
}