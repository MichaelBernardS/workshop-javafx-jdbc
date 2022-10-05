package gui.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) { // Palco atual, com o evento que o botão recebeu;
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); // Acessar o Stage onde o controller que recebeu o controle, está, por exemplo, apertando um botão, pega o Stage daquele botão; getSoure é do tipo object, por isso fizemos um downcasting pra Node, e a janela é superclasse do Stage, por isso o downcasting;
	}
	
	public static Integer tryParseToInt(String str) { // Método que ajuda a converter o valor da caixinha para inteiro;
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null; // Não ter risco de ter exceção, ele lê o número ou retorna nulo;
		}
	}
	
	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) { // Método utilitário para formatar a Data nas colunas;
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);
				
				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} 
					else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}
	
	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) { // Método utilitário para formatar números com pontos flutuantes nas colunas;
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					}
					else {
						Locale.setDefault(Locale.US);
						setText(String.format("%." + decimalPlaces + "f", item));
					}
				}
			};
			return cell;
		});
	}
}