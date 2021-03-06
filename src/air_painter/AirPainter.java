package air_painter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Core;

import java.io.IOException;

public class AirPainter extends Application {

    private Scene primaryScene = null;

    private FXMLLoader fxmlLoader = null;

    private VideoController videoController = null;

    public AirPainter() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        initFXMLLoader();
        initPrimaryScene();
        bindControllers();
    }

    private void initFXMLLoader() {
        String fxmlFile = "gui_elements.fxml";
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(AirPainter.class.getResource(fxmlFile));
        fxmlLoader.setRoot(new BorderPane());
    }

    private void initPrimaryScene() {
        Parent root = loadUIHierarchyFromFXML();
        primaryScene = new Scene(root, 1000, 800);
    }

    @NotNull
    private Pane loadUIHierarchyFromFXML() {
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            System.err.println("Failed to load object hierarchy from FXML file");
            return new Pane();
        }
    }

    private void bindControllers() {
        UIController uiController = fxmlLoader.getController();
        videoController = new VideoController(uiController);
        uiController.setVideoController(videoController);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Air Painter");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        primaryStage.sizeToScene();
    }

    @Override
    public void stop() {
        videoController.stopDisplay();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
