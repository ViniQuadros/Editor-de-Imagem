package com.editor.editor;

import javafx.scene.control.Alert;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

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

        if (!isGreyscale) {
            Image imagem = imagemOriginal.getImage();
            int largura = (int) imagem.getWidth();
            int altura = (int) imagem.getHeight();

            PixelReader pixelReader = imagem.getPixelReader();
            WritableImage novaImagem = new WritableImage(largura, altura);
            PixelWriter pixelWriter = novaImagem.getPixelWriter();

            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    int argb = pixelReader.getArgb(x, y);

                    int a = (argb >> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;

                    int luminosidade = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                    int novoArgb = (a << 24) | (luminosidade << 16) | (luminosidade << 8) | luminosidade;

                    pixelWriter.setArgb(x, y, novoArgb);
                }
            }

            imagemAlterada.setImage(novaImagem);
            isGreyscale = true;

        } else {
            imagemAlterada.setImage(imagemOriginal.getImage());
            isGreyscale = false;
        }
    }

    public void thresholdImage(ImageView imagemOriginal, ImageView imagemAlterada, int threshold) {
        if (imagemOriginal == null || imagemOriginal.getImage() == null) {
            warningHandling();
            return;
        }

        int largura = (int) imagemOriginal.getImage().getWidth();
        int altura = (int) imagemOriginal.getImage().getHeight();

        PixelReader pixelReader = imagemOriginal.getImage().getPixelReader();
        WritableImage novaImagem = new WritableImage(largura, altura);
        PixelWriter pixelWriter = novaImagem.getPixelWriter();

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                int argb = pixelReader.getArgb(x, y);

                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Converte para escala de cinza
                int luminosidade = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                // Aplica o threshold
                int cor = (luminosidade >= threshold) ? 255 : 0;
                int novoArgb = (a << 24) | (cor << 16) | (cor << 8) | cor;

                pixelWriter.setArgb(x, y, novoArgb);
            }
        }

        imagemAlterada.setImage(novaImagem);
    }


    public void ajustarBrilho(ImageView imagemView, Image base, double sliderValue) {
        if (base == null) return;

        double brilhoFator = sliderValue / 100.0;

        int width = (int) base.getWidth();
        int height = (int) base.getHeight();

        WritableImage novaImagem = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color cor = base.getPixelReader().getColor(x, y);

                double r = Math.min(Math.max(cor.getRed() + brilhoFator, 0), 1);
                double g = Math.min(Math.max(cor.getGreen() + brilhoFator, 0), 1);
                double b = Math.min(Math.max(cor.getBlue() + brilhoFator, 0), 1);

                novaImagem.getPixelWriter().setColor(x, y, new Color(r, g, b, cor.getOpacity()));
            }
        }

        imagemView.setImage(novaImagem);
    }



}


