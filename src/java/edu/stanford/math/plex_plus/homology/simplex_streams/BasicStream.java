package edu.stanford.math.plex_plus.homology.simplex_streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.stanford.math.plex_plus.datastructures.pairs.DoubleGenericPair;
import edu.stanford.math.plex_plus.datastructures.pairs.DoubleOrderedIterator;
import edu.stanford.math.plex_plus.homology.simplex.ChainBasisElement;
import edu.stanford.math.plex_plus.utility.ExceptionUtility;
import gnu.trove.map.hash.TObjectDoubleHashMap;

/**
 * @author Andris
 *
 */
public abstract class BasicStream<T extends ChainBasisElement> implements SimplexStream<T> {
	/**
	 * This contains the simplicies of the complex ordered in order of filtration value.
	 */
	protected final List<DoubleGenericPair<T>> simplices = new ArrayList<DoubleGenericPair<T>>();

	/**
	 * This hash map contains the filtration values of the simplices in the complex.
	 */
	protected final TObjectDoubleHashMap<T> filtrationValues = new TObjectDoubleHashMap<T>();

	/**
	 * Comparator which provides ordering of elements of the stream.
	 */
	protected final Comparator<T> basisComparator;

	/**
	 * Boolean which indicates whether stream has been finalized or not
	 */
	protected boolean isFinalized = false;

	/**
	 * Stores the maximum dimension in the complex
	 */
	protected int dimension = 0;

	/**
	 * Stores the number of vertices in the complex.
	 */
	protected int numVertices = 0;

	/**
	 * Constructor which accepts a comparator for comparing the type T.
	 * This comparator defines the ordering on the type T. Thus the overall
	 * filtered objects are sorted first in order of filtration, and then by
	 * the ordering provided by the comparator. 
	 * 
	 * @param comparator a Comparator which provides an ordering of the objects
	 */
	protected BasicStream(Comparator<T> comparator) {
		this.basisComparator = comparator;
	}

	/**
	 * This function adds a simplex to the complex with given filtration value.
	 * 
	 * @param vertices the vertices of the simplex to add
	 * @param filtrationIndex the filtration value of the simplex
	 */
	protected void addSimplexInternal(T simplex, double filtrationIndex) {
		ExceptionUtility.verifyNonNull(simplex);

		if (this.isFinalized) {
			throw new IllegalStateException("Cannot add objects to finalized stream.");
		}

		this.simplices.add(new DoubleGenericPair<T>(filtrationIndex, simplex));
		this.filtrationValues.put(simplex, filtrationIndex);
		this.dimension = Math.max(this.dimension, simplex.getDimension());

		if (simplex.getDimension() == 0) {
			this.numVertices++;
		}
	}

	/**
	 * This function validates the stream to make sure that it
	 * contains a valid filtered simplicial complex. It checks the
	 * two following conditions:
	 * 1. For each simplex in the complex, all of the faces of the simplex
	 * also belong to the simplicial complex.
	 * 2. The faces of each simplex have filtration values that are
	 * less than or equal to those of its cofaces.
	 * 
	 * @return true if the stream is consistent, false otherwise
	 */
	public boolean validate() {	
		for (DoubleGenericPair<T> pair: this.simplices) {
			double filtrationValue = pair.getFirst();

			// get the boundary
			ChainBasisElement[] boundary = this.getBoundary(pair.getSecond());

			// make sure that each boundary element is also inside the
			// complex with a filtration value less than or equal to the
			// current simplex
			for (ChainBasisElement face: boundary) {

				// if the face is not in the complex, then the stream
				// is inconsistent
				if (!this.filtrationValues.contains(face)) {
					return false;
				}

				// if the face's filtration value is greater than that of the
				// current simplex, the stream is also inconsistent
				if (this.filtrationValues.get(face) > filtrationValue) {
					return false;
				}
			}
		}

		// all simplices in the complex have been checked - good, return true
		return true;
	}

	@Override
	public T[] getBoundary(T simplex) {
		return (T[]) simplex.getBoundaryArray();
	}

	@Override
	public int[] getBoundaryCoefficients(T simplex) {
		return simplex.getBoundaryCoefficients();
	}

	public Comparator<T> getBasisComparator() {
		return this.basisComparator;
	}

	@Override
	public Iterator<T> iterator() {
		return new DoubleOrderedIterator<T>(this.simplices);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (DoubleGenericPair<T> simplex : this.simplices) {
			builder.append(simplex.toString());
			builder.append('\n');
		}

		return builder.toString();
	}

	@Override
	public double getFiltrationValue(T simplex) {
		return this.filtrationValues.get(simplex);
	}

	@Override
	public boolean isFinalized() {
		return this.isFinalized;
	}

	@Override
	public int getDimension() {
		return this.dimension;
	}

	public abstract void finalizeStream();
}