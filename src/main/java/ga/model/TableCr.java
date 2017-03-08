package ga.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Chromosome for solving a table seating problem.
 */
public class TableCr implements Chromosome {

  /**
   * Data representation for this chromosome. Each chromosome represents a complete
   * seating solution.
   */
  public List<List<TableGuest>> tables = new ArrayList<>();

  /**
   * List of all guests. Each guest should be seated exactly once.
   */
  private List<TableGuest> totalGuests;

  /**
   * The number of guests per table. Calculated by dividing the number of guests
   * by the number of tables.
   */
  private int tableSize;

  public TableCr(List<TableGuest> totalGuests) {
    this.totalGuests = totalGuests;
  }

  /**
   * Copy constructor.
   */
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

  /**
   * @inheritDoc
   */
  @Override
  public double fitness() {
    if (!totalPresent()) return 0;

    double sum = 0;
    for (List<TableGuest> t : tables) {
      sum += TableGuest.variance(t);
    }
    return -sum / tables.size();
  }

  /**
   * @inheritDoc
   */
  @Override
  public TableCr mutate() {
    TableCr child = new TableCr(this);

    List<TableGuest> t1 = child.tables.get((int) (Math.random() * tables.size()));
    List<TableGuest> t2 = null;
    do {
      t2 = child.tables.get((int) (Math.random() * tables.size()));
    } while (t1 == t2);

    TableGuest g1 = t1.get((int) (Math.random() * t1.size()));
    TableGuest g2 = t2.get((int) (Math.random() * t2.size()));
    t1.remove(g1);
    t2.remove(g2);
    t1.add(g2);
    t2.add(g1);
    return child;
  }

  /**
   * @inheritDoc
   */
  @Override
  public TableCr cross(Chromosome o) {
    if (!(o instanceof TableCr)) throw new RuntimeException("Type mismatch");
    TableCr other = (TableCr) o;
    if (tables.size() != other.tables.size()) throw new RuntimeException("Assumes equal table sizes.");

    // Take half a table from each chromosome
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

  /**
   * Check if this chromosome contains a table.
   * Assumes the tables are sorted.
   *
   * @param search the table to search for
   * @return true if the table is present
   */
  public boolean containsTable(List<TableGuest> search) {
    for (List<TableGuest> table : tables) {
      // Requires sorted tables
      if (table.equals(search)) return true;
    }
    return false;
  }

  /**
   * Move guests to a table by re-arranging so no duplicates are made.
   *
   * @param toTable the table to move to
   * @param move the guests to move
   */
  private void moveToTable(List<TableGuest> toTable, List<TableGuest> move) {
    List<TableGuest> missing = new ArrayList<>();
    for (TableGuest g : move) {
      if (!toTable.contains(g)) {
        missing.add(g);
      }
    }
    List<TableGuest> remove = grabRandom(toTable, missing.size(), missing);
    for (int i = 0; i < remove.size(); i++) {
      swap(missing.get(i), remove.get(i));
    }
  }

  /**
   * Swap the seating of two guests.
   */
  private void swap(TableGuest a, TableGuest b) {
    List<TableGuest> t1 = findTable(a);
    List<TableGuest> t2 = findTable(b);
    t1.remove(a);
    t1.add(b);
    t2.remove(b);
    t2.add(a);
  }

  /**
   * Find the table a guest is seated at.
   */
  private List<TableGuest> findTable(TableGuest a) {
    for (List<TableGuest> table : tables) {
      if (table.contains(a)) return table;
    }
    return null;
  }

  /**
   * Grab random set of guests from a table.
   *
   * @param table the table to grab from
   * @param size the number of guests to grab
   * @param excluded list of guests to avoid picking
   * @return a random selection of guests from the table
   */
  private List<TableGuest> grabRandom(List<TableGuest> table, int size, List<TableGuest> excluded) {
    List<TableGuest> remove = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      TableGuest g;
      do {
        g = table.get((int) (Math.random() * table.size()));
      } while (excluded.contains(g) || remove.contains(g));
    }
    return remove;
  }

  /**
   * Check if all the guests are present.
   * @return true if all guests are present
   */
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
