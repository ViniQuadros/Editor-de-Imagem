package com.editor.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    public void rotacionarImagem(int angulo, ImageView imagemOriginal, ImageView imagemAlterada){
        if (imagemOriginal == null || imagemOriginal.getImage() == null) {
            errorHandling();
            return;
        }

        Image imagem = imagemOriginal.getImage();
        int largura = (int) imagem.getWidth();
        int altura = (int) imagem.getHeight();

        PixelReader pixelReader = imagem.getPixelReader();

        // nova imagem (mesmo tamanho por simplicidade)
        WritableImage novaImagem = new WritableImage(largura, altura);
        PixelWriter pixelWriter = novaImagem.getPixelWriter();

        // centro da imagem
        double cx = largura / 2.0;
        double cy = altura / 2.0;

        // ângulo em radianos
        double rad = Math.toRadians(angulo);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        // percorre os pixels da nova imagem
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                // aplica a rotação inversa para pegar o pixel da imagem original
                double xOriginal = cos * (x - cx) + sin * (y - cy) + cx;
                double yOriginal = -sin * (x - cx) + cos * (y - cy) + cy;

                int ix = (int) Math.round(xOriginal);
                int iy = (int) Math.round(yOriginal);

                // verifica se está dentro dos limites
                if (ix >= 0 && ix < largura && iy >= 0 && iy < altura) {
                    pixelWriter.setArgb(x, y, pixelReader.getArgb(ix, iy));
                } else {
                    // se não tiver pixel correspondente, deixa transparente
                    pixelWriter.setArgb(x, y, 0x00000000);
                }
            }
        }

        // Atualiza a imagem alterada
        imagemAlterada.setImage(novaImagem);
    }
    public void espelharHorizontal(ImageView imagemOriginal, ImageView imagemAlterada) {
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

        double cx = largura / 2.0;
        double cy = altura / 2.0;

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                // aplica espelhamento horizontal (inverte X em relação ao centro)
                double xOriginal = -1 * (x - cx) + cx;
                double yOriginal = (y - cy) + cy;

                int ix = (int) Math.round(xOriginal);
                int iy = (int) Math.round(yOriginal);

                if (ix >= 0 && ix < largura && iy >= 0 && iy < altura) {
                    pixelWriter.setArgb(x, y, pixelReader.getArgb(ix, iy));
                } else {
                    pixelWriter.setArgb(x, y, 0x00000000);
                }
            }
        }

        imagemAlterada.setImage(novaImagem);
    }

    public void espelharVertical(ImageView imagemOriginal, ImageView imagemAlterada) {
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

        double cx = largura / 2.0;
        double cy = altura / 2.0;

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                // aplica espelhamento vertical (inverte Y em relação ao centro)
                double xOriginal = (x - cx) + cx;
                double yOriginal = -1 * (y - cy) + cy;

                int ix = (int) Math.round(xOriginal);
                int iy = (int) Math.round(yOriginal);

                if (ix >= 0 && ix < largura && iy >= 0 && iy < altura) {
                    pixelWriter.setArgb(x, y, pixelReader.getArgb(ix, iy));
                } else {
                    pixelWriter.setArgb(x, y, 0x00000000);
                }
            }
        }

        imagemAlterada.setImage(novaImagem);
    }
}
