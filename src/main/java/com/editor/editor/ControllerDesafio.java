package com.editor.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.stage.FileChooser;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import java.io.File;

public class ControllerDesafio {
    private Filtros filtros = new Filtros();
    private static final int TAMANHO_MINIMO_PONTO = 20;

    @FXML
    private Label respostaDesafio;
    @FXML
    private ImageView imagemDesafio;

    @FXML
    void openImage(ActionEvent event) {
        // Cria o seletor de arquivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Imagem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(((MenuItem) event.getSource()).getParentPopup().getOwnerWindow());

        if (file == null) {
            return;
        }

        Image imageOriginal = new Image(file.toURI().toString());
        imagemDesafio.setImage(imageOriginal);

        ImageView imgOriginal = new ImageView(imageOriginal);
        ImageView imgCinza = new ImageView();
        ImageView imgGauss = new ImageView();
        ImageView imgSobel = new ImageView();
        ImageView imgThreshold = new ImageView();

        filtros.greyscaleImagem(imgOriginal, imgCinza);
        filtros.PBGaussiano(imgCinza, imgGauss);
        filtros.PASobel(imgGauss, imgSobel);
        int linhaY = encontrarBarra(imgSobel.getImage());

        if (linhaY == -1) {
            respostaDesafio.setText("Erro: Não foi possível encontrar a barra.");
            return;
        }

        filtros.thresholdImage(imgGauss, imgThreshold, 200); // Ajuste o 150 se necessário

        int[] contagem = contarPontos(imgThreshold.getImage(), linhaY);

        respostaDesafio.setText("Superior: " + (contagem[0]-2) + " | Inferior: " + contagem[1]);

        imagemDesafio.setImage(imgThreshold.getImage());
    }

    public int encontrarBarra(Image imagem) {
        PixelReader reader = imagem.getPixelReader();
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        int[] projecaoHorizontal = new int[altura];

        for (int y = 0; y < altura; y++) {
            int pixelsNestaLinha = 0;
            for (int x = 0; x < largura; x++) {

                if (reader.getColor(x, y).getBrightness() > 0.1) {
                    pixelsNestaLinha++;
                }
            }
            projecaoHorizontal[y] = pixelsNestaLinha;
        }

        int linhaDivisoriaY = -1;
        int maxPixelsNaLinha = 0;
        int margem = (int) (altura * 0.10);

        for (int y = margem; y < altura - margem; y++) {
            if (projecaoHorizontal[y] > maxPixelsNaLinha) {
                maxPixelsNaLinha = projecaoHorizontal[y];
                linhaDivisoriaY = y;
            }
        }
        return linhaDivisoriaY;
    }

    public int[] contarPontos(Image imagemBinaria, int linhaDivisoriaY) {
        PixelReader reader = imagemBinaria.getPixelReader();
        int largura = (int) imagemBinaria.getWidth();
        int altura = (int) imagemBinaria.getHeight();

        boolean[][] visitado = new boolean[largura][altura];

        int pontosSuperiores = 0;
        int pontosInferiores = 0;

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {

                if (visitado[x][y]) {
                    continue;
                }

                // .getRed() == 1.0 significa "é branco?"
                boolean eUmPonto = (reader.getColor(x, y).getRed() <= 1.0 && reader.getColor(x, y).getRed() >= 0.0);

                if (eUmPonto) {
                    int tamanhoDaBolha = explorarBolha(reader, visitado, x, y, largura, altura);

                    if (tamanhoDaBolha > TAMANHO_MINIMO_PONTO) {
                        if (y < linhaDivisoriaY) {
                            pontosSuperiores++;
                        } else {
                            pontosInferiores++;
                        }
                    }
                } else {
                    visitado[x][y] = true;
                }
            }
        }
        return new int[]{pontosSuperiores, pontosInferiores};
    }

    private int explorarBolha(PixelReader reader, boolean[][] visitado, int startX, int startY, int largura, int altura) {
        Queue<Point> fila = new LinkedList<>();
        fila.add(new Point(startX, startY));
        visitado[startX][startY] = true;

        int tamanhoDaBolha = 0;
        int[] dx = {0, 0, 1, -1, 1, -1, 1, -1}; // 8 direções
        int[] dy = {1, -1, 0, 0, 1, 1, -1, -1};

        while (!fila.isEmpty()) {
            Point p = fila.poll();
            tamanhoDaBolha++;

            for (int i = 0; i < 8; i++) {
                int vizinhoX = p.x + dx[i];
                int vizinhoY = p.y + dy[i];

                if (vizinhoX < 0 || vizinhoX >= largura || vizinhoY < 0 || vizinhoY >= altura) {
                    continue;
                }
                if (visitado[vizinhoX][vizinhoY]) {
                    continue;
                }

                if (reader.getColor(vizinhoX, vizinhoY).getRed() == 0.0) {
                    visitado[vizinhoX][vizinhoY] = true;
                    fila.add(new Point(vizinhoX, vizinhoY));
                } else {
                    visitado[vizinhoX][vizinhoY] = true;
                }
            }
        }
        return tamanhoDaBolha;
    }
}