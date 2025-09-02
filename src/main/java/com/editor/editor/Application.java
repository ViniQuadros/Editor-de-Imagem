package com.editor.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;


import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("EditorDeImagens.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Controller controller = fxmlLoader.getController();
        scene.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                controller.desfazer(new ActionEvent());
            } else if (event.isControlDown() && event.getCode() == KeyCode.L) {
                controller.carregarImagemDeLink("https://i.pinimg.com/736x/c1/59/0d/c1590d1e874e60a2c53365d63585c1eb.jpg");
            }
        });
        stage.setTitle("Visualise");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/vizualize.png")));
        stage.setScene(scene);
        stage.show();

    }
}
