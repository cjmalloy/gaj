package ga.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chris on 06/03/17.
 */
public class TableCr implements Chromosome {
  public List<List<TableGuest>> tables = new ArrayList<>();

  private List<TableGuest> totalGuests;
  private int tableSize;

  public TableCr(List<TableGuest> totalGuests) {
    this.totalGuests = totalGuests;
  }

  public TableCr(TableCr c) {
    this.totalGuests = c.totalGuests;
    this.tableSize = c.tableSize;
    for (List<TableGuest> table : c.tables) {
      tables.add(new ArrayList<>(table));
    }
  }

  public void initRandom(int tables) {
    this.tables.clear();
    ArrayList<TableGuest> guests = new ArrayList<>(totalGuests);
    Collections.shuffle(guests);
    tableSize = totalGuests.size() / tables;
    if (tableSize != Math.floor(tableSize)) throw new RuntimeException("Table size not integer.");
    for (int i = 0; i < tables; i++) {
      ArrayList<TableGuest> t = new ArrayList<>(guests.subList(tableSize * i, tableSize * (i + 1)));
      t.sort(Comparator.comparingInt(g -> g.id));
      this.tables.add(t);
    }
  }

  @Override
  public double fitness() {
    if (!totalPresent()) return 0;

    double sum = 0;
    for (List<TableGuest> t : tables) {
      sum += TableGuest.variance(t);
    }
    return -sum / tables.size();
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
    TableCr child = new TableCr(this);

    for (int i = 0; i < tables.size(); i++) {
      List<TableGuest> t1 = child.tables.get(i);
      List<TableGuest> t2 = other.tables.get(i);
      List<TableGuest> move = t2.subList(tableSize/2, tableSize);
      moveToTable(t1, move);
    }
    return child;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TableCr)) return false;
    TableCr other = (TableCr) o;

    for (List<TableGuest> table : tables) {
      if (!other.containsTable(table)) return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int n = 0;
    for (List<TableGuest> table : tables) {
      for (TableGuest g : table) {
        n += g.id;
      }
      n *= 10;
    }
    return n;
  }

  public boolean containsTable(List<TableGuest> search) {
    for (List<TableGuest> table : tables) {
      // Requires sorted tables
      if (table.equals(search)) return true;
    }
    return false;
  }

  private void moveToTable(List<TableGuest> toTable, List<TableGuest> move) {
    List<TableGuest> missing = new ArrayList<>();
    for (TableGuest g : move) {
      if (!toTable.contains(g)) {
        missing.add(g);
      }
    }
    List<TableGuest> remove = removeRandom(toTable, missing.size(), missing);
    for (int i = 0; i < remove.size(); i++) {
      swap(missing.get(i), remove.get(i));
    }
  }

  private void swap(TableGuest a, TableGuest b) {
    List<TableGuest> t1 = findTable(a);
    List<TableGuest> t2 = findTable(b);
    t1.remove(a);
    t1.add(b);
    t2.remove(b);
    t2.add(a);
  }

  private List<TableGuest> findTable(TableGuest a) {
    for (List<TableGuest> table : tables) {
      if (table.contains(a)) return table;
    }
    return null;
  }

  private List<TableGuest> removeRandom(List<TableGuest> table, int size, List<TableGuest> excluded) {
    List<TableGuest> remove = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      TableGuest g;
      do {
        g = table.get((int) (Math.random() * table.size()));
      } while (excluded.contains(g) || remove.contains(g));
    }
    return remove;
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
