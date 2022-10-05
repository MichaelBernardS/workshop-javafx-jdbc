package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId; // Primeira coluna do Department - Integer por causa do id;
	
	@FXML
	private TableColumn<Seller, String> tableColumnName; // Segunda coluna do Department - String por causa do nome;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail; // Primeira coluna do Seller - String por causa do e-mail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate; // Segunda coluna do Seller - Date por causa da data;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary; // Terceira coluna do Seller - Double por causa do salário;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT; // Criado o atributo para podermos atualizar departamento;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE; // Criado o atributo para podermos remover departamento;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) { // Adicionado arg para ter uma referência para o controle que recebeu o evento; E a partir do evento, vai ter condição de acessar o Stage; Botão p cadastrar um novo departamento;
		Stage parentStage = Utils.currentStage(event); // Referência pro stage atual, e passando pra criar a janela de formulário;
		Seller obj = new Seller(); // Já que vamos colocar um novo departamento, vamos instanciar um vazio, sem nenhum dado; Passar um objeto para o formulário, é uma prática comum qnd estamos programando no padrão MVC;
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage); // 1 - Parâmetro objeto do departamento, para injetar no controlador do formulário; 2 - O formulário que abriremos; 3 - Janela pai;
	}
	
	public void setSellerService(SellerService service) { // Ter a condinção de injetar dependência, de qualquer lugar, e não instanciar direto na classe; Isso é um princípio solid, que é a inversão de controle, são boas práticas, de fzer um set pra ter essa possibilidade, e não instanciar direto na classe;
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // Para iniciar algum componente da tela;
	}

	private void initializeNodes() { // Inicialização das colunas;
		
		// Departament
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id")); // Padrão do JavaFX para iniciar o comportamento das colunas;
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		// Seller
		
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy"); // Formatação de data na tabela;
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2); // Formatação do ponto flutuante na tabela, 2 casas decimais
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // Pega a referência pra janela; O window é uma superclasse do stage, por isso fizemos um downcasting p stage;
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); // TableView para acompanhar a altura (tamanho) da janela;	
	}
	
	public void updateTableView() { // Método responsável por acessar o serviço, carregar os deps e jogar na obsList, e dps associar ao TableView;
		if (service == null) { // Caso o dev esqueça de lançar um serviço, criamos uma exceção personalizada;
			throw new IllegalStateException("Service was null");
		}
		List<Seller> list = service.findAll();	
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons(); // Método que acrescenta o botão com o texto edit em cada linha da tabela; E todo botão que for clicado, abre o formulário de edição;
		initRemoveButtons();
	}
	
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) { // Formulário pra preencher um novo departamento; Qnd é criado uma janela de diálogo, é necessário informar quem é o Stage que criou essa janela de diálogo, por isto, foi passado por param;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load(); // Carregando a view;
			
			SellerFormController controller = loader.getController(); // Pegando referência do controlador;
			controller.setSeller(obj); // Injetando o departamento no controlador;
			controller.setSellerService(new SellerService()); // Injeção da dependência no controlador;
			controller.subscribeDataChangeListener(this); // Me inscrever para escutar/receber o evento, e qnd for disparado vai ser executado o método onDataChanged abaixo.
			controller.updateFormData(); // Carregar os dados desse objeto no formulário;
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
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

	@Override
	public void onDataChanged() { // Notificação que os dados foram alterados;
		updateTableView(); // Atualizar os dados da tabelinha;
	}
	
	private void initEditButtons() { // Método responsável por criar cada botão de edição em cada linha da tabela, para ser possível clicar em cada botão e editar o departamento; (Código específico pego de um framework no stackoverflow);
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() { // Criado um objeto CellFactory responsável por instanciar os botões e configurar os botões;
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
								event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event))); // Na hora de criar a janela do formulário, passa o obj, que é o departamento da linha que tiver o botão de edição que clicar, ou seja, pegando um objeto preenchido com departamento e criando a tela de cadastro já com esse objeto preenchido;  E todo botão que for clicado, abre o formulário de edição;
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
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

	private void removeEntity(Seller obj) { // Remoção de um departamento; Operação para remover uma entidade;
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