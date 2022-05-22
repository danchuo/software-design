module com.danchuo.jigsawclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.danchuo.jigsawclient to javafx.fxml;
    exports com.danchuo.jigsawclient;
}