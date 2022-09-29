package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	// EventHandlers (Eventos que tratam os eventos do Menu);
	
	@FXML
	public void onMenuItemSellerAction() { // Padr�o usado nos EventHandlers. Come�a com on, dps o nome do controle, dps o evento (Action � padr�o) que vai tratar;
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/Aboutmfxml");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {	
	}
	
	private synchronized void loadView(String absoluteName) { // Absolute pq vms passar o caminho absoluto; Synchronized pra garantir que o processamento n�o interrompa, j� que opera��es gr�fica s�o multithreads;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load(); // Carregar a view;
			
			Scene mainScene = Main.getMainScene(); // Mostrar a view dentro da janela principal; Puxando ele pela refer�ncia;
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // M�todo getRoot pega o primeiro elemento (ScrollPane no caso) da view; .getContent p acessar o conte�do do scrollPane;
			
			Node mainMenu = mainVBox.getChildren().get(0); // Primeiro filho do VBox da janela principal, que � o MainMenu;
			mainVBox.getChildren().clear(); // Limpar todos os filhos do MainVBox;
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}