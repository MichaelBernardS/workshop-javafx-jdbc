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
	
	public static Stage currentStage(ActionEvent event) { // Palco atual, com o evento que o bot�o recebeu;
		return (Stage) ((Node) event.getSource()).getScene().getWindow(); // Acessar o Stage onde o controller que recebeu o controle, est�, por exemplo, apertando um bot�o, pega o Stage daquele bot�o; getSoure � do tipo object, por isso fizemos um downcasting pra Node, e a janela � superclasse do Stage, por isso o downcasting;
	}
	
	public static Integer tryParseToInt(String str) { // M�todo que ajuda a converter o valor da caixinha para inteiro;
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {
			return null; // N�o ter risco de ter exce��o, ele l� o n�mero ou retorna nulo;
		}
	}
	
	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) { // M�todo utilit�rio para formatar a Data nas colunas;
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
	
	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) { // M�todo utilit�rio para formatar n�meros com pontos flutuantes nas colunas;
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