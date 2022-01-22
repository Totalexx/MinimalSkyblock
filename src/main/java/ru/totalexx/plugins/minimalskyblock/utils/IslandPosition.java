package ru.totalexx.plugins.minimalskyblock.utils;

public class IslandPosition {

    public enum NextDirection {
        RIGHT {
            public IslandPosition.NextDirection rotateDirection() {
                return DOWN;
            }
        },
        DOWN {
            public IslandPosition.NextDirection rotateDirection() {
                return LEFT;
            }
        },
        LEFT {
            public IslandPosition.NextDirection rotateDirection() {
                return TOP;
            }
        },
        TOP {
            public IslandPosition.NextDirection rotateDirection() {
                return RIGHT;
            }
        };

        public abstract IslandPosition.NextDirection rotateDirection();
    }

    public int id;
    public int x;
    public int z;
    public NextDirection nextDirection;

    public IslandPosition(int id, int x, int z) {
        this.id = id;
        this.x = x;
        this.z = z;
    }

    public IslandPosition(int id, int x, int z, NextDirection nextDirection) {
        this(id, x, z);
        this.nextDirection = nextDirection;
    }
}