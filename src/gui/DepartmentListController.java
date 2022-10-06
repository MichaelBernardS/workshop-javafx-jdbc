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
	private TableColumn<Department, Integer> tableColumnId; // Primeira coluna - Integer por causa do id, q é inteiro;
	
	@FXML
	private TableColumn<Department, String> tableColumnName; // Segunda coluna - String por causa do nome, q é String;
	
	@FXML
	private TableColumn<Department, Department> tableColumnEDIT; // Criado o atributo para podermos atualizar departamento;
	
	@FXML
	private TableColumn<Department, Department> tableColumnREMOVE; // Criado o atributo para podermos remover departamento;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) { // Adicionado arg para ter uma referência para o controle que recebeu o evento; E a partir do evento, vai ter condição de acessar o Stage; Botão p cadastrar um novo departamento;
		Stage parentStage = Utils.currentStage(event); // Referência pro stage atual, e passando pra criar a janela de formulário;
		Department obj = new Department(); // Já que vamos colocar um novo departamento, vamos instanciar um vazio, sem nenhum dado; Passar um objeto para o formulário, é uma prática comum qnd estamos programando no padrão MVC;
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage); // 1 - Parâmetro objeto do departamento, para injetar no controlador do formulário; 2 - O formulário que abriremos; 3 - Janela pai;
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
		initEditButtons(); // Método que acrescenta o botão com o texto edit em cada linha da tabela; E todo botão que for clicado, abre o formulário de edição;
		initRemoveButtons();
	}
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) { // Formulário pra preencher um novo departamento; Qnd é criado uma janela de diálogo, é necessário informar quem é o Stage que criou essa janela de diálogo, por isto, foi passado por param;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // Carregando a view;
			
			DepartmentFormController controller = loader.getController(); // Pegando referência do controlador;
			controller.setDepartment(obj); // Injetando o departamento no controlador;
			controller.setDepartmentService(new DepartmentService()); // Injeção da dependência no controlador;
			controller.subscribeDataChangeListener(this); // Me inscrever para escutar/receber o evento, e qnd for disparado vai ser executado o método onDataChanged abaixo.
			controller.updateFormData(); // Carregar os dados desse objeto no formulário;
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false); // Se a janela pode ou não ser redimensionada, ou seja, não pode redimensionada;
			dialogStage.initOwner(parentStage); // Stage pai dessa janela;
			dialogStage.initModality(Modality.WINDOW_MODAL); // Se a janela vai ser modal (Enqnt ela tiver ativa, não clica na janela de trás, ou seja, enqnt não fechar ela, não pode acessar a outra de trás) ou outro comportamento;
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() { // Notificação que os dados foram alterados;
		updateTableView(); // Atualizar os dados da tabelinha;
	}
	
	private void initEditButtons() { // Método responsável por criar cada botão de edição em cada linha da tabela, para ser possível clicar em cada botão e editar o departamento; (Código específico pego de um framework no stackoverflow);
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() { // Criado um objeto CellFactory responsável por instanciar os botões e configurar os botões;
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
								event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event))); // Na hora de criar a janela do formulário, passa o obj, que é o departamento da linha que tiver o botão de edição que clicar, ou seja, pegando um objeto preenchido com departamento e criando a tela de cadastro já com esse objeto preenchido;  E todo botão que for clicado, abre o formulário de edição;
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

	private void removeEntity(Department obj) { // Remoção de um departamento; Operação para remover uma entidade;
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?"); // Resultado desse Alert, que vai ser um botão clicado, atribuido a uma variável do tipo ButtonType;
		
		if (result.get() == ButtonType.OK) { // .get pois o optional, carrega o outro objeto dentro dele, podendo estar presente ou não, então p acessar este objeto dentro do optional, usa-se o get; Ou seja, se apertar no OK, confirmou a deleção;
			if (service == null) { // Passou desse if, significa que o service foi instanciado;
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj); // Chamar a operação de remover;
				updateTableView(); // Forçar a atualizar os dados da tabela;
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}