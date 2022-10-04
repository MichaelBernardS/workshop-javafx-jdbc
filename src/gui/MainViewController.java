package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartmentService;
import model.services.SellerService;

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
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> { // Antes feito em uma outra fun��o, foi movido para c� para passar como par�metro, servindo para inicializar o controlador; Parametriza��o com o Consumer T, para n�o precisar criar mais vers�es da fun��o LoadView;
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		}); 
	}
	
	@FXML
	public void onMenuItemDepartmentAction() { // Padr�o usado nos EventHandlers. Come�a com on, dps o nome do controle, dps o evento (Action � padr�o) que vai tratar;
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> { // Antes feito em uma outra fun��o, foi movido para c� para passar como par�metro, servindo para inicializar o controlador; Parametriza��o com o Consumer T, para n�o precisar criar mais vers�es da fun��o LoadView;
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		}); 
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {}); // Como a janela about n�o tem nd p ser feito, vms declarar uma fun��o x q leva em nd;
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {	
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer <T> initializingAction) { // Absolute pq vms passar o caminho absoluto; Synchronized pra garantir que o processamento n�o interrompa, j� que opera��es gr�fica s�o multithreads; Interface funcional Consumer, tornando a fun��o gen�rica;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load(); // Carregar a view;
			
			Scene mainScene = Main.getMainScene(); // Mostrar a view dentro da janela principal; Puxando ele pela refer�ncia;
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // M�todo getRoot pega o primeiro elemento (ScrollPane no caso) da view; .getContent p acessar o conte�do do scrollPane;
			
			Node mainMenu = mainVBox.getChildren().get(0); // Primeiro filho do VBox da janela principal, que � o MainMenu;
			mainVBox.getChildren().clear(); // Limpar todos os filhos do MainVBox;
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			// Comandos especiais para ativar o que passar como par�metro na fun��o (p abrir a tela na hr q abrir o DepartmentList);
			
			T controller = loader.getController(); // getController vai retornar um controlador do tipo que chamar acima, no caso, DepartmentListController;
			initializingAction.accept(controller); // Executar a a��o, atrav�s do accept do Consumer;
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}