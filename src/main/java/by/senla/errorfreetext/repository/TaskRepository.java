package by.senla.errorfreetext.repository;

import by.senla.errorfreetext.model.entity.Task;
import by.senla.errorfreetext.model.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    Task getTaskById(UUID id);

    List<Task> findTasksByStatus(Status status);
}