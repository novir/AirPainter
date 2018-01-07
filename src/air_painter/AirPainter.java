package air_painter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class AirPainter extends Application {

    private FXMLLoader fxmlLoader = null;
    private Scene primaryScene = null;
    private VideoController videoController = null;

    public AirPainter() {
        fxmlLoader = buildLoaderFromFXMLFile("root_elements.fxml", new HBox());
        primaryScene = buildPrimaryScene();
        UIController uiController = fxmlLoader.getController();
        videoController = new VideoController(uiController);
        uiController.setVideoController(videoController);
    }

    private FXMLLoader buildLoaderFromFXMLFile(@NotNull String fileName,
                                               @NotNull Pane rootPane) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(AirPainter.class.getResource(fileName));
        fxmlLoader.setRoot(rootPane);
        return fxmlLoader;
    }

    @NotNull
    private Scene buildPrimaryScene() {
        Parent root = loadUIHierarchyFromFXML();
        return new Scene(root, 1000, 800);
    }

    @Nullable
    private Pane loadUIHierarchyFromFXML() {
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            System.err.println("Failed to load object hierarchy from FXML file");
            e.printStackTrace();
            return null;
        }
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
