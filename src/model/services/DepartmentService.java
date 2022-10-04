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
	
	public void saveOrUpdate(Department obj) { // Verificar se tem que inserir um novo departamento ou atualizar um já existente;
		if (obj.getId() == null) { // Ou seja, se for nulo, quer dizer que não tem nd, e iremos inserir um novo no BD;
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Department obj) { // Remover um departamento do banco de dados;
		dao.deleteById(obj.getId());
	}
}

// Dados mocados (Mock), são dados de "mentira", e não do banco de dados; Dados criados falsos, para retornar algo;