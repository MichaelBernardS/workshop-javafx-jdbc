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
	public void onMenuItemSellerAction() { // Padrão usado nos EventHandlers. Começa com on, dps o nome do controle, dps o evento (Action é padrão) que vai tratar;
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> { // Antes feito em uma outra função, foi movido para cá para passar como parâmetro, servindo para inicializar o controlador; Parametrização com o Consumer T, para não precisar criar mais versões da função LoadView;
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		}); 
	}
	
	@FXML
	public void onMenuItemDepartmentAction() { // Padrão usado nos EventHandlers. Começa com on, dps o nome do controle, dps o evento (Action é padrão) que vai tratar;
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> { // Antes feito em uma outra função, foi movido para cá para passar como parâmetro, servindo para inicializar o controlador; Parametrização com o Consumer T, para não precisar criar mais versões da função LoadView;
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		}); 
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {}); // Como a janela about não tem nd p ser feito, vms declarar uma função x q leva em nd;
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {	
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer <T> initializingAction) { // Absolute pq vms passar o caminho absoluto; Synchronized pra garantir que o processamento não interrompa, já que operações gráfica são multithreads; Interface funcional Consumer, tornando a função genérica;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load(); // Carregar a view;
			
			Scene mainScene = Main.getMainScene(); // Mostrar a view dentro da janela principal; Puxando ele pela referência;
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent(); // Método getRoot pega o primeiro elemento (ScrollPane no caso) da view; .getContent p acessar o conteúdo do scrollPane;
			
			Node mainMenu = mainVBox.getChildren().get(0); // Primeiro filho do VBox da janela principal, que é o MainMenu;
			mainVBox.getChildren().clear(); // Limpar todos os filhos do MainVBox;
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			// Comandos especiais para ativar o que passar como parâmetro na função (p abrir a tela na hr q abrir o DepartmentList);
			
			T controller = loader.getController(); // getController vai retornar um controlador do tipo que chamar acima, no caso, DepartmentListController;
			initializingAction.accept(controller); // Executar a ação, através do accept do Consumer;
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}