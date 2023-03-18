package spotify;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SpotifyController{

    @FXML
    TextField spotifyUser, spotifyPass;

    public SpotifyController(){

    }

    @FXML
    private void passMask(){
        
    }

    @FXML
    private void spotifyAuthorization() {
        SpotifyCredentialsService credents = new SpotifyCredentialsServiceLocal();
        credents.credentials(spotifyUser.getText(), spotifyPass.getText());
    }
}
