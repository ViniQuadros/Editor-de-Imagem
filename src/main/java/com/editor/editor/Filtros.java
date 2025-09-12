package com.editor.editor;

import javafx.scene.control.Alert;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

public class Filtros extends ModificacoesImagens{
    private boolean isGreyscale = false;

    public boolean getIsGreyscale(){
        return isGreyscale;
    }

    public void greyscaleImagem(ImageView imagemOriginal, ImageView imagemAlterada) {
        if (!isGreyscale) {
            processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
                //Modifica a cor dos pixels da imagem
                for (int y = 0; y < altura; y++) {
                    for (int x = 0; x < largura; x++) {
                        int argb = reader.getArgb(x, y);

                        int a = (argb >> 24) & 0xFF;
                        int r = (argb >> 16) & 0xFF;
                        int g = (argb >> 8) & 0xFF;
                        int b = argb & 0xFF;

                        int luminosidade = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                        int novoArgb = (a << 24) | (luminosidade << 16) | (luminosidade << 8) | luminosidade;

                        writer.setArgb(x, y, novoArgb);
                    }
                }

                isGreyscale = true;
            });
        } else {
            imagemAlterada.setImage(imagemOriginal.getImage());
            isGreyscale = false;
        }
    }

    public void thresholdImage(ImageView imagemOriginal, ImageView imagemAlterada, int threshold) {
        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    int argb = reader.getArgb(x, y);

                    //Definição dos novos valores RGBA da imagem
                    int a = (argb >> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;

                    // Converte para escala de cinza
                    int luminosidade = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                    // Aplica o threshold
                    int cor = (luminosidade >= threshold) ? 255 : 0;
                    int novoArgb = (a << 24) | (cor << 16) | (cor << 8) | cor;

                    writer.setArgb(x, y, novoArgb);
                }
            }
        });
    }

    public void ajustarBrilho(ImageView imagemOriginal, ImageView imagemAlterada, double sliderValue) {
        // Converte o sliderValue em um fator de brilho entre -1 e 1
        double brilhoFator = sliderValue / 100.0;

        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    Color cor = reader.getColor(x, y);

                    double r = Math.min(Math.max(cor.getRed() + brilhoFator, 0), 1);
                    double g = Math.min(Math.max(cor.getGreen() + brilhoFator, 0), 1);
                    double b = Math.min(Math.max(cor.getBlue() + brilhoFator, 0), 1);

                    writer.setColor(x, y, new Color(r, g, b, cor.getOpacity()));
                }
            }
        });
    }

    public void contrasteImagem(ImageView imagemOriginal, ImageView imagemAlterada, double C) {
        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    Color cor = reader.getColor(x, y);

                    double r = (cor.getRed() - 0.5) * C + 0.5;
                    double g = (cor.getGreen() - 0.5) * C + 0.5;
                    double b = (cor.getBlue() - 0.5) * C + 0.5;

                    // Limita os valores
                    r = Math.min(1.0, Math.max(0.0, r));
                    g = Math.min(1.0, Math.max(0.0, g));
                    b = Math.min(1.0, Math.max(0.0, b));

                    //Aplica o contraste
                    writer.setColor(x, y, new Color(r, g, b, cor.getOpacity()));
                }
            }
        });
    }
}