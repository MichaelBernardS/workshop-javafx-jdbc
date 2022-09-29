package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {
	
	public List<Department> findAll() { // Método que retorna uma lista de departamentos, com os dados mocados;
		List<Department> list = new ArrayList<>();
		list.add(new Department (1, "Books"));
		list.add(new Department (2, "Computers"));
		list.add(new Department (3, "Electronics"));
		return list;
	}
}

// Dados mocados (Mock), são dados de "mentira", e não do banco de dados; Dados criados falsos, para retornar algo;