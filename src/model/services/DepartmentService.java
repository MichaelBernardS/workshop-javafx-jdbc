package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll() { // Método que retorna uma lista de departamentos, com os dados mocados;
		return dao.findAll();
	}
}

// Dados mocados (Mock), são dados de "mentira", e não do banco de dados; Dados criados falsos, para retornar algo;