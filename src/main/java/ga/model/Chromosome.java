package ga.model;

/**
 * Interface for a chromosome.
 */
public interface Chromosome {
  /**
   * @return the fitness. Larger is better.
   */
  double fitness();

  /**
   * Copy this chromosome and mutate the chromosome subtly.
   * @return a new chromosome
   */
  Chromosome mutate();

  /**
   * Copy this chromosome and cross it with another.
   * @return a new chromosome
   */
  Chromosome cross(Chromosome other);
}
