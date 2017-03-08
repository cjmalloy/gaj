package ga;

import ga.engine.Engine;
import ga.model.TableCr;
import ga.model.TableGuest;

import java.util.ArrayList;
import java.util.List;

/**
 * Test driver for solving a table seating problem.
 */
public class GaTest {

  /**
   * Number of chromosomes.
   */
  static int population = 400;

  /**
   * Number of guests in seating plan.
   */
  static int guests = 100;

  /**
   * Number of tables in seating plan.
   */
  static int tables = 10;

  /**
   * Number of generation in this test.
   */
  static int generations = 1000;

  public static void main(String[] args) {
    // Init random guest list
    List<TableGuest> totalGuests = new ArrayList<>();
    for (int i = 0; i < guests; i++) {
      TableGuest g = new TableGuest(i);
      g.initRandom();
      totalGuests.add(g);
    }
    List<TableCr> initialSet = new ArrayList<>();
    for (int i = 0; i < population; i++) {
      TableCr cr = new TableCr(totalGuests);
      cr.initRandom(tables);
      initialSet.add(cr);
    }
    Engine<TableCr> engine = new Engine<>(initialSet);
    engine.run(generations);
    engine.getBest().print();
  }
}
