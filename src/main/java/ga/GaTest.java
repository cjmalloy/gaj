package ga;

import ga.engine.Engine;
import ga.model.TableCr;
import ga.model.TableGuest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 06/03/17.
 */
public class GaTest {

  static int population = 400;
  static int guests = 100;

  public static void main(String[] args) {
    List<TableGuest> totalGuests = new ArrayList<>();
    for (int i = 0; i < guests; i++) {
      TableGuest g = new TableGuest(i);
      g.initRandom();
      totalGuests.add(g);
    }
    List<TableCr> initialSet = new ArrayList<>();
    for (int i = 0; i < population; i++) {
      TableCr cr = new TableCr(totalGuests);
      cr.initRandom(10);
      initialSet.add(cr);
    }
    Engine<TableCr> engine = new Engine<>(initialSet);
    engine.run(1000);
    engine.getBest().print();
  }
}
