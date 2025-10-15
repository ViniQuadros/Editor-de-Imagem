package com.editor.editor;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Objects;
import java.util.Stack;

public class Controller {
    private boolean isLightTheme = false;

    // Classes de alteração da imagem
    private final Transformacoes transformacoes = new Transformacoes();
    private final Filtros filtros = new Filtros();
    private final MorfologiaMatematica morfologiaMatematica = new MorfologiaMatematica();

    // SplitPane
    @FXML
    private SplitPane splitPane;

    // StackPanes
    @FXML
    private StackPane inputImageContainer;
    @FXML
    private StackPane outputImageContainer;

    // Imagens
    @FXML
    private ImageView imagemAlterada;
    @FXML
    private ImageView imagemOriginal;

    //Controle das images a serem desfeitas e refeitas
    Stack<Image> ultimaImagem = new Stack<>();
    private Image imagemAnterior;
    private Image refazerImagem;

    // Inputs de Efeito na Imagem
    @FXML
    private TextField valorTransladarX;
    @FXML
    private TextField valorTransladarY;
    @FXML
    private TextField valorAngulo;

    @FXML
    private TextField valorThreshold;

    // Filtros
    @FXML
    private Button greyscaleBtn;
    @FXML
    private Button gaussianoBtn;
    @FXML
    private Button medianaBtn;
    @FXML
    private Button passaAltaSobelBtn;
    @FXML
    private Button passaAltaRobertsBtn;
    @FXML
    private Button dilatacaoBtn;
    @FXML
    private Button erosaoBtn;
    @FXML
    private Button aberturaBtn;
    @FXML
    private Button fechamentoBtn;
    @FXML
    private Button afinamentoBtn;
    @FXML
    private Button[] allButtons;

    private Image imagemBase;
    @FXML
    private Slider sliderBrilho;
    private Tooltip tooltip;
    @FXML
    private TextField valorContraste;

