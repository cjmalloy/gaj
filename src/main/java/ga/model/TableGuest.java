package ga.model;

import java.util.List;

/**
 * Represents a guest.
 */
public class TableGuest {

  public final int id;

  /**
   * -1 = liberal, 1 = conservative, 0 = don't care
   */
  public double politics = 0;

  public boolean vegan = false;

  public FoodChoice food = FoodChoice.CHICKEN;

  public enum FoodChoice {
    CHICKEN,
    STEAK,
    FISH
  }

  public TableGuest(int id) {
    this.id = id;
  }

  public void initRandom() {
    politics = Math.random() * 2 - 1;
    vegan = Math.random() < 0.5;
    food = FoodChoice.values()[(int) Math.floor(Math.random() * 3)];
  }

  public static double variance(List<TableGuest> guests) {
    return getPoliticsStdDev(guests) * 3 +
           getVeganStdDev(guests) * 2 +
           getFoodStdDev(guests);
  }

  private static double getPoliticsMean(List<TableGuest> guests) {
    double sum = 0.0;
    for(TableGuest a : guests)
      sum += a.politics;
    return sum / guests.size();
  }

  private static double getPoliticsVariance(List<TableGuest> guests) {
    double mean = getPoliticsMean(guests);
    double temp = 0;
    for(TableGuest a :guests)
      temp += Math.pow(a.politics - mean, 2);
    return temp/guests.size();
  }

  private static double getPoliticsStdDev(List<TableGuest> guests) {
    return Math.sqrt(getPoliticsVariance(guests));
  }

  private static double getVeganMean(List<TableGuest> guests) {
    double sum = 0.0;
    for(TableGuest a : guests)
      sum += a.vegan ? 1 : 0;
    return sum/guests.size();
  }

  private static double getVeganVariance(List<TableGuest> guests) {
    double mean = getVeganMean(guests);
    double temp = 0;
    for(TableGuest a : guests)
      temp += Math.pow((a.vegan ? 1 : 0) - mean, 2);
    return temp/guests.size();
  }

  private static double getVeganStdDev(List<TableGuest> guests) {
    return Math.sqrt(getVeganVariance(guests));
  }

  private static double getFoodMean(List<TableGuest> guests) {
    double sum = 0.0;
    for(TableGuest a : guests)
      sum += a.food.ordinal();
    return sum/guests.size();
  }

  private static double getFoodVariance(List<TableGuest> guests) {
    double mean = getFoodMean(guests);
    double temp = 0;
    for(TableGuest a : guests)
      temp += Math.pow(a.food.ordinal() - mean, 2);
    return temp/guests.size();
  }

  private static double getFoodStdDev(List<TableGuest> guests) {
    return Math.sqrt(getFoodVariance(guests));
  }
}
