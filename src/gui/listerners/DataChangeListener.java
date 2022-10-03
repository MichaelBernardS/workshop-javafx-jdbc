package gui.listerners;

public interface DataChangeListener { // Interface que permite um objeto a escutar um evento de outro objeto;
	
	void onDataChanged(); // Evento pra ser emitido, quando os eventos mudarem;
}

// A partir do padrão Observer, onde tem a ideia de evento, quando acontecer alguma atualização na tabela, vai emitir um evento para a tela de listagem, que é o Observer;
// Forma de comunicar dois objetos de uma forma altamente desacoplada, pois o objeto que emite o evento, ele não conhece o objeto que está escutando o evento dele;