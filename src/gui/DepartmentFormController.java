package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
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
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
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