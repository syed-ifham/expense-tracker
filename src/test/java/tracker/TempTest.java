package tracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TempTest {
    @Test
    public void test(){
        assertTrue(Files.exists(Path.of("temp/phone.db")));
    }
}
