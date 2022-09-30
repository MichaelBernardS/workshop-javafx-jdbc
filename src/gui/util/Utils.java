package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) { // Palco atual, com o evento que o botão recebeu;
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); // Acessar o Stage onde o controller que recebeu o controle, está, por exemplo, apertando um botão, pega o Stage daquele botão; getSoure é do tipo object, por isso fizemos um downcasting pra Node, e a janela é superclasse do Stage, por isso o downcasting;
	}
}