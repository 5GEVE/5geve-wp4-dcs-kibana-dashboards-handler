package com.telcaria.dcs.storage.enums;

public enum GraphType {
  LINE ("line"),
  PIE ("pie"),
  COUNTER("metric"),
  GAUGE("gauge");

  private final String name;

  private GraphType(String s) {
    name = s;
  }

  public boolean equalsName(String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns false
    return name.equals(otherName);
  }

  public String toString() {
    return this.name;
  }
}

