package spotify;

public class SpotifyCredentialsServiceImpl implements SpotifyCredentialsService {

    String clientID, clientSecret, redirectorUri;

    public void credentials(String spotifyUser, String spotifyPass){
        clientID = spotifyUser;
        clientSecret = spotifyPass;
    }

}
