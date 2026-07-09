package tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {
    @GetMapping
    public ResponseEntity<?> checkHealth() {
        return ResponseEntity.ok("I am OK");
    }

}
