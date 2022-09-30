package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // Primeira coluna - Integer por causa do id, q é inteiro;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) { // Adicionado arg para ter uma referência para o controle que recebeu o evento; E a partir do evento, vai ter condição de acessar o Stage;
		Stage parentStage = Utils.currentStage(event); // Referência pro stage atual, e passando pra criar a janela de formulário;
		createDialogForm("/gui/DepartmentForm.fxml", parentStage); // 1 - O formulário que abriremos; 2 - Janela pai;
	}
	
	public void setDepartmentService(DepartmentService service) { // Ter a condinção de injetar dependência, de qualquer lugar, e não instanciar direto na classe; Isso é um princípio solid, que é a inversão de controle, são boas práticas, de fzer um set pra ter essa possibilidade, e não instanciar direto na classe;
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // Para iniciar algum componente da tela;
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); // Padrão do JavaFX para iniciar o comportamento das colunas;
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // Pega a referência pra janela; O window é uma superclasse do stage, por isso fizemos um downcasting p stage;
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // TableView para acompanhar a altura (tamanho) da janela;	
	}
	
	public void updateTableView() { // Método responsável por acessar o serviço, carregar os deps e jogar na obsList, e dps associar ao TableView;
		if (service == null) { // Caso o dev esqueça de lançar um serviço, criamos uma exceção personalizada;
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll();	
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
	private void createDialogForm(String absoluteName, Stage parentStage) { // Formulário pra preencher um novo departamento; Qnd é criado uma janela de diálogo, é necessário informar quem é o Stage que criou essa janela de diálogo, por isto, foi passado por param;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // Carregando a view;
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); // Se a janela pode ou não ser redimensionada, ou seja, não pode redimensionada;
			dialogStage.initOwner(parentStage); // Stage pai dessa janela;
			dialogStage.initModality(Modality.WINDOW_MODAL); // Se a janela vai ser modal (Enqnt ela tiver ativa, não clica na janela de trás, ou seja, enqnt não fechar ela, não pode acessar a outra de trás) ou outro comportamento;
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}