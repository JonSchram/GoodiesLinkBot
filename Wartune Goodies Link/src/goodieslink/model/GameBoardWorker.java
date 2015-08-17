package goodieslink.model;

import java.awt.Point;
import java.util.Set;
import java.util.concurrent.Callable;

import goodieslink.processing.Square;
import goodieslink.processing.matching.RegionMatcher;
import goodieslink.processing.matching.SimilarityResult;

public class GameBoardWorker implements Callable<SimilarityResult> {
	private Point source;
	private Point dest;
	private int sourceId;
	private Square s1;
	private Square s2;
	private RegionMatcher measure;
	Set<Integer> nonMatchingSet;

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
