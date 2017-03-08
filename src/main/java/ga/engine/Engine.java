package ga.engine;

import ga.model.Chromosome;
import ga.model.TableCr;

import javax.print.DocPrintJob;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chris on 06/03/17.
 */
public class Engine<T extends Chromosome> {

  public double keepRatio = 0.5;
  public double mutateRatio = 0.1;

  private List<T> chromosomes;
  private final int size;
  private int generations;
  private int threshold;

  public Engine(List<T> initial) {
    this.chromosomes = initial;
    this.size = initial.size();
  }

  public void run(int generations, int threshold) {
    this.generations = generations;
    this.threshold = threshold;
    run();
  }

  public void run(int generations) {
    this.generations = generations;
    this.threshold = -1;
    run();
  }

  public void run() {
    int keep = (int) Math.ceil(keepRatio * size);
    int mutate = (int) Math.ceil(mutateRatio * size);
    for (int i = 0; i < generations; i++) {
      sort();
      System.out.println("Generation: " + i);
      System.out.println("Top fitness: " + chromosomes.get(0).fitness());
      chromosomes.subList(keep, chromosomes.size()).clear();
      List<T> best = new ArrayList<>(chromosomes);
      while (chromosomes.size() < size) {
        Chromosome c = generateOffspring(best);
        if (chromosomes.size() - keep < mutate) {
          c = c.mutate();
        }
        chromosomes.add((T) c);
      }
    }
  }

  private void sort() {
    chromosomes = chromosomes.stream().sorted((o1, o2) -> toInt(o2.fitness() - o1.fitness())).distinct().collect(Collectors.toList());
  }

  private T generateOffspring(List<T> best) {
    T p1 = best.get((int) (Math.random() * best.size()));
    T p2 = null;
    do {
      p2 = best.get((int) (Math.random() * best.size()));
    } while (p1 == p2);
    return (T) p1.cross(p2);
  }

  private static int toInt(double v) {
    if (v < 0) return -1;
    if (v > 0) return 1;
    return 0;
  }

  public T getBest() {
    sort();
    return chromosomes.get(0);
  }
}
