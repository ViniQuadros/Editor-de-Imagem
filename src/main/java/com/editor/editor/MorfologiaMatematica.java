package com.editor.editor;

import javafx.scene.image.*;

public class MorfologiaMatematica extends ModificacoesImagens{
    private boolean isDilatacao;
    private boolean isErosao;
    private boolean isAbertura;
    private boolean isFechamento;

    public void operacaoMorfologica(ImageView imagemOriginal, ImageView imagemAlterada, String operacao) {
        // 1. Definição do Elemento Estruturante (EE) (É igual para ambas as operações)
        final int[][] elementoEstruturante = {
                {0, 10, 0},
                {10, 10, 10},
                {0, 10, 0}
        };
        final int tamanhoEE = 3;
        final int offset = tamanhoEE / 2;

        // 2. Configuração da Lógica e dos Valores Iniciais (Muda conforme a operação)
        boolean isDilatacao = operacao.equalsIgnoreCase("DILATACAO");

        final int VALOR_INICIAL = isDilatacao ? 0 : 255;

        // 3. Execução do Processamento da Imagem
        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {

            // Varre a imagem, ignorando a borda de 1 pixel
            for (int y = offset; y < altura - offset; y++) {
                for (int x = offset; x < largura - offset; x++) {

                    // Inicializa os valores R, G, B com o valor inicial (0 ou 255)
                    int resultadoR = VALOR_INICIAL;
                    int resultadoG = VALOR_INICIAL;
                    int resultadoB = VALOR_INICIAL;

                    // Itera sobre o Elemento Estruturante (vizinhança 3x3)
                    for (int j = -offset; j <= offset; j++) {
                        for (int i = -offset; i <= offset; i++) {
                            int argbVizinho = reader.getArgb(x + i, y + j);
                            int pesoEE = elementoEstruturante[j + offset][i + offset];

                            int rVizinho = (argbVizinho >> 16) & 0xFF;
                            int gVizinho = (argbVizinho >> 8) & 0xFF;
                            int bVizinho = argbVizinho & 0xFF;

                            // 4. Lógica de Morfologia (Ponto onde as operações divergem)
                            if (isDilatacao) {
                                // Dilatação: SOMA + MAX
                                int valorR = rVizinho + pesoEE;
                                int valorG = gVizinho + pesoEE;
                                int valorB = bVizinho + pesoEE;

                                resultadoR = Math.max(resultadoR, valorR);
                                resultadoG = Math.max(resultadoG, valorG);
                                resultadoB = Math.max(resultadoB, valorB);
                            } else {
                                // Erosão: SUBTRAÇÃO + MIN
                                int valorR = rVizinho - pesoEE;
                                int valorG = gVizinho - pesoEE;
                                int valorB = bVizinho - pesoEE;

                                resultadoR = Math.min(resultadoR, valorR);
                                resultadoG = Math.min(resultadoG, valorG);
                                resultadoB = Math.min(resultadoB, valorB);
                            }
                        }
                    }

                    // 5. Clipe (Ponto onde a lógica de saturação difere)
                    int novoR, novoG, novoB;

                    if (isDilatacao) {
                        // Limita o valor superior (satura em 255)
                        novoR = Math.min(255, resultadoR);
                        novoG = Math.min(255, resultadoG);
                        novoB = Math.min(255, resultadoB);
                    } else {
                        // Limita o valor inferior (satura em 0)
                        novoR = Math.max(0, resultadoR);
                        novoG = Math.max(0, resultadoG);
                        novoB = Math.max(0, resultadoB);
                    }

                    // Monta o novo pixel
                    int a = (reader.getArgb(x, y) >> 24) & 0xFF;
                    int novoArgb = (a << 24) | (novoR << 16) | (novoG << 8) | novoB;
                    writer.setArgb(x, y, novoArgb);
                }
            }
        });
    }

    // Adicione este método PRIVATE na classe Filtros
