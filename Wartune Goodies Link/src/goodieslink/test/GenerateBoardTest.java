package goodieslink.test;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GenerateBoardTest {

	public static void main(String[] args) {
		int[][] ids = { { 1, 2, 3, 4, 5, 6, 1, 3, 7, 8, 9, 10 }, { 1, 11, 2, 12, 13, 4, 4, 10, 9, 14, 15, 16 },
				{ 8, 17, 16, 18, 6, 19, 16, 20, 2, 5, 7, 21 }, { 10, 15, 9, 16, 4, 1, 19, 9, 22, 23, 9, 14 },
				{ 4, 24, 2, 10, 18, 25, 4, 26, 11, 17, 25, 22 }, { 24, 9, 19, 21, 20, 2, 26, 13, 12, 2, 19, 23 } };

		HashMap<Integer, HashSet<Point>> matches = new HashMap<>();

		for (int i = 0; i < ids.length; i++) {
			for (int j = 0; j < ids[i].length; j++) {
				int id = ids[i][j];
				if (!matches.containsKey(id)) {
					matches.put(id, new HashSet<Point>());
				}
				matches.get(id).add(new Point(j, i));
			}
		}

		// test ids that should be equal
		Set<Integer> keys = matches.keySet();
		for (int key : keys) {
			Set<Point> locations = matches.get(key);
			Iterator<Point> locationIterator = locations.iterator();
			Point previous = locationIterator.next();
			while (locationIterator.hasNext()) {
				Point current = locationIterator.next();
				System.out.println("assertEquals(gb.getSquareId(" + previous.y + "," + previous.x + "), gb.getSquareId("
						+ current.y + "," + current.x + "));");
				previous = current;
			}
		}

		// test ids that should NOT be equal
		Iterator<Integer> keyIterator = keys.iterator();
		int firstId = keyIterator.next();
		Point firstIdLocation = matches.get(firstId).iterator().next();
		while (keyIterator.hasNext()) {
			int currentId = keyIterator.next();
			Point comparisonLocation = matches.get(currentId).iterator().next();
			System.out.println("assertNotEquals(gb.getSquareId(" + firstIdLocation.y + "," + firstIdLocation.x
					+ "), gb.getSquareId(" + comparisonLocation.y + "," + comparisonLocation.x + "));");
		}
	}

}
