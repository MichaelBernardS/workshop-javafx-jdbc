package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> errors = new HashMap<>(); // Declarado um Map, para guardar os erros de cada campo do formul�rio da forma chave e valor, ou seja, para campo tal, erro tal, em pares;
	
	public ValidationException(String msg) { // Exce��o para validar um formul�rio, esta exce��o vai carregar as msgs de erro do formul�rio caso existam;
		super(msg); // For�ar a instancia��o da exce��o com String, passando pra superclasse esta mensagem;
	}
	
	public Map<String, String> getErrors() {
		return errors;
	}
	
	public void addError(String fieldName, String errorMessage) { // M�todo para permitir que adicione um elemento nesta cole��o, 1 - Nome do campo, 2 - Msg de erro;
		errors.put(fieldName, errorMessage);
	}
}