package gui.listerners;

public interface DataChangeListener { // Interface que permite um objeto a escutar um evento de outro objeto;
	
	void onDataChanged(); // Evento pra ser emitido, quando os eventos mudarem;
}

// A partir do padr�o Observer, onde tem a ideia de evento, quando acontecer alguma atualiza��o na tabela, vai emitir um evento para a tela de listagem, que � o Observer;
// Forma de comunicar dois objetos de uma forma altamente desacoplada, pois o objeto que emite o evento, ele n�o conhece o objeto que est� escutando o evento dele;