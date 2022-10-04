package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> errors = new HashMap<>(); // Declarado um Map, para guardar os erros de cada campo do formulário da forma chave e valor, ou seja, para campo tal, erro tal, em pares;
	
	public ValidationException(String msg) { // Exceção para validar um formulário, esta exceção vai carregar as msgs de erro do formulário caso existam;
		super(msg); // Forçar a instanciação da exceção com String, passando pra superclasse esta mensagem;
	}
	
	public Map<String, String> getErrors() {
		return errors;
	}
	
	public void addError(String fieldName, String errorMessage) { // Método para permitir que adicione um elemento nesta coleção, 1 - Nome do campo, 2 - Msg de erro;
		errors.put(fieldName, errorMessage);
	}
}