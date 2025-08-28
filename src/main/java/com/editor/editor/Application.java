package com.editor.editor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("EditorDeImagens.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Visualise");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/vizualize.png")));
        stage.setScene(scene);
        stage.show();
    }
}
