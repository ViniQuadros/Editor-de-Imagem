package com.editor.editor;

import java.util.List;
import java.util.ArrayList;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.util.Arrays;


public class Filtros extends ModificacoesImagens{
    private boolean isGreyscale = false;
    private boolean isMediana = false;
    private boolean isGaussiano = false;
    private boolean isSolbel = false;
    private boolean isRoberts = false;
    private boolean isAfinamento = false;

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
        PBGaussiano(imagemAlterada, imagemAlterada);
        processarImagem(imagemAlterada, imagemAlterada, (reader, writer, largura, altura) -> {
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
        PBGaussiano(imagemAlterada, imagemAlterada);

        processarImagem(imagemAlterada, imagemAlterada, (reader, writer, largura, altura) -> {
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

    public void PBMediana(ImageView imagemOriginal, ImageView imagemAlterada) {
        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
            int tamanhoMascara = 3;
            int offset = tamanhoMascara / 2; // Offset de 1 para máscara 3x3

            // Arrays para armazenar os valores de R, G e B da vizinhança
            int[] rVizinhanca = new int[tamanhoMascara * tamanhoMascara];
            int[] gVizinhanca = new int[tamanhoMascara * tamanhoMascara];
            int[] bVizinhanca = new int[tamanhoMascara * tamanhoMascara];

            // Varre a imagem, ignorando a borda de 1 pixel (onde a máscara 3x3 não pode ser centrada)
            for (int y = offset; y < altura - offset; y++) {
                for (int x = offset; x < largura - offset; x++) {
                    int k = 0; // Índice do array da vizinhança

                    // Coleta os valores R, G e B da vizinhança 3x3
                    for (int j = -offset; j <= offset; j++) {
                        for (int i = -offset; i <= offset; i++) {
                            int argbVizinho = reader.getArgb(x + i, y + j);

                            rVizinhanca[k] = (argbVizinho >> 16) & 0xFF;
                            gVizinhanca[k] = (argbVizinho >> 8) & 0xFF;
                            bVizinhanca[k] = argbVizinho & 0xFF;
                            k++;
                        }
                    }

                    // 1. ORDENAÇÃO: Ordena os arrays de R, G e B
                    // Este é o passo mais custoso e fundamental do filtro da Mediana.
                    Arrays.sort(rVizinhanca);
                    Arrays.sort(gVizinhanca);
                    Arrays.sort(bVizinhanca);

                    // 2. OBTENÇÃO DA MEDIANA: A mediana é o valor central do array ordenado
                    // O índice central para um array de 9 elementos (3x3) é 4 (índices de 0 a 8)
                    int medianaR = rVizinhanca[4];
                    int medianaG = gVizinhanca[4];
                    int medianaB = bVizinhanca[4];

                    // Mantém o canal Alfa (opacidade) do pixel original
                    int argbOriginal = reader.getArgb(x, y);
                    int a = (argbOriginal >> 24) & 0xFF;

                    // Cria o novo valor ARGB com as medianas dos canais
                    int novoArgb = (a << 24) | (medianaR << 16) | (medianaG << 8) | medianaB;

                    // Aplica o novo pixel
                    writer.setArgb(x, y, novoArgb);
                }
            }
        });
    }

    public void PBGaussiano(ImageView imagemOriginal, ImageView imagemAlterada) {
        // Máscara Gaussiana 3x3 e o divisor (soma dos pesos = 16)
        final int[][] mascara = {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
        final int divisor = 16;
        final int tamanhoMascara = 3;
        final int offset = tamanhoMascara / 2; // Offset de 1 para máscara 3x3

        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {
            // Varre a imagem, ignorando a borda de 1 pixel (onde a máscara 3x3 não pode ser centrada)
            for (int y = offset; y < altura - offset; y++) {
                for (int x = offset; x < largura - offset; x++) {

                    // Acumuladores para as somas ponderadas de cada canal de cor
                    long somaR = 0;
                    long somaG = 0;
                    long somaB = 0;

                    // Itera sobre a máscara 3x3
                    for (int j = -offset; j <= offset; j++) {
                        for (int i = -offset; i <= offset; i++) {

                            // Obtém o valor ARGB do vizinho
                            int argbVizinho = reader.getArgb(x + i, y + j);

                            // Extrai os componentes de cor do vizinho
                            int r = (argbVizinho >> 16) & 0xFF;
                            int g = (argbVizinho >> 8) & 0xFF;
                            int b = argbVizinho & 0xFF;

                            // Determina o peso da máscara para esta posição
                            int peso = mascara[j + offset][i + offset];

                            somaR += r * peso;
                            somaG += g * peso;
                            somaB += b * peso;
                        }
                    }

                    int novoR = (int) Math.min(255, somaR / divisor);
                    int novoG = (int) Math.min(255, somaG / divisor);
                    int novoB = (int) Math.min(255, somaB / divisor);

                    // Mantém o canal Alfa (opacidade) do pixel original
                    int argbOriginal = reader.getArgb(x, y);
                    int a = (argbOriginal >> 24) & 0xFF;

                    // Cria o novo valor ARGB
                    int novoArgb = (a << 24) | (novoR << 16) | (novoG << 8) | novoB;

                    // Aplica o novo pixel
                    writer.setArgb(x, y, novoArgb);
                }
            }
        });
    }

    public void afinamentoZhangSuen(ImageView imagemOriginal, ImageView imagemAlterada) {

        greyscaleImagem(imagemOriginal, imagemAlterada);

        binarizarImagem(imagemAlterada, imagemAlterada, 0.5);

        aplicarZhangSuen(imagemAlterada, imagemAlterada);
    }


    private void aplicarZhangSuen(ImageView src, ImageView dst) {
        processarImagem(src, dst, (reader, writer, largura, altura) -> {
            int[][] img = new int[altura][largura];
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    Color c = reader.getColor(x, y);
                    img[y][x] = (c.equals(Color.BLACK)) ? 1 : 0;
                }
            }

            boolean mudou;
            do {
                mudou = false;
                List<int[]> paraRemover = new ArrayList<>();

                for (int y = 1; y < altura - 1; y++) {
                    for (int x = 1; x < largura - 1; x++) {
                        if (img[y][x] != 1) continue;

                        int[] p = getVizinhos(img, x, y); // P2..P9
                        int B = p[0] + p[1] + p[2] + p[3] + p[4] + p[5] + p[6] + p[7];
                        int A = transicoes(p);

                        // Condições da etapa 1 (Zhang-Suen)
                        if (B >= 2 && B <= 6 &&
                                A == 1 &&
                                (p[0] * p[2] * p[4] == 0) &&   // P2 * P4 * P6 == 0
                                (p[2] * p[4] * p[6] == 0)) {  // P4 * P6 * P8 == 0
                            paraRemover.add(new int[]{x, y});
                        }
                    }
                }

                if (!paraRemover.isEmpty()) mudou = true;
                for (int[] r : paraRemover) img[r[1]][r[0]] = 0;
                paraRemover.clear();


                for (int y = 1; y < altura - 1; y++) {
                    for (int x = 1; x < largura - 1; x++) {
                        if (img[y][x] != 1) continue;

                        int[] p = getVizinhos(img, x, y);
                        int B = p[0] + p[1] + p[2] + p[3] + p[4] + p[5] + p[6] + p[7];
                        int A = transicoes(p);

                        if (B >= 2 && B <= 6 &&
                                A == 1 &&
                                (p[0] * p[2] * p[6] == 0) &&   // P2 * P4 * P8 == 0
                                (p[0] * p[4] * p[6] == 0)) {  // P2 * P6 * P8 == 0
                            paraRemover.add(new int[]{x, y});
                        }
                    }
                }

                if (!paraRemover.isEmpty()) mudou = true;
                for (int[] r : paraRemover) img[r[1]][r[0]] = 0;

            } while (mudou);

            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    writer.setColor(x, y, img[y][x] == 1 ? Color.BLACK : Color.WHITE);
                }
            }
        });
    }



    private int[] getVizinhos(int[][] img, int x, int y) {
        return new int[]{
                img[y - 1][x],     // P2 N
                img[y - 1][x + 1], // P3 NE
                img[y][x + 1],     // P4 E
                img[y + 1][x + 1], // P5 SE
                img[y + 1][x],     // P6 S
                img[y + 1][x - 1], // P7 SW
                img[y][x - 1],     // P8 W
                img[y - 1][x - 1]  // P9 NW
        };
    }

    private int transicoes(int[] p) {
        int t = 0;
        for (int i = 0; i < p.length; i++) {
            int atual = p[i];
            int prox = p[(i + 1) % p.length];
            if (atual == 0 && prox == 1) t++;
        }
        return t;
    }

    public void binarizarImagem(ImageView imagemOriginal, ImageView imagemBinarizada, double limiar) {
        processarImagem(imagemOriginal, imagemBinarizada, (reader, writer, largura, altura) -> {
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    Color c = reader.getColor(x, y);
                    double intensidade = c.getRed();
                    if (intensidade < limiar) {
                        writer.setColor(x, y, Color.BLACK); // objeto
                    } else {
                        writer.setColor(x, y, Color.WHITE); // fundo
                    }                }
            }
        });
    }
    public boolean isGreyscale() {
        return this.isGreyscale;
    }

    public void setGreyscale() {
        this.isGreyscale = true;
        this.isMediana = false;
        this.isGaussiano = false;
        this.isSolbel = false;
        this.isRoberts = false;
    }

    public boolean isMediana() {
        return this.isMediana;
    }

    public void setMediana() {
        this.isGreyscale = false;
        this.isMediana = true;
        this.isGaussiano = false;
        this.isSolbel = false;
        this.isRoberts = false;
    }

    public boolean isGaussiano() {
        return this.isGaussiano;
    }

    public void setGaussiano() {
        this.isGreyscale = false;
        this.isMediana = false;
        this.isGaussiano = true;
        this.isSolbel = false;
        this.isRoberts = false;
    }

    public boolean isSolbel() {
        return this.isSolbel;
    }

    public void setSolbel() {
        this.isGreyscale = false;
        this.isMediana = false;
        this.isGaussiano = false;
        this.isSolbel = true;
        this.isRoberts = false;
    }

    public boolean isRoberts() {
        return this.isRoberts;
    }

    public void setRoberts() {
        this.isGreyscale = false;
        this.isMediana = false;
        this.isGaussiano = false;
        this.isSolbel = false;
        this.isRoberts = true;
    }

    public void afinamento(ImageView imagemOriginal, ImageView imagemAlterada, double threshold) {
        processarImagem(imagemOriginal, imagemAlterada, (reader, writer, largura, altura) -> {

            boolean[][] bin = new boolean[altura][largura];

            // 1️⃣ Binariza a imagem (foreground = preto)
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    Color c = reader.getColor(x, y);
                    double luminancia = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
                    bin[y][x] = luminancia <= threshold;
                }
            }

            // 2️⃣ Gera matriz de contorno (true = pixel de contorno)
            boolean[][] contorno = new boolean[altura][largura];

            for (int y = 1; y < altura - 1; y++) {
                for (int x = 1; x < largura - 1; x++) {
                    if (!bin[y][x]) continue; // fundo

                    // Verifica se o pixel tem pelo menos um vizinho de fundo
                    boolean borda = false;
                    for (int j = -1; j <= 1 && !borda; j++) {
                        for (int i = -1; i <= 1; i++) {
                            if (!bin[y + j][x + i]) {
                                borda = true;
                                break;
                            }
                        }
                    }

                    contorno[y][x] = borda;
                }
            }

            // 3️⃣ Opcional: refina o contorno removendo pixels duplos (para garantir 1px)
            boolean[][] refinado = refinarContorno(contorno, largura, altura);

            // 4️⃣ Escreve o resultado
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    writer.setColor(x, y, refinado[y][x] ? Color.BLACK : Color.WHITE);
                }
            }
        });
    }

    // 🔧 Remove duplicações e mantém apenas pixels essenciais do contorno
    private boolean[][] refinarContorno(boolean[][] contorno, int largura, int altura) {
        boolean[][] resultado = new boolean[altura][largura];
        for (int y = 1; y < altura - 1; y++) {
            for (int x = 1; x < largura - 1; x++) {
                if (!contorno[y][x]) continue;

                int vizinhos = 0;
                for (int j = -1; j <= 1; j++) {
                    for (int i = -1; i <= 1; i++) {
                        if (contorno[y + j][x + i]) vizinhos++;
                    }
                }

                // Mantém o pixel se ele não for totalmente cercado (evita borda grossa)
                resultado[y][x] = vizinhos <= 3;
            }
        }
        return resultado;
    }

    public boolean isAfinamento() {
        return this.isAfinamento;
    }

    public void setAfinamento() {
        this.isGreyscale = false;
        this.isMediana = false;
        this.isGaussiano = false;
        this.isSolbel = false;
        this.isRoberts = false;
        this.isAfinamento = true;
    }
}