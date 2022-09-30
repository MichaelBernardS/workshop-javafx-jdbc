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
	private TableColumn<Department, Integer> tableColumnId; // Primeira coluna - Integer por causa do id, q � inteiro;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) { // Adicionado arg para ter uma refer�ncia para o controle que recebeu o evento; E a partir do evento, vai ter condi��o de acessar o Stage;
		Stage parentStage = Utils.currentStage(event); // Refer�ncia pro stage atual, e passando pra criar a janela de formul�rio;
		createDialogForm("/gui/DepartmentForm.fxml", parentStage); // 1 - O formul�rio que abriremos; 2 - Janela pai;
	}
	
	public void setDepartmentService(DepartmentService service) { // Ter a condin��o de injetar depend�ncia, de qualquer lugar, e n�o instanciar direto na classe; Isso � um princ�pio solid, que � a invers�o de controle, s�o boas pr�ticas, de fzer um set pra ter essa possibilidade, e n�o instanciar direto na classe;
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // Para iniciar algum componente da tela;
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); // Padr�o do JavaFX para iniciar o comportamento das colunas;
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // Pega a refer�ncia pra janela; O window � uma superclasse do stage, por isso fizemos um downcasting p stage;
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // TableView para acompanhar a altura (tamanho) da janela;	
	}
	
	public void updateTableView() { // M�todo respons�vel por acessar o servi�o, carregar os deps e jogar na obsList, e dps associar ao TableView;
		if (service == null) { // Caso o dev esque�a de lan�ar um servi�o, criamos uma exce��o personalizada;
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = service.findAll();	
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	
	private void createDialogForm(String absoluteName, Stage parentStage) { // Formul�rio pra preencher um novo departamento; Qnd � criado uma janela de di�logo, � necess�rio informar quem � o Stage que criou essa janela de di�logo, por isto, foi passado por param;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // Carregando a view;
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); // Se a janela pode ou n�o ser redimensionada, ou seja, n�o pode redimensionada;
			dialogStage.initOwner(parentStage); // Stage pai dessa janela;
			dialogStage.initModality(Modality.WINDOW_MODAL); // Se a janela vai ser modal (Enqnt ela tiver ativa, n�o clica na janela de tr�s, ou seja, enqnt n�o fechar ela, n�o pode acessar a outra de tr�s) ou outro comportamento;
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}