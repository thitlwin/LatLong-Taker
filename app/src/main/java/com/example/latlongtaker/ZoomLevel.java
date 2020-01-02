package com.example.latlongtaker;

public enum ZoomLevel {
    World(1),
    Landmass_continent(5),
    City(10),
    Streets(15),
    Buildings(20);

    int value;

    ZoomLevel(int value) {
        this.value = value;
    }
}
