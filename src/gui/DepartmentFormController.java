package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listerners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); // Classe vai guardar uma lista de objetos interessados em receber o evento; Permitindo outros objetos se inscreverem nessa lista, e receber o evento;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) { // Dessa forma, o controlador tem uma instância do Departamento;
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) { // Método que vai inscrever o listener na lista, simplesmente o adicionando;
		dataChangeListeners.add(listener); // Outros objetos desde que eles implementam a interface, pode se inscrever pra receber o evento da classe;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) { // Programação defensiva, testando se o programador esqueceu de injetar um entity;
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) { // Estamos fznd manualmente a injeção, e não usando um fremework para tal, portanto, é bom colocar;
			throw new IllegalStateException("Service was null");
		}
		try { // Já que a operação de salvar ou atualizar usa BD, pode gerar uma exceção;
			entity = getFormData();
			service.saveOrUpdate(entity); // Salvou no banco de dados;
			notifyDataChangeListeners(); // Após o salvamento, notificaremos (emissão do evento - Subject) os listeners que a lista foi atualizada;
			Utils.currentStage(event).close(); // Pegando referência da janela atual, após o evento (ação), e fechar ela;
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() { // Este método vai fzer a notificação que a lista tem que ser atualizada;
		for (DataChangeListener listener : dataChangeListeners) { // For para notificar cada um dos listeners;
			listener.onDataChanged();
		}
	}

	private Department getFormData() { // Função responsável por pegar os dados nas caixinhas do formulário, e instanciar um departamento;
		Department obj = new Department();
		
		obj.setId(Utils.tryParseToInt(txtId.getText())); // getText tá no formato de String, então, usamos a função que fizemos p converter p inteiro, lembrando que se não for um número inteiro, retorna nulo;
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close(); // Pegando referência da janela atual, após o evento (ação), e fechar ela;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() { // Aqui colocaremos algumas restrições;
		Constraints.setTextFieldInteger(txtId); // Só aceitar números no Id;
		Constraints.setTextFieldMaxLength(txtName, 30); // Máximo 30 caracteres no nome;
	}
	
	public void updateFormData() {
		if (entity == null) { // Programação defensiva, caso o programador esqueça de inserir um id;
			throw new IllegalStateException("Entity was null");
		}
		
		txtId.setText(String.valueOf(entity)); // Caixinha de texto ela trabalha com String, então convertemos (String.valueOf) o valor inteiro, pra String;
		txtName.setText(entity.getName());
	}
	
}