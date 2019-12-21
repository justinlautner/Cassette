package MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //The basics
        URL url = new File("src/main/java/MainPackage/MainPage.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Cassette");
        primaryStage.setScene(new Scene(root, 900, 475));


        primaryStage.show();
    }//end start method


    public static void main(String[] args) {
        launch(args);
    }

}

