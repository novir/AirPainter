package air_painter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class AirPainter extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = constructUIFromFXMLFile("root_elements.fxml");
        Scene primaryScene = new Scene(root, 500, 500);
        primaryStage.setTitle("Air Painter");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    private Parent constructUIFromFXMLFile(String fileName) {
        Pane rootPane = new HBox();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(AirPainter.class.getResource(fileName));
        fxmlLoader.setRoot(rootPane);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            System.err.println("Failed to load object hierarchy from FXML file");
            e.printStackTrace();
            return rootPane;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
