package com.example.demo.worker;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers() {
        List<Worker> workers = workerService.getAllWorkers();
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable Long id) {
        Worker worker = workerService.getWorkerById(id);
        return ResponseEntity.ok(worker);
    }

    @PostMapping
    public ResponseEntity<Worker> createWorker(@RequestBody Worker worker) {
        // Basic validation
        if (worker.getName() == null || worker.getPhone() == null || worker.getDailyWage() == null) {
            throw new RuntimeException("Name, Phone and Daily Wage are required");
        }
        Worker savedWorker = workerService.saveWorker(worker);
        return new ResponseEntity<>(savedWorker, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Worker> updateWorker(@PathVariable Long id, @RequestBody Worker workerDetails) {
        Worker updatedWorker = workerService.updateWorker(id, workerDetails);
        return ResponseEntity.ok(updatedWorker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.noContent().build();
    }

    // Additional useful endpoint for attendance system
    @GetMapping("/active")
    public ResponseEntity<List<Worker>> getActiveWorkersOnly() {
        List<Worker> activeWorkers = workerService.getActiveWorkers();
        return ResponseEntity.ok(activeWorkers);
    }
}