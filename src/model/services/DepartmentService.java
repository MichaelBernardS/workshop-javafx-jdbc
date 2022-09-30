package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll() { // M�todo que retorna uma lista de departamentos, com os dados mocados;
		return dao.findAll();
	}
}

// Dados mocados (Mock), s�o dados de "mentira", e n�o do banco de dados; Dados criados falsos, para retornar algo;