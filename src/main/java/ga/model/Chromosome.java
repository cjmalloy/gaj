package ga.model;

/**
 * Created by chris on 06/03/17.
 */
public interface Chromosome {
  double fitness();
  Chromosome mutate();
  Chromosome cross(Chromosome other);
}
