package com.danchuo.jigsawclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JigsawApplication extends Application {
    public static final int WIDTH = 1200;
    public static final int HEIGHT = 700;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(JigsawApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Playing");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String... args) {
        launch();
    }
}