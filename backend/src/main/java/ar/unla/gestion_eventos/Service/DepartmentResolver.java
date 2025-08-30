package ar.unla.gestion_eventos.Service;

import ar.unla.gestion_eventos.Domain.Department;
import ar.unla.gestion_eventos.Repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DepartmentResolver {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentResolver(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Optional<Department> resolveDepartment(String glpiName) {
        if (glpiName == null || glpiName.trim().isEmpty()) {
            return null;
        }

        String normalizedName = normalizeString(glpiName);
        return Optional.ofNullable(departmentRepository.findByName(normalizedName).orElse(null));
    }

    private String normalizeString(String text) {
        return text.toLowerCase().trim();
    }
}