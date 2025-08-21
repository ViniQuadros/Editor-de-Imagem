package com.editor.editor;

import javafx.scene.control.Alert;
import javafx.scene.image.*;

public class Transformacoes {
    private void errorHandling() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso!");
        alert.setHeaderText("Nenhuma imagem para ser editada!");
        alert.showAndWait();
    }

    public void transladarImagem(int deslocamentoX, int deslocamentoY, ImageView imagemOriginal, ImageView imagemAlterada) {
        if (imagemOriginal == null || imagemOriginal.getImage() == null) {
            errorHandling();
            return;
        }

        Image imagem = imagemOriginal.getImage();
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader pixelReader = imagem.getPixelReader();
        WritableImage novaImagem = new WritableImage(largura, altura);
        PixelWriter pixelWriter = novaImagem.getPixelWriter();

        // Copia os pixels com deslocamento
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int novoX = x + deslocamentoX;
                int novoY = y + deslocamentoY;

                // só escreve se o pixel estiver dentro dos limites
                if (novoX >= 0 && novoX < largura && novoY >= 0 && novoY < altura) {
                    pixelWriter.setArgb(novoX, novoY, pixelReader.getArgb(x, y));
                }
            }
        }

        // Atualiza a imagem alterada
        imagemAlterada.setImage(novaImagem);
    }

    public void espelharImagem(ImageView imagemOriginal, ImageView imagemAlterada) {
        // Verifica se o ImageView tem uma imagem válida
        if (imagemOriginal == null || imagemOriginal.getImage() == null) {
            errorHandling();
            return;
        }

        Image imagem = imagemOriginal.getImage();
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader pixelReader = imagem.getPixelReader();
        if (pixelReader == null) return;

        WritableImage novaImagem = new WritableImage(largura, altura);
        PixelWriter pixelWriter = novaImagem.getPixelWriter();

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                // Inverte o eixo X (espelhamento horizontal)
                int espelhoX = largura - 1 - x;
                int cor = pixelReader.getArgb(x, y);
                pixelWriter.setArgb(espelhoX, y, cor);
            }
        }

        imagemAlterada.setImage(novaImagem);
    }
}
