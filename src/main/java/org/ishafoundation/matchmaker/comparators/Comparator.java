package org.ishafoundation.matchmaker.comparators;

/**
 * An operator which compares two values for similarity, and returns a
 * number in the range 0.0 to 1.0 indicating the degree of similarity.
 */
public interface Comparator {

  /**
   * Returns true if the comparator breaks string values up into
   * tokens when comparing. Necessary because this impacts indexing of
   * values.
   */
  
  public double score(String v1, String v2);
  
  public String explainScore(String v1, String v2);
  
  public String toString();
  
}