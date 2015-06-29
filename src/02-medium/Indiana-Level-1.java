import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Player {
    static Direction TOP = Direction.TOP;
    static Direction LEFT = Direction.LEFT;
    static Direction RIGHT = Direction.RIGHT;
    static Direction DOWN = Direction.DOWN;
    static Map<Coordinate, RoomType> grid;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int W = in.nextInt(); // number of columns.
        int H = in.nextInt(); // number of rows.
        in.nextLine();
        grid = new HashMap<Coordinate, RoomType>(W * H);
        for (int i = 0; i < H; i++) {
            String[] LINE = in.nextLine().split(" ");
            // represents a line in the grid and contains W integers. Each integer represents one room of a given type.
            for (int j = 0; j < W; j++) {
                grid.put(new Coordinate(j, i), RoomType.byType(Integer.parseInt(LINE[j])));
            }
        }
        int EX = in.nextInt(); // the coordinate along the X axis of the exit (not useful for this first mission, but must be read).
        in.nextLine();

        // game loop
        while (true) {
            int XI = in.nextInt();
            int YI = in.nextInt();
            String POS = in.next();
            Direction enter = Direction.valueOf(POS);
            Coordinate c = new Coordinate(XI, YI);
            in.nextLine();

            System.out.println(getNextCoordinate(c, enter)); // One line containing the X Y coordinates of the room in which you believe Indy will be on the next turn.
        }
    }

    private static Coordinate getNextCoordinate(Coordinate coordinate, Direction enterDirection) {
        Direction exitDirection = getExitDirection(coordinate, enterDirection);
        switch (exitDirection) {
            case RIGHT:
                coordinate.x++;
                break;
            case LEFT:
                coordinate.x--;
                break;
            case DOWN:
                coordinate.y++;
                break;
        }
        return coordinate;
    }

    private static Direction getExitDirection(Coordinate coordinate, Direction enterDirection) {
        Path[] paths = grid.get(coordinate).paths;
        int i = 0;
        while (paths.length > i && paths[i].enter != enterDirection) {
            i++;
        }
        return paths[i].exit;
    }

    static class Coordinate {
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public String toString() {
            return x + " " + y;
        }
    }

    enum RoomType {
        /* 0*/BLOCK(new Path[]{}),
        /* 1*/CROSSROAD(new Path[]{Path.TOP_DOWN, Path.RIGHT_DOWN, Path.LEFT_DOWN}),
        /* 2*/STRAIGHT_HORIZONTAL(new Path[]{Path.LEFT_RIGHT, Path.RIGHT_LEFT}),
        /* 3*/STRAIGHT_VERTICAL(new Path[]{Path.TOP_DOWN}),
        /* 4*/CORNERS_TOP_LEFT_RIGHT_DOWN(new Path[]{Path.TOP_LEFT, Path.RIGHT_DOWN}),
        /* 5*/CORNERS_TOP_RIGHT_LEFT_DOWN(new Path[]{Path.TOP_RIGHT, Path.LEFT_DOWN}),
        /* 6*/T_UP(new Path[]{Path.LEFT_RIGHT, Path.RIGHT_LEFT}),
        /* 7*/T_RIGHT(new Path[]{Path.TOP_DOWN, Path.RIGHT_DOWN}),
        /* 8*/T_DOWN(new Path[]{Path.LEFT_DOWN, Path.RIGHT_DOWN}),
        /* 9*/T_LEFT(new Path[]{Path.LEFT_DOWN, Path.TOP_DOWN}),
        /*10*/CORNER_TOP_LEFT(new Path[]{Path.TOP_LEFT}),
        /*11*/CORNER_TOP_RIGHT(new Path[]{Path.TOP_RIGHT}),
        /*12*/CORNER_DOWN_RIGHT(new Path[]{Path.RIGHT_DOWN}),
        /*13*/CORNER_DOWN_LEFT(new Path[]{Path.LEFT_DOWN});

        Path[] paths;

        RoomType(Path[] paths) {
            this.paths = paths;
        }

        public static RoomType byType(int type) {
            return RoomType.values()[type];
        }
    }

    enum Path {
        TOP_DOWN(TOP, DOWN),
        TOP_LEFT(TOP, LEFT),
        TOP_RIGHT(TOP, RIGHT),
        LEFT_DOWN(LEFT, DOWN),
        LEFT_RIGHT(LEFT, RIGHT),
        RIGHT_DOWN(RIGHT, DOWN),
        RIGHT_LEFT(RIGHT, LEFT);

        Direction enter;
        Direction exit;

        Path(Direction enter, Direction exit) {
            this.enter = enter;
            this.exit = exit;
        }
    }

    enum Direction {
        TOP,
        RIGHT,
        DOWN,
        LEFT;
    }
}