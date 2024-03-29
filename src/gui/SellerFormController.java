package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listerners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	
	private Seller entity; // Entidade relacionada a este formul�rio, generalizando;
	
	private SellerService service;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); // Classe vai guardar uma lista de objetos interessados em receber o evento; Permitindo outros objetos se inscreverem nessa lista, e receber o evento;
	
	@FXML
	private TextField txtId; // TextField significa entrar com os dados;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate; // Para data � utilizado o DatePicker, que � o nome daquele calend�riozinho para escolher uma data;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment; // ComboBox � um recurso para quando clicando, aparecerem todos os departamentos para escolher, ao criar um novo vendedor por exemplo;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	private ObservableList<Department> obsList; // Lista criada para guardar os departamentos;
	
	public void setSeller(Seller entity) { // Dessa forma, o controlador tem uma inst�ncia do vendedor;
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService) { // Inje��o de depend�ncia tanto de vendedor, quanto departamento;
		this.service = service;
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) { // M�todo que vai inscrever o listener na lista, simplesmente o adicionando;
		dataChangeListeners.add(listener); // Outros objetos desde que eles implementam a interface, pode se inscrever pra receber o evento da classe;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) { // Programa��o defensiva, testando se o programador esqueceu de injetar um entity;
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) { // Estamos fznd manualmente a inje��o, e n�o usando um fremework para tal, portanto, � bom colocar;
			throw new IllegalStateException("Service was null");
		}
		try { // J� que a opera��o de salvar ou atualizar usa BD, pode gerar uma exce��o;
			entity = getFormData();
			service.saveOrUpdate(entity); // Salvou no banco de dados;
			notifyDataChangeListeners(); // Ap�s o salvamento, notificaremos (emiss�o do evento - Subject) os listeners que a lista foi atualizada;
			Utils.currentStage(event).close(); // Pegando refer�ncia da janela atual, ap�s o evento (a��o), e fechar ela;
		}
		catch (ValidationException e) { // O try vai chamar o getFormData, onde pode gerar um ValidationException;
			setErrorMessages(e.getErrors()); // Cole��o de erros; Impress�o da mensagem de erro;
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() { // Este m�todo vai fzer a notifica��o que a lista tem que ser atualizada;
		for (DataChangeListener listener : dataChangeListeners) { // For para notificar cada um dos listeners;
			listener.onDataChanged();
		}
	}

	private Seller getFormData() { // Fun��o respons�vel por pegar os dados preenchidos nas caixinhas do formul�rio, e carregar (instanciar) um objeto com esses dados (vendedor) retornando no final;
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error"); // Apenas instanciando uma exce��o;
		
		obj.setId(Utils.tryParseToInt(txtId.getText())); // getText t� no formato de String, ent�o, usamos a fun��o que fizemos p converter p inteiro, lembrando que se n�o for um n�mero inteiro, retorna nulo;
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) { // trim para eliminar qualquer campo com espa�o em branco no come�o ou final, validando a partir do equals, se for igual ao espa�o vazio, quer dizer q t� vazio;
			exception.addError("name", "Field can't be empty"); // Lan�ando uma exce��o, para caso o campo nome seja vazio;
		}
		obj.setName(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}
		else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault())); // Forma de pegar um valor no DatePicker, convertendo a data escolhida no hor�rio do pc do usu�rio, para o instant que � uma data independentemente de localidade;
			obj.setBirthDate(Date.from(instant)); // Convertendo um instant, para Date, pois BirthDate � tipo Date;
		}
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText())); // Convers�o de String(txtBaseSalary) para Double; 
		
		obj.setDepartment(comboBoxDepartment.getValue()); // Setando o departamento do ComboBox para o objeto; Vai setar o departamento na hora de criar um novo vendedor;
		
		if (exception.getErrors().size() > 0) { // Testando se na cole��o de erros tem pelo menos um erro;
			throw exception; // Se isso for verdade, lan�ar� a exce��o; Se n�o tiver nenhum erro para lan�ar, vai retornar o objeto;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close(); // Pegando refer�ncia da janela atual, ap�s o evento (a��o), e fechar ela;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() { // Aqui colocaremos algumas restri��es;
		Constraints.setTextFieldInteger(txtId); // Definindo que o campo id � do tipo Integer, chamando o m�todo j� existente;
		Constraints.setTextFieldMaxLength(txtName, 70); // M�ximo 70 caracteres no nome do vendedor;
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy"); // Definindo o formato pra data no DatePicker;
		
		initializeComboBoxDepartment(); // Inicializar o comboBox;
	}
	
	public void updateFormData() {
		if (entity == null) { // Programa��o defensiva, caso o programador esque�a de inserir um id;
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity.getId())); // Pega os dados do objeto e joga nas caixinhas do formul�rio, ou seja, jogando a entidade(no caso vendedor) no txtId; Caixinha de texto ela trabalha com String, ent�o convertemos (String.valueOf) o valor inteiro, pra String;
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary())); // Convertendo o campo, pois a caixinha � String, e BaseSalary � double;
		if (entity.getBirthDate() != null) { // Prote��o para converter a data para LocalDate se ela n�o for nula somente;
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); // BirthDate � um objeto do tipo java.util.Date, mas o datepicker trabalha com o LocalDate, pois no computador mostra a data no computador do usu�rio, utilizando o systemDefault;
		}
		if (entity.getDepartment() == null) { // Testando se � um vendedor novo que est� cadastrando, ent�o ele n�o vai ter departamento ainda;
			comboBoxDepartment.getSelectionModel().selectFirst(); // Definindo o primeiro elemento para o ComboBox;
		}
		else {
			comboBoxDepartment.setValue(entity.getDepartment()); // O departamento que estiver associado com o vendedor (entity) vai pra comboBox; Ou seja, o m�todo pega os dados do objeto, e preenche o formul�rio com esses dados (vendedor);
		}
	}
	
	public void loadAssociatedObjects() { // Objetos associados -> Departamentos;
		if (departmentService == null) { // Programa��o defensiva pro departmentService;
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = departmentService.findAll(); // Carregar os departamentos que est�o no BD;
		obsList = FXCollections.observableArrayList(list); // Preenchendo a lista ObsList com os departamentos;
		comboBoxDepartment.setItems(obsList); // Associando a lista com o ComboBox;
	}
	
	private void setErrorMessages(Map<String, String> errors) { // M�todo respons�vel por pegar cada poss�vel erro que est�o na exce��o, e escrev�-los na tela, preenchendo as msgs no Label;
		Set<String> fields = errors.keySet(); // Outra estrutura de dados para setar os campos; Conjunto de erros;
		
		if (fields.contains("name")) { // Testando se existe um erro com a chave name do vendedor;
			labelErrorName.setText(errors.get("name")); // Se existir, setar o texto dele, a essa msg de erro no Label; Msg de erro caso tente colocar um vendedor sem nome por exemplo;
		}
		else {
			labelErrorName.setText("");
		}
		
		// Operador tern�rio para n�o ficar todos os 4 campos com if e else; No caso, os 3 abaixo � a msm coisa que o name acima;
		
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : "")); // Interroga��o significa se a condi��o for verdadeira, e ap�s isso, coloca o campo na frente, setando o nome do campo; E dois pontos significa falsa e a condi��o na frente;
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}	
}

// Formul�rio para preencher os dados do vendedor;