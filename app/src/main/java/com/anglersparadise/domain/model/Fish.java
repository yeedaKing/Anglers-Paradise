// app/src/main/java/com/anglersparadise/domain/model/Fish.java
package com.anglersparadise.domain.model;

import java.util.Objects;

public class Fish {
    private long id;
    private String species;     // default "Common Fish"
    private int size;           // 1..5
    private long caughtAt;      // epoch ms

    public Fish(long id, String species, int size, long caughtAt) {
        this.id = id;
        this.species = (species != null) ? species : "Common Fish";
        this.size = size;
        this.caughtAt = caughtAt;
    }

    public long getId() { return id; }
    public String getSpecies() { return species; }
    public int getSize() { return size; }
    public long getCaughtAt() { return caughtAt; }

    public void setId(long id) { this.id = id; }
    public void setSpecies(String species) { this.species = species; }
    public void setSize(int size) { this.size = size; }
    public void setCaughtAt(long caughtAt) { this.caughtAt = caughtAt; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fish)) return false;
        Fish fish = (Fish) o;
        return id == fish.id;
    }

    @Override public int hashCode() { return Objects.hash(id); }
}
