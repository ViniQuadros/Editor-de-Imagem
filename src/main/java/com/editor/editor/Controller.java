package com.editor.editor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {
    //Classes de Efeito
    private Transformacoes transformacoes = new  Transformacoes();

    //Imagens
    @FXML
    private ImageView imagemAlterada;
    @FXML
    private ImageView imagemOriginal;

    //Menus do Topo
    @FXML
    private MenuItem abrirImagemMenu;
    @FXML
    private MenuItem sairMenu;

    //Botões de Efeito na Imagem

    //Transformações
    @FXML
    private Button transladarBtn;
    @FXML
    private Button espelharBtn;

    //Funções do Menu do Topo
    @FXML
    void openImage(ActionEvent event) {
        // Cria o seletor de arquivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Imagem");

        // Define os tipos de arquivo permitidos
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

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
    void sair(ActionEvent event) {
        Platform.exit();
    }

    //Funções de Efeito

    //Funções de Transformacao
    @FXML
    void transladar(ActionEvent event) {
        transformacoes.transladarImagem(50,60, imagemOriginal, imagemAlterada);
    }

    @FXML
    void espelhar(ActionEvent event) {
        transformacoes.espelharImagem(imagemOriginal, imagemAlterada);
    }

    @FXML
    void espelharHorizontal(ActionEvent event) {
        // TODO: implementar espelhamento horizontal
        System.out.println("Espelhar Horizontal chamado");
    }

    @FXML
    void espelharVertical(ActionEvent event) {
        // TODO: implementar espelhamento vertical
        System.out.println("Espelhar Vertical chamado");
    }

    @FXML
    void aumentar(ActionEvent event) {
        // TODO: implementar zoom aumentar
        System.out.println("Aumentar chamado");
    }

    @FXML
    void diminuir(ActionEvent event) {
        // TODO: implementar zoom diminuir
        System.out.println("Diminuir chamado");
    }

    @FXML
    void rotacionar(ActionEvent event) {
        // TODO: implementar rotação
        System.out.println("Rotacionar chamado");
    }

}


