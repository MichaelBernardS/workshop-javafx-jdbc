package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Scene mainScene; // Guardando a referência da cena nesse atributo; (Boa prática deixar ele privado)
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml")); // Instanciando e não chamando método estático; Importante para manipular a tela antes de carregar;
			ScrollPane scrollPane = loader.load(); // O load carrega a View;
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			mainScene = new Scene(scrollPane); // Cena principal, já instanciando como arg o objeto principal da View (Anchor Pane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getMainScene() { // Jà que o método estático é privado, fizemos um método para referenciar essa cena;
		return mainScene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}