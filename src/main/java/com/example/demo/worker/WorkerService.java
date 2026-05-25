package com.example.demo.worker;

import com.example.demo.worker.exception.WorkerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Worker getWorkerById(Long id) {
        return workerRepository.findById(id)
                .orElseThrow(() -> new WorkerNotFoundException("Worker not found with id: " + id));
    }

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public Worker updateWorker(Long id, Worker workerDetails) {
        Worker existingWorker = getWorkerById(id);

        existingWorker.setName(workerDetails.getName());
        existingWorker.setPhone(workerDetails.getPhone());
        existingWorker.setDesignation(workerDetails.getDesignation());
        existingWorker.setDailyWage(workerDetails.getDailyWage());
        existingWorker.setActive(workerDetails.isActive());

        return workerRepository.save(existingWorker);
    }

    public void deleteWorker(Long id) {
        Worker worker = getWorkerById(id);
        worker.setActive(false);           // Soft delete
        workerRepository.save(worker);
    }

    public List<Worker> getActiveWorkers() {
        return workerRepository.findAll().stream()
                .filter(Worker::isActive)
                .toList();
    }
}