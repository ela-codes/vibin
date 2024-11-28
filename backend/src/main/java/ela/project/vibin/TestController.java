package ela.project.vibin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestEntityRepository repository;

    @GetMapping
    public List<TestEntity> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public TestEntity create(@RequestBody TestEntity entity) {
        return repository.save(entity);
    }
}