// Ele contém a mesma lógica de processamento do seu método público, mas recebe e retorna a Imagem,
// permitindo o encadeamento para Abertura e Fechamento.
    private WritableImage operacaoMorfologica(Image inputImage, String operacao) {
        if (inputImage == null) return null;

        final int largura = (int) inputImage.getWidth();
        final int altura = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(largura, altura);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        final int[][] elementoEstruturante = {
                {0, 10, 0},
                {10, 10, 10},
                {0, 10, 0}
        };
        final int offset = 1;

        boolean isDilatacao = operacao.equalsIgnoreCase("DILATACAO");
        final int VALOR_INICIAL = isDilatacao ? 0 : 255;

        for (int y = offset; y < altura - offset; y++) {
            for (int x = offset; x < largura - offset; x++) {

                int resultadoR = VALOR_INICIAL;
                int resultadoG = VALOR_INICIAL;
                int resultadoB = VALOR_INICIAL;

                for (int j = -offset; j <= offset; j++) {
                    for (int i = -offset; i <= offset; i++) {

                        int argbVizinho = reader.getArgb(x + i, y + j);
                        int pesoEE = elementoEstruturante[j + offset][i + offset];

                        int rVizinho = (argbVizinho >> 16) & 0xFF;
                        int gVizinho = (argbVizinho >> 8) & 0xFF;
                        int bVizinho = argbVizinho & 0xFF;

                        if (isDilatacao) {
                            int valorR = rVizinho + pesoEE;
                            int valorG = gVizinho + pesoEE;
                            int valorB = bVizinho + pesoEE;

                            resultadoR = Math.max(resultadoR, valorR);
                            resultadoG = Math.max(resultadoG, valorG);
                            resultadoB = Math.max(resultadoB, valorB);

                        } else {
                            int valorR = rVizinho - pesoEE;
                            int valorG = gVizinho - pesoEE;
                            int valorB = bVizinho - pesoEE;

                            resultadoR = Math.min(resultadoR, valorR);
                            resultadoG = Math.min(resultadoG, valorG);
                            resultadoB = Math.min(resultadoB, valorB);
                        }
                    }
                }

                int novoR, novoG, novoB;

                if (isDilatacao) {
                    novoR = Math.min(255, resultadoR);
                    novoG = Math.min(255, resultadoG);
                    novoB = Math.min(255, resultadoB);
                } else {
                    novoR = Math.max(0, resultadoR);
                    novoG = Math.max(0, resultadoG);
                    novoB = Math.max(0, resultadoB);
                }

                int a = (reader.getArgb(x, y) >> 24) & 0xFF;
                int novoArgb = (a << 24) | (novoR << 16) | (novoG << 8) | novoB;
                writer.setArgb(x, y, novoArgb);
            }
        }
        return outputImage;
    }

    public void abertura(ImageView imagemOriginal, ImageView imagemAlterada){
        Image original = imagemOriginal.getImage();
        if (original == null) return;

        // Abertura = Erosão seguida de Dilatação
        WritableImage imagemErodida = operacaoMorfologica(original, "EROSAO");
        WritableImage imagemAberta = operacaoMorfologica(imagemErodida, "DILATACAO");

        imagemAlterada.setImage(imagemAberta);
    }

    public void fechamento(ImageView imagemOriginal, ImageView imagemAlterada){
        Image original = imagemOriginal.getImage();
        if (original == null) return;

        // Fechamento = Dilatação seguida de Erosão
        WritableImage imagemDilatada = operacaoMorfologica(original, "DILATACAO");
        WritableImage imagemFechada = operacaoMorfologica(imagemDilatada, "EROSAO");

        imagemAlterada.setImage(imagemFechada);
    }

    public boolean isDilatacao() {
        return this.isDilatacao;
    }

    public void setDilatacao() {
        isDilatacao = true;
        isErosao = false;
        isAbertura = false;
        isFechamento = false;
    }

    public boolean isErosao() {
        return this.isErosao;
    }

    public void setErosao() {
        isDilatacao = false;
        isErosao = true;
        isAbertura = false;
        isFechamento = false;    }

    public boolean isAbertura() {
        return this.isAbertura;
    }

    public void setAbertura() {
        isDilatacao = false;
        isErosao = false;
        isAbertura = true;
        isFechamento = false;
    }

    public boolean isFechamento() {
        return this.isFechamento;
    }

    public void setFechamento() {
        isDilatacao = false;
        isErosao = false;
        isAbertura = false;
        isFechamento = true;
    }
}
