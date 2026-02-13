package by.senla.errorfreetext.repository;

import by.senla.errorfreetext.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    Task getTaskById(UUID id);
}