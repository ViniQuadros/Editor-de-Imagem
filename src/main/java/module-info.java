module com.editor.editor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.base;

    opens com.editor.editor to javafx.fxml;
    exports com.editor.editor;
}