package com.editor.editor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Objects;
import java.util.Stack;

public class Controller {
    // Classes de Efeito
    private final Transformacoes transformacoes = new Transformacoes();

    // SplitPane
    @FXML
    private SplitPane splitPane;

    // Anchors
    @FXML
    private AnchorPane inputPane;
    @FXML
    private AnchorPane outputPane;

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

    // Menus do Topo
    @FXML
    private MenuItem abrirImagemMenu;
    @FXML
    private MenuItem sairMenu;
    @FXML
    private MenuItem resetarPosicaoMenu;
    @FXML
    private MenuItem sobreMenu;

    // Inputs de Efeito na Imagem
    @FXML
    private TextField valorTransladarX;
    @FXML
    private TextField valorTransladarY;
    @FXML
    private TextField valorAngulo;

    // Botões de Efeito na Imagem

    // Transformações
    @FXML
    private Button transladarBtn;
    @FXML
    private Button espelharBtn;

    // Inicialização
    @FXML
    public void initialize() {
        try{
            Image lena = new Image(Objects.requireNonNull(getClass().getResource("/images/Lena.jpeg")).toExternalForm());
            imagemOriginal.setImage(lena);
            imagemAlterada.setImage(lena);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Input
        imagemOriginal.fitWidthProperty().bind(inputImageContainer.widthProperty());
        imagemOriginal.fitHeightProperty().bind(inputImageContainer.heightProperty());
        imagemOriginal.setPreserveRatio(true);

        // Output
        imagemAlterada.fitWidthProperty().bind(outputImageContainer.widthProperty());
        imagemAlterada.fitHeightProperty().bind(outputImageContainer.heightProperty());
        imagemAlterada.setPreserveRatio(true);

//         // Input
//         imagemOriginal.fitWidthProperty().bind(inputPane.widthProperty());
//         imagemOriginal.fitHeightProperty().bind(inputPane.heightProperty());
//         imagemOriginal.setPreserveRatio(true);
//
//         // Output
//         imagemAlterada.fitWidthProperty().bind(outputPane.widthProperty());
//         imagemAlterada.fitHeightProperty().bind(outputPane.heightProperty());
//         imagemAlterada.setPreserveRatio(true);
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Nenhuma imagem para ser salva!");
            alert.showAndWait();
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
                e.printStackTrace();
            }
        }
    }

    @FXML
    void sair(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void desfazer(ActionEvent event) {
        if(!ultimaImagem.isEmpty()){
            refazerImagem = imagemAlterada.getImage();
            imagemAnterior = ultimaImagem.pop();
            imagemAlterada.setImage(imagemAnterior);
        }
    }

    @FXML
    void refazer(ActionEvent event) {
        if(refazerImagem != null){
            imagemAlterada.setImage(refazerImagem);
        }
    }

    @FXML
    void resetarPosicao(ActionEvent event) {
        splitPane.setDividerPositions(0.3, 0.65);
    }

    @FXML
    void sobre(ActionEvent event) {
        Alert sobrePrograma = new Alert(Alert.AlertType.CONFIRMATION);
        sobrePrograma.setTitle("Sobre o Programa");
        sobrePrograma.setHeaderText("Editor de Imagens \nProjeto de PDI");
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
        System.out.println("Aumentar chamado");
    }

    @FXML
    void diminuir(ActionEvent event) {
        System.out.println("Diminuir chamado");
    }
}
