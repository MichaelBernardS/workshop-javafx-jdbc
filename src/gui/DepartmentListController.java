package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listerners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // Primeira coluna - Integer por causa do id, q � inteiro;
	
	@FXML
	private TableColumn<Department, String> tableColumnName; // Segunda coluna - String por causa do nome, q � String;
	
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT; // Criado o atributo para podermos atualizar departamento;
	
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE; // Criado o atributo para podermos remover departamento;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) { // Adicionado arg para ter uma refer�ncia para o controle que recebeu o evento; E a partir do evento, vai ter condi��o de acessar o Stage; Bot�o p cadastrar um novo departamento;
		Stage parentStage = Utils.currentStage(event); // Refer�ncia pro stage atual, e passando pra criar a janela de formul�rio;
		Department obj = new Department(); // J� que vamos colocar um novo departamento, vamos instanciar um vazio, sem nenhum dado; Passar um objeto para o formul�rio, � uma pr�tica comum qnd estamos programando no padr�o MVC;
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage); // 1 - Par�metro objeto do departamento, para injetar no controlador do formul�rio; 2 - O formul�rio que abriremos; 3 - Janela pai;
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
		initEditButtons(); // M�todo que acrescenta o bot�o com o texto edit em cada linha da tabela; E todo bot�o que for clicado, abre o formul�rio de edi��o;
		initRemoveButtons();
	}
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) { // Formul�rio pra preencher um novo departamento; Qnd � criado uma janela de di�logo, � necess�rio informar quem � o Stage que criou essa janela de di�logo, por isto, foi passado por param;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // Carregando a view;
			
			DepartmentFormController controller = loader.getController(); // Pegando refer�ncia do controlador;
			controller.setDepartment(obj); // Injetando o departamento no controlador;
			controller.setDepartmentService(new DepartmentService()); // Inje��o da depend�ncia no controlador;
			controller.subscribeDataChangeListener(this); // Me inscrever para escutar/receber o evento, e qnd for disparado vai ser executado o m�todo onDataChanged abaixo.
			controller.updateFormData(); // Carregar os dados desse objeto no formul�rio;
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); // Se a janela pode ou n�o ser redimensionada, ou seja, n�o pode redimensionada;
			dialogStage.initOwner(parentStage); // Stage pai dessa janela;
			dialogStage.initModality(Modality.WINDOW_MODAL); // Se a janela vai ser modal (Enqnt ela tiver ativa, n�o clica na janela de tr�s, ou seja, enqnt n�o fechar ela, n�o pode acessar a outra de tr�s) ou outro comportamento;
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() { // Notifica��o que os dados foram alterados;
		updateTableView(); // Atualizar os dados da tabelinha;
	}
	
	private void initEditButtons() { // M�todo respons�vel por criar cada bot�o de edi��o em cada linha da tabela, para ser poss�vel clicar em cada bot�o e editar o departamento; (C�digo espec�fico pego de um framework no stackoverflow);
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() { // Criado um objeto CellFactory respons�vel por instanciar os bot�es e configurar os bot�es;
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
								event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event))); // Na hora de criar a janela do formul�rio, passa o obj, que � o departamento da linha que tiver o bot�o de edi��o que clicar, ou seja, pegando um objeto preenchido com departamento e criando a tela de cadastro j� com esse objeto preenchido;  E todo bot�o que for clicado, abre o formul�rio de edi��o;
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if (obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Department obj) { // Remo��o de um departamento; Opera��o para remover uma entidade;
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?"); // Resultado desse Alert, que vai ser um bot�o clicado, atribuido a uma vari�vel do tipo ButtonType;
		
		if (result.get() == ButtonType.OK) { // .get pois o optional, carrega o outro objeto dentro dele, podendo estar presente ou n�o, ent�o p acessar este objeto dentro do optional, usa-se o get; Ou seja, se apertar no OK, confirmou a dele��o;
			if (service == null) { // Passou desse if, significa que o service foi instanciado;
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj); // Chamar a opera��o de remover;
				updateTableView(); // For�ar a atualizar os dados da tabela;
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}