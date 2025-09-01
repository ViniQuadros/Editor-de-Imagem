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

    public void aumentarBrilho(ImageView imagemOriginal, ImageView imagemAlterada, double percentual) {
        if (imagemOriginal.getImage() == null) return;

        Image imagem = imagemOriginal.getImage();
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader reader = imagem.getPixelReader();
        WritableImage novaImagem = new WritableImage(largura, altura);
        PixelWriter writer = novaImagem.getPixelWriter();

        double fatorBrilho = percentual / 100.0 * 255; // 0-100% → 0-255

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int argb = reader.getArgb(x, y);

                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                r = Math.min(255, Math.max(0, (int)(r + fatorBrilho)));
                g = Math.min(255, Math.max(0, (int)(g + fatorBrilho)));
                b = Math.min(255, Math.max(0, (int)(b + fatorBrilho)));

                int novoArgb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(x, y, novoArgb);
            }
        }

        imagemAlterada.setImage(novaImagem);
    }

    public void aumentarContraste(ImageView imagemOriginal, ImageView imagemAlterada, double percentual) {
        if (imagemOriginal.getImage() == null) return;

        Image imagem = imagemOriginal.getImage();
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader reader = imagem.getPixelReader();
        WritableImage novaImagem = new WritableImage(largura, altura);
        PixelWriter writer = novaImagem.getPixelWriter();

        // percentual de 0-100 → fator de contraste
        double fatorContraste = (percentual / 100.0) * 2; // 0 → 0, 50 → 1, 100 → 2
        fatorContraste = fatorContraste + 1 - 1; // neutro em 50%

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int argb = reader.getArgb(x, y);

                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Ajuste de contraste
                r = (int) Math.min(255, Math.max(0, ((r - 128) * fatorContraste + 128)));
                g = (int) Math.min(255, Math.max(0, ((g - 128) * fatorContraste + 128)));
                b = (int) Math.min(255, Math.max(0, ((b - 128) * fatorContraste + 128)));

                int novoArgb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(x, y, novoArgb);
            }
        }

        imagemAlterada.setImage(novaImagem);
    }


}
