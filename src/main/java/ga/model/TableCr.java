package ga.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 06/03/17.
 */
public class TableCr implements Chromosome {
  public List<List<TableGuest>> tables = new ArrayList<>();

  private final List<TableGuest> totalGuests;
  private int tableSize;

  public TableCr(List<TableGuest> totalGuests) {
    this.totalGuests = totalGuests;
  }

  public void initRandom(int tables) {
    this.tables.clear();
    ArrayList<TableGuest> guests = new ArrayList<>(totalGuests);
    Collections.shuffle(guests);
    tableSize = totalGuests.size() / tables;
    if (tableSize != Math.floor(tableSize)) throw new RuntimeException("Table size not integer.");
    for (int i = 0; i < tables; i++) {
      this.tables.add(new ArrayList<>(guests.subList(tableSize * i, tableSize * (i + 1))));
    }
  }

  @Override
  public double fitness() {
    if (!totalPresent()) return 0;

    double sum = 0;
    for (List<TableGuest> t : tables) {
      sum += TableGuest.variance(t);
    }
    return sum / tables.size();
  }

  @Override
  public TableCr mutate() {
    TableCr child = new TableCr(totalGuests);
    child.tables = new ArrayList<>(tables);

    List<TableGuest> t1 = tables.get((int) (Math.random() * tables.size()));
    List<TableGuest> t2 = null;
    do {
      t2 = tables.get((int) (Math.random() * tables.size()));
    } while (t1 == t2);

    TableGuest g1 = t1.get((int) (Math.random() * t1.size()));
    TableGuest g2 = t2.get((int) (Math.random() * t2.size()));
    t1.remove(g1);
    t2.remove(g2);
    t1.add(g2);
    t2.add(g1);
    return child;
  }

  @Override
  public TableCr cross(Chromosome o) {
    if (!(o instanceof TableCr)) throw new RuntimeException("Type mismatch");
    TableCr other = (TableCr) o;
    if (tables.size() != other.tables.size()) throw new RuntimeException("Assumes equal table sizes.");

    int size = tables.size() / 2;
    ArrayList<List<TableGuest>> left = new ArrayList<>(other.tables.subList(0, size));
    ArrayList<List<TableGuest>> right = new ArrayList<>(tables.subList(size, tables.size()));

    // TODO: avoid invalid children
//    List<TableGuest> duplicates = flatten(other.tables.subList(size, tables.size()));
//    for (int i = 0; i < tables.size(); i++) {
//      for (int j = 0; j < tableSize; j++) {
//        TableGuest g = tables.get(i).get(j);
//        if (duplicates.contains(g)) {
//
//        }
//      }
//    }

    TableCr child = new TableCr(totalGuests);
    child.tables = left;
    left.addAll(right);
    return child;
  }

  private List<TableGuest> flatten(List<List<TableGuest>> lists) {
    ArrayList<TableGuest> results = new ArrayList<>();
    for (List<TableGuest> t : lists) {
      results.addAll(t);
    }
    return results;
  }

  private boolean totalPresent() {
    for (List<TableGuest> t : tables) {
      for (TableGuest g : t) {
        if (!totalGuests.contains(g)) return false;
      }
    }
    return true;
  }

  public void print() {
    System.out.println("Table ordering result: " + fitness());
  }
}
