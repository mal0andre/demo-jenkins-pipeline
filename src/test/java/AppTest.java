import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
	@Test
	void testAdd() {
		assertEquals(3, App.add(1, 2));
	}
}