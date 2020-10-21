package mainpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //The basics
        URL url = new File("src/main/resources/fxml/MainPage.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        Controller controller = loader.getController();

        //Send stage to playlist, to change title according to now playing
        controller.setStage(primaryStage);
        controller.loadMusic();
        primaryStage.setTitle("Cassette");
        primaryStage.getIcons().add(new Image("icons/cassette-icon.png"));
        primaryStage.setScene(new Scene(root, 1425, 775));

        primaryStage.show();
    }//end start method


    public static void main(String[] args) {
        launch(args);
    }

}

