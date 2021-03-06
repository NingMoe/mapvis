package mapvis.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        URL location = AppController.class.getResource("app.fxml");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        BorderPane root = loader.load(location.openStream());

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(1000);

        stage.show();
    }

}