    // Inicialização do FXML
    @FXML
    public void initialize() {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        //Define Lena como a imagem inicial
        try {
            Image lena = new Image(Objects.requireNonNull(getClass().getResource("/images/Lena.jpeg")).toExternalForm());
            imagemOriginal.setImage(lena);
            imagemAlterada.setImage(lena);
            imagemBase = lena;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        allButtons = new Button[]{
                greyscaleBtn,
                gaussianoBtn,
                medianaBtn,
                passaAltaSobelBtn,
                passaAltaRobertsBtn,
                dilatacaoBtn,
                erosaoBtn,
                aberturaBtn,
                fechamentoBtn,
                afinamentoBtn
        };

        // Imagem Input
        imagemOriginal.fitWidthProperty().bind(inputImageContainer.widthProperty());
        imagemOriginal.fitHeightProperty().bind(inputImageContainer.heightProperty());
        imagemOriginal.setPreserveRatio(true);

        // Imagem Output (sem bind, só preserva proporção)
        imagemAlterada.setPreserveRatio(true);

        tooltip = new Tooltip(String.format("%.2f", sliderBrilho.getValue()));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);
        tooltip.setHideDelay(Duration.ZERO);
        sliderBrilho.setTooltip(tooltip);

        sliderBrilho.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (imagemBase != null) {
                ultimaImagem.push(imagemAlterada.getImage()); // registro para desfazer
                filtros.ajustarBrilho(imagemOriginal, imagemAlterada, newVal.doubleValue());
                tooltip.setText(String.format("%.2f", (double) newVal));
            }
        });
    }

    // Funções do Menu do Topo
    @FXML
    void openImage(ActionEvent event) {
        // Cria o seletor de arquivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Imagem");

        // Define os tipos de arquivo permitidos
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

        // Abre a janela para escolher o arquivo
        File file = fileChooser.showOpenDialog(((MenuItem) event.getSource()).getParentPopup().getOwnerWindow());

        if (file != null) {
            // Cria a imagem a partir do arquivo
            Image image = new Image(file.toURI().toString());

            // Mostra a mesma imagem nas duas ImageViews
            imagemOriginal.setImage(image);
            imagemAlterada.setImage(image);
        }
    }

    @FXML
    void salvarImagem(ActionEvent event) {
        Image image = imagemAlterada.getImage();
        if (image == null) {
            alerta("Nenhuma imagem para ser salva!");
        }

        // Configura o seletor
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Imagem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"));

        // Abre a janela
        File file = fileChooser.showSaveDialog(imagemAlterada.getScene().getWindow());
        if (file != null) {
            try {
                String fileName = file.getName().toLowerCase();
                String format = "png";
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    format = "jpg";
                }

                ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText("Imagem salva em " + file.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception e) {
                alerta("Erro: " + e.getMessage());
            }
        }
    }

    //Fecha o programa
    @FXML
    void sair(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void desfazer(ActionEvent event) {
        if (!ultimaImagem.isEmpty()) {
            refazerImagem = imagemAlterada.getImage();
            imagemAnterior = ultimaImagem.pop();
            imagemAlterada.setImage(imagemAnterior);
        }
    }

    @FXML
    void refazer(ActionEvent event) {
        if (refazerImagem != null) {
            imagemAlterada.setImage(refazerImagem);
        }
    }

    //Reseta a posição das imagens na tela
    @FXML
    void resetarPosicao(ActionEvent event) {
        splitPane.setDividerPositions(0.3, 0.65);
    }

    @FXML
    void setDarkTheme(ActionEvent event) {
        if (isLightTheme) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            isLightTheme = false;
        }
    }

    @FXML
    void setLightTheme(ActionEvent event) {
        if (!isLightTheme) {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            isLightTheme = true;
        }
    }

    @FXML
    void imprimir(ActionEvent event) {
        if (imagemAlterada.getImage() == null) {
            alerta("Nenhuma imagem para imprimir!");
        }

        // Cria o trabalho de impressão
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Nenhuma impressora disponível.");
            alert.showAndWait();
            return;
        }

        // Abre a caixa de diálogo de impressão
        boolean proceed = job.showPrintDialog(imagemAlterada.getScene().getWindow());
        if (proceed) {
            // Imprime o ImageView (com a imagem alterada)
            boolean success = job.printPage(imagemAlterada);
            if (success) {
                job.endJob();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText("Imagem enviada para a impressora!");
                alert.showAndWait();
            } else {
                alerta("Falha ao imprimir a imagem.");
            }
        }
    }

    @FXML
    void sobre(ActionEvent event) {
        Alert sobrePrograma = new Alert(Alert.AlertType.CONFIRMATION);
        sobrePrograma.setTitle("Sobre o Programa");
        sobrePrograma.setHeaderText("Editor de Imagens\nProjeto de PDI\nAutores: Lucas Michaelsen, Lucas Santos e Vinícius de Quadros");
        sobrePrograma.showAndWait();
    }

    // Funções de Efeito

    // Funções de Transformacao
    @FXML
    void transladar(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        int x = Integer.parseInt(valorTransladarX.getText());
        int y = Integer.parseInt(valorTransladarY.getText());
        transformacoes.transladarImagem(x, y, imagemOriginal, imagemAlterada);
    }

    @FXML
    void rotacionar(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        int angulo = Integer.parseInt(valorAngulo.getText());
        transformacoes.rotacionarImagem(angulo, imagemOriginal, imagemAlterada);
    }

    @FXML
    void espelharHorizontal(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        transformacoes.espelharHorizontal(imagemAlterada, imagemAlterada);
    }

    @FXML
    void espelharVertical(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        transformacoes.espelharVertical(imagemAlterada, imagemAlterada);
    }

    @FXML
    void aumentar(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        transformacoes.aumentar(imagemAlterada, imagemAlterada);
    }

    @FXML
    void diminuir(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        transformacoes.diminuir(imagemAlterada, imagemAlterada);
    }

    //Funções de Filtro
    @FXML
    void greyscale(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        filtros.greyscaleImagem(imagemOriginal, imagemAlterada);
        filtros.setGreyscale();
        changeColorBtn(filtros.isGreyscale(), greyscaleBtn);
    }

    @FXML
    void threshold(ActionEvent event) {
        int valor = Integer.parseInt(valorThreshold.getText());
        if (valor < 0) {
            valor = 0;
            alerta("Digite um número válido para threshold!");
            valorThreshold.setText("0");
        } else if (valor > 255) {
            valor = 255;
            valorThreshold.setText("255");
        }
        filtros.thresholdImage(imagemOriginal, imagemAlterada, valor);
    }

    @FXML
    void contrastre(ActionEvent event) {
        try {
            if (Integer.parseInt(valorContraste.getText()) < 0) {
                alerta("Digite um número válido para contraste!");
                valorContraste.setText("1");
                ultimaImagem.push(imagemAlterada.getImage());
            } else {
                double contraste = Double.parseDouble(valorContraste.getText());

                filtros.contrasteImagem(imagemOriginal, imagemAlterada, contraste);
            }
        } catch (NumberFormatException e) {
            alerta(null);
        }
    }

    @FXML
    void mediana(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        filtros.PBMediana(imagemOriginal, imagemAlterada);
        filtros.setMediana();
        changeColorBtn(filtros.isMediana(), medianaBtn);
    }

    @FXML
    void gaussiano(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        filtros.PBGaussiano(imagemOriginal, imagemAlterada);
        filtros.setGaussiano();
        changeColorBtn(filtros.isGaussiano(), gaussianoBtn);
    }

    @FXML
    public void passaAltaSobel(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        filtros.PASobel(imagemOriginal, imagemAlterada);
        filtros.setSolbel();
        changeColorBtn(filtros.isSolbel(), passaAltaSobelBtn);
    }

    @FXML
    public void passaAltaRoberts(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        filtros.PARoberts(imagemOriginal, imagemAlterada);
        filtros.setRoberts();
        changeColorBtn(filtros.isRoberts(), passaAltaRobertsBtn);
    }

    @FXML
    public void afinamento(ActionEvent event){
        ultimaImagem.push(imagemAlterada.getImage());
        filtros.setAfinamento();
        changeColorBtn(filtros.isAfinamento(), afinamentoBtn);
    }

    //Morfologia Matemática
    @FXML
    void dilatacao(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        morfologiaMatematica.operacaoMorfologica(imagemOriginal, imagemAlterada, "DILATACAO");
        morfologiaMatematica.setDilatacao();
        changeColorBtn(morfologiaMatematica.isDilatacao(), dilatacaoBtn);
    }

    @FXML
    void erosao(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        morfologiaMatematica.operacaoMorfologica(imagemOriginal, imagemAlterada, "EROSAO");
        morfologiaMatematica.setErosao();
        changeColorBtn(morfologiaMatematica.isErosao(), erosaoBtn);
    }

    @FXML
    void abertura(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        morfologiaMatematica.abertura(imagemOriginal, imagemAlterada);
        morfologiaMatematica.setAbertura();
        changeColorBtn(morfologiaMatematica.isAbertura(), aberturaBtn);
    }

    @FXML
    void fechamento(ActionEvent event) {
        ultimaImagem.push(imagemAlterada.getImage());
        morfologiaMatematica.fechamento(imagemOriginal, imagemAlterada);
        morfologiaMatematica.setFechamento();
        changeColorBtn(morfologiaMatematica.isFechamento(), fechamentoBtn);
    }

    //Permite carregar a imagem utilizando links externos
    public void carregarImagemDeLink(String url) {
        try {
            Image image = new Image(url, true); // true = carrega de forma assíncrona
            imagemOriginal.setImage(image);
            imagemAlterada.setImage(image);
            imagemBase = image;
        } catch (Exception e) {
            alerta("Não foi possível carregar a imagem do link.");
        }
    }

    private void alerta(String mensagem) {
        if (mensagem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Entrada inválida");
            alert.setHeaderText("Erro");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Entrada inválida");
            alert.setHeaderText(mensagem);
            alert.showAndWait();
        }
    }

    private void changeColorBtn(boolean filter, Button button){
        if (filter) {
            activateBtn(button);
        } else {
            deactivateBtn(button);
        }
    }

    private void activateBtn(Button button){
        if (!isLightTheme) {
            button.setStyle("-fx-background-color: #015801;");
        } else {
            button.setStyle("-fx-background-color: #00ff00;");
        }
        deactivateBtn(button);
    }

    private void deactivateBtn(Button button){
        for(Button b : allButtons){
            if(b == button){
                continue;
            }
            b.setStyle("");
        }
    }
}