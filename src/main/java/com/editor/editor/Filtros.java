package com.editor.editor;

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

    public void PARoberts(ImageView imagemOriginal, ImageView imagemAlterada){
        int[][] GX = {
                {1, 0}, {0, -1}
        };
        int[][] GY = {
                {0, 1}, {-1, 0}
        };
        greyscaleImagem(imagemOriginal, imagemAlterada);

        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
            // Converte a imagem para grayscale antes do for

            for (int y = 0; y < altura - 1; y++) {
                for (int x = 0; x < largura - 1; x++) {
                    Color c1 = reader.getColor(x, y);
                    Color c2 = reader.getColor(x + 1, y);
                    Color c3 = reader.getColor(x, y + 1);
                    Color c4 = reader.getColor(x + 1, y + 1);

                    double p1 = c1.getRed();
                    double p2 = c2.getRed();
                    double p3 = c3.getRed();
                    double p4 = c4.getRed();

                    double gx = p1 * GX[0][0] + p2 * GX[0][1] + p3 * GX[1][0] + p4 * GX[1][1];
                    double gy = p1 * GY[0][0] + p2 * GY[0][1] + p3 * GY[1][0] + p4 * GY[1][1];

                    // Magnitude do gradiente
                    double g = Math.sqrt(gx * gx + gy * gy);

                    // Normaliza para 0..1
                    g = Math.min(1.0, g);

                    writer.setColor(x, y, new Color(g, g, g, 1.0));
                }
            }
        });

    }

    public void PASobel(ImageView imagemOriginal, ImageView imagemAlterada){
        int[][] GX = {
                {-1, 0, 1}, {-2, 0, 2},{-1, 0, 1}
        };
        int[][] GY = {
                {-1, -2, -1}, {0, 0, 0}, {1, 2, 1}
        };

        greyscaleImagem(imagemOriginal, imagemAlterada);

        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
            // percorre sem pegar bordas, por isso o -1
            for (int y = 1; y < altura - 1; y++) {
                for (int x = 1; x < largura - 1; x++) {

                    // pega vizinhança 3x3 em torno
                    Color c1 = reader.getColor(x - 1,y - 1);
                    Color c2 = reader.getColor(x,y - 1);
                    Color c3 = reader.getColor(x + 1, y - 1);

                    Color c4 = reader.getColor(x - 1, y);
                    Color c5 = reader.getColor(x, y);
                    Color c6 = reader.getColor(x + 1, y);

                    Color c7 = reader.getColor(x - 1, y + 1);
                    Color c8 = reader.getColor(x, y + 1);
                    Color c9 = reader.getColor(x + 1, y + 1);

                    double p1 = c1.getRed();
                    double p2 = c2.getRed();
                    double p3 = c3.getRed();
                    double p4 = c4.getRed();
                    double p5 = c5.getRed();
                    double p6 = c6.getRed();
                    double p7 = c7.getRed();
                    double p8 = c8.getRed();
                    double p9 = c9.getRed();

                    // aplicando máscaras
                    double gx = p1 * GX[0][0] + p2 * GX[0][1] + p3 * GX[0][2]
                            + p4 * GX[1][0] + p5 * GX[1][1] + p6 * GX[1][2]
                            + p7 * GX[2][0] + p8 * GX[2][1] + p9 * GX[2][2];

                    double gy = p1 * GY[0][0] + p2 * GY[0][1] + p3 * GY[0][2]
                            + p4 * GY[1][0] + p5 * GY[1][1] + p6 * GY[1][2]
                            + p7 * GY[2][0] + p8 * GY[2][1] + p9 * GY[2][2];


                    double g = Math.sqrt(gx * gx + gy * gy);

                    if (g > 1.0) g = 1.0;

                    writer.setColor(x, y, new Color(g, g, g, 1.0));
                }
            }
        });

    }

}