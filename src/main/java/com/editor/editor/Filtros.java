package com.editor.editor;

import javafx.scene.control.Alert;
import javafx.scene.image.*;

public class Filtros {
    private boolean isGreyscale = false;

    private void warningHandling() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso!");
        alert.setHeaderText("Nenhuma imagem para ser editada!");
        alert.showAndWait();
    }

    public void greyscaleImagem(ImageView imagemOriginal, ImageView imagemAlterada) {
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

        // Itera sobre todos os pixels da imagem
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                // Lê o valor ARGB do pixel original
                int argb = pixelReader.getArgb(x, y);

                // Extrai os componentes de cor
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Calcula a luminosidade (média ponderada) para o novo pixel cinza
                int luminosidade = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                // Cria o novo valor ARGB com os canais R, G e B iguais à luminosidade
                int novoArgb = (a << 24) | (luminosidade << 16) | (luminosidade << 8) | luminosidade;

                // Escreve o novo pixel na imagem alterada
                pixelWriter.setArgb(x, y, novoArgb);
            }
        }

        // Atualiza a ImageView com a nova imagem em escala de cinza
        imagemAlterada.setImage(novaImagem);
    }
}
