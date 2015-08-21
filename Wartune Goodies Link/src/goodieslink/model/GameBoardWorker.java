package goodieslink.model;

import java.awt.Point;
import java.util.Set;
import java.util.concurrent.Callable;

import goodieslink.processing.Square;
import goodieslink.processing.matching.RegionMatcher;
import goodieslink.processing.matching.SimilarityResult;

/**
 * Worker for multithreaded region similarity processing in game board.
 * Calculates similarity rating between two regions using the given
 * RegionMatcher
 * 
 * @author Jonathan Schram
 *
 */
public class GameBoardWorker implements Callable<SimilarityResult> {
	/**
	 * Source grid location
	 */
	private Point source;
	/**
	 * Destination grid location
	 */
	private Point dest;
	/**
	 * Icon ID of source location
	 */
	private int sourceId;
	/**
	 * Square representing source square on grid
	 */
	private Square s1;
	/**
	 * Square representing destination square on grid
	 */
	private Square s2;
	/**
	 * Object for calculating region similarity
	 */
	private RegionMatcher measure;
	/**
	 * Optimization technique to skip similarity calculation if this source ID
	 * is known not to be similar
	 */
	Set<Integer> nonMatchingSet;

	/**
	 * 
	 * Creates a new GameBoardWorker with specified parameters
	 * 
	 * @param source
	 *            Source location in grid
	 * @param dest
	 *            Destination location in grid
	 * @param sourceId
	 *            Icon ID of source grid location
	 * @param s1
	 *            Square corresponding to source grid location
	 * @param s2
	 *            Square corresponding to destination grid location
	 * @param measure
	 *            RegionMatcher to calculate square similarity
	 * @param nonMatchingSet
	 *            Set to store icon IDs that are known to not be matches
	 */
	public GameBoardWorker(Point source, Point dest, int sourceId, Square s1, Square s2, RegionMatcher measure,
			Set<Integer> nonMatchingSet) {
		this.source = source;
		this.dest = dest;
		this.sourceId = sourceId;
		this.s1 = s1;
		this.s2 = s2;
		this.measure = measure;
		this.nonMatchingSet = nonMatchingSet;
	}

	@Override
	public SimilarityResult call() throws Exception {
		double similarity;
		if (!nonMatchingSet.contains(sourceId)) {
			similarity = measure.similarity(s1, s2);
		} else {
			similarity = Double.MAX_VALUE;
		}
		SimilarityResult sr = new SimilarityResult(source, dest, sourceId);
		sr.setSimilarity(similarity);
		return sr;
	}
}
