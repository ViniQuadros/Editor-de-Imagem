package com.editor.editor;

import javafx.scene.control.Alert;
import javafx.scene.image.*;

public class ModificacoesImagens {
    protected void warningHandling() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso!");
        alert.setHeaderText("Nenhuma imagem para ser editada!");
        alert.showAndWait();
    }

    @FunctionalInterface
    protected interface PixelTransform {
        void apply(PixelReader reader, PixelWriter writer, int largura, int altura);
    }

    protected void processarImagem(ImageView imagemOriginal, ImageView imagemAlterada, PixelTransform transform) {
        if (imagemOriginal == null || imagemOriginal.getImage() == null) {
            warningHandling();
            return;
        }

        Image imagem = imagemOriginal.getImage();
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader pixelReader = imagem.getPixelReader();
        WritableImage novaImagem = new WritableImage(largura, altura);
        PixelWriter pixelWriter = novaImagem.getPixelWriter();

        transform.apply(pixelReader, pixelWriter, largura, altura);

        //Atualiza a imagem de output
        imagemAlterada.setImage(novaImagem);
    }
}
