package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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
}