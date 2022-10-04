package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll() { // M�todo que retorna uma lista de departamentos, com os dados mocados;
		return dao.findAll();
	}
	
	public void saveOrUpdate(Seller obj) { // Verificar se tem que inserir um novo departamento ou atualizar um j� existente;
		if (obj.getId() == null) { // Ou seja, se for nulo, quer dizer que n�o tem nd, e iremos inserir um novo no BD;
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Seller obj) { // Remover um departamento do banco de dados;
		dao.deleteById(obj.getId());
	}
}

// Dados mocados (Mock), s�o dados de "mentira", e n�o do banco de dados; Dados criados falsos, para retornar algo;