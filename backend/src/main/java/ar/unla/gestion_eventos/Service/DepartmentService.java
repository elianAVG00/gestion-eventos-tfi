package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.Department;
import ar.unla.gestion_eventos.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> findDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    public Optional<Department> updateDepartment(Long id, Department updatedDepartment) {
        return departmentRepository.findById(id).map(department -> {
            department.setName(updatedDepartment.getName());
            department.setColorHex(updatedDepartment.getColorHex());
            department.setActive(updatedDepartment.isActive());
            return departmentRepository.save(department);
        });
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}