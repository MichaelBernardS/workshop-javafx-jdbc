package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) { // Palco atual, com o evento que o bot�o recebeu;
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); // Acessar o Stage onde o controller que recebeu o controle, est�, por exemplo, apertando um bot�o, pega o Stage daquele bot�o; getSoure � do tipo object, por isso fizemos um downcasting pra Node, e a janela � superclasse do Stage, por isso o downcasting;
	}
}