import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Player {

    static int W; // number of columns.
    static int H; // number of rows.
    static int EX; // the coordinate along the X axis of the exit.
    static Map<Coordinate, Room> grid;
    static final Direction TOP = Direction.TOP;
    static final Direction LEFT = Direction.LEFT;
    static final Direction DOWN = Direction.DOWN;
    static final Direction RIGHT = Direction.RIGHT;

    public static void main(String args[]) {
        long startTime = System.currentTimeMillis();
        Scanner in = new Scanner(System.in);

        initWidthHeight(startTime, in);
        initGrid(in);
        initExitXCoordinate(in);

        Coordinate initialCoordinate = getInitialCoordinate(in);
        String posI = in.next(); // Indy entrance point into the room TOP, LEFT or RIGHT
        Path initialPath = getInitialPath(initialCoordinate, posI);
        List<Command> commands = goFrom(initialCoordinate, initialPath, 1, -1); // commands for rotating rooms of FIRST found path
        Collections.reverse(commands);

        System.err.println("Done calculations: " + (System.currentTimeMillis() - startTime) + "ms");

        adjustGrid(commands);

        List<Command> rockCommands = solveCrossroads(initialCoordinate, initialPath);

        logCommands(commands, "Indy Commands:");
        logCommands(rockCommands, "Rock Commands:");

        if (rockCommands.isEmpty()) {
            outputWithoutConsideringRocks(commands);
        } else {
            outputWithRockIntersections(commands, rockCommands);
        }
        outputWaitTillTheEnd();
    }

    //<editor-fold desc="Initialization">
    private static void initWidthHeight(long startTime, Scanner in) {
        System.err.println("Created Scanner: " + (System.currentTimeMillis() - startTime) + "ms");
        W = in.nextInt();
        H = in.nextInt();
        System.err.println("Scanned for (int) W and H: " + (System.currentTimeMillis() - startTime) + "ms");
        in.nextLine();
    }

    private static void initGrid(Scanner in) {
        grid = new HashMap<Coordinate, Room>(W * H);
        for (int i = 0; i < H; i++) {
            String[] LINE = in.nextLine().split(" ");
            // each line represents a line in the grid and contains W integers T.
            // The absolute value of T specifies the type of the room. If T is negative, the room cannot be rotated.
            for (int j = 0; j < W; j++) {
                grid.put(new Coordinate(j, i), new Room(Integer.parseInt(LINE[j])));
            }
        }
    }

    private static void initExitXCoordinate(Scanner in) {
        EX = in.nextInt();
        in.nextLine();
    }

    private static Coordinate getInitialCoordinate(Scanner in) {
        int XI = in.nextInt();
        int YI = in.nextInt();
        return new Coordinate(XI, YI);
    }

    private static Path getInitialPath(Coordinate coordinate, String indianaEntrance) {
        Path[] paths = grid.get(coordinate).roomType.paths;
        return getPathMatchingEntrance(paths, Direction.valueOf(indianaEntrance));
    }
    //</editor-fold>

    //<editor-fold desc="Finding Indiana's path">
    private static List<Command> goFrom(Coordinate coordinate, Path path, int rotationsRemaining, int distanceFromStart) {
        Coordinate nextCoordinate = new Coordinate(coordinate.x, coordinate.y);
        Direction nextRoomEntrance = calculateEntrance(nextCoordinate, path.exit);
        if (isOutOfBounds(nextCoordinate)) {
            return null;
        }
        if (isExitReached(nextCoordinate)) { // end of recursion
            return new ArrayList<Command>(); // initial command list is empty because last room cannot be rotated
        }
        for (RotateOption option : findPossibleRotations(nextCoordinate, nextRoomEntrance, ++rotationsRemaining, distanceFromStart + 1)) {
            List<Command> commands = goFrom(nextCoordinate, option.path, rotationsRemaining - option.numberOfRotations, distanceFromStart + 1);
            if (null != commands) { // goal has been reached, end of recursion occurred
                commands.addAll(option.rotateCommands);
                return commands;
            }
        }
        return null;
    }

    private static boolean isOutOfBounds(Coordinate c) {
        return c.x < 0 || c.x > W - 1 || c.y > H - 1;
    }

    static List<RotateOption> findPossibleRotations(Coordinate c, Direction entrance, int rotationsRemaining, int distanceFromStart) {
        List<RotateOption> rotateOptions = new ArrayList<RotateOption>();
        Room room = grid.get(c);
        List<Command> command = new ArrayList<Command>(0);
        addRotateOption(rotateOptions, room.roomType.paths, entrance, 0, command); // set for not rotated room
        if (!room.rotatable || rotationsRemaining == 0) {
            return rotateOptions;
        }

        RoomType rightRoom = RoomType.byId(room.roomType.rightRotatedId);
        command = new ArrayList<Command>(1);
        Command rotateRightCommand = new Command(c, RIGHT, distanceFromStart);
        command.add(rotateRightCommand);
        addRotateOption(rotateOptions, rightRoom.paths, entrance, 1, command); // set for RIGHT rotated room

        RoomType leftRoom = RoomType.byId(room.roomType.leftRotatedId);
        command = new ArrayList<Command>(1);
        command.add(new Command(c, LEFT, distanceFromStart));
        addRotateOption(rotateOptions, leftRoom.paths, entrance, 1, command); // set for LEFT rotated room

        if (--rotationsRemaining == 0) {
            return rotateOptions;
        }

        command = new ArrayList<Command>(2);
        command.add(rotateRightCommand);
        command.add(rotateRightCommand);
        addRotateOption(rotateOptions, RoomType.byId(rightRoom.rightRotatedId).paths, entrance, 2, command); // set for twice RIGHT rotated room
        return rotateOptions;
    }

    static void addRotateOption(List<RotateOption> rotateOptions, Path[] roomPaths, Direction entrance, int rotations, List<Command> commands) {
        Path path = getPathMatchingEntrance(roomPaths, entrance);
        if (path != null) {
            rotateOptions.add(new RotateOption(path, rotations, commands));
        }
    }
    //</editor-fold>

    private static void adjustGrid(List<Command> commands) {
        for (Command command : commands) {
            Room room = grid.get(command.coordinate);
            if (command.direction == LEFT) {
                grid.put(command.coordinate, new Room(room.roomType.leftRotatedId));
            } else {
                grid.put(command.coordinate, new Room(room.roomType.rightRotatedId));
            }
        }
    }

    //<editor-fold desc="From every crossroad on indy's path rotate first room possible so he cannot come to intersection with rocks">
    private static List<Command> solveCrossroads(Coordinate coordinate, Path indyPath) {
        Room room = grid.get(coordinate);
        List<Command> result = new ArrayList<Command>();

        if (isCrossroad(room.roomType)) {
            for (Direction entrance : findOtherEntrances(room.roomType, indyPath)) {
                result.addAll(tryRotate(coordinate, entrance)); // other entrances are actually exits from that room
            }
        }

        Coordinate nextCoordinate = new Coordinate(coordinate.x, coordinate.y);
        Direction entrance = calculateEntrance(nextCoordinate, indyPath.exit);
        if(isExitReached(nextCoordinate)) {
            return result;
        }

        Path nextPath = getPathMatchingEntrance(grid.get(nextCoordinate).roomType.paths, entrance);
        result.addAll(solveCrossroads(nextCoordinate, nextPath));
        return result;
    }

    static boolean isCrossroad(RoomType roomType) {
        return roomType == RoomType.CROSSROAD || roomType == RoomType.T_DOWN || roomType == RoomType.T_LEFT || roomType == RoomType.T_RIGHT;
    }

    private static List<Direction> findOtherEntrances(RoomType roomType, Path indyPath) {
        List<Direction> entrances = new ArrayList<Direction>(2);
        for (Path path : roomType.paths) {
            if (path.entrance != indyPath.entrance) {
                entrances.add(path.entrance);
            }
        }
        return entrances;
    }

    private static List<Command> tryRotate(Coordinate previousCoordinate, Direction leavingDirection) {
        Coordinate coordinate = new Coordinate(previousCoordinate.x, previousCoordinate.y);
        Direction entrance = calculateEntrance(coordinate, leavingDirection);
        Room room = grid.get(coordinate);
        if (null == room) {
            return new ArrayList<Command>(0);
        }
        if (room.rotatable && room.roomType != RoomType.CROSSROAD) {
            boolean needToRotate = needToRotate(room.roomType.paths, entrance);
            if (!needToRotate) {
                return new ArrayList<Command>(0);
            }
            needToRotate = needToRotate(RoomType.byId(room.roomType.leftRotatedId).paths, entrance);
            if (!needToRotate) {
                List<Command> result = new ArrayList<Command>(1);
                result.add(new Command(coordinate, LEFT));
                return result;
            }
            needToRotate = needToRotate(RoomType.byId(room.roomType.rightRotatedId).paths, entrance);
            if (!needToRotate) {
                List<Command> result = new ArrayList<Command>(1);
                result.add(new Command(coordinate, RIGHT));
                return result;
            }
            System.err.println("There is a T_DOWN that could be rotated twice to avoid intersection with indy @" + coordinate.x + ":" + coordinate.y);
            return new ArrayList<Command>(0);
        } else {
            List<Command> result = new ArrayList<Command>();
            for (Path path : room.roomType.paths) {
                if (path.exit == entrance) {
                    result.addAll(tryRotate(coordinate, path.entrance));
                }
            }
            return result;
        }
    }

    static boolean needToRotate(Path[] paths, Direction match) {
        for (Path path : paths) {
            if (path.exit == match) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Helper methods">
    private static Path getPathMatchingEntrance(Path[] paths, Direction entrance) {
        for (Path path : paths) {
            if (path.entrance == entrance) {
                return path;
            }
        }
        return null;
    }

    private static Direction calculateEntrance(Coordinate next, Direction leavingDirection) {
        switch (leavingDirection) {
            case RIGHT:
                next.x++;
                return LEFT;
            case LEFT:
                next.x--;
                return RIGHT;
            case DOWN:
                next.y++;
                return TOP;
            default:
                next.y--;
                return DOWN;
        }
    }

    private static boolean isExitReached(Coordinate c) {
        return c.y == H - 1 && c.x == EX;
    }
    //</editor-fold>

    //<editor-fold desc="Output">
    private static void outputWithoutConsideringRocks(List<Command> commands) {
        for (Command command : commands) {
            System.out.println(command);
        }
    }

    private static void outputWithRockIntersections(List<Command> commands, List<Command> rockCommands) {
        int previousDistance = -1;
        int rockCommandsIterator = 0;
        for(Command command : commands) {
            int elementsToFit = command.distance - previousDistance - 1;
            for(int i = 0; rockCommandsIterator < rockCommands.size() && i < elementsToFit; rockCommandsIterator++, i++) {
                System.out.println(rockCommands.get(rockCommandsIterator));
            }
            previousDistance = command.distance;
            System.out.println(command);
        }
    }

    private static void outputWaitTillTheEnd() {
        while (true) {
            System.out.println("WAIT"); // One line containing on of three commands: 'X Y LEFT', 'X Y RIGHT' or 'WAIT'
        }
    }

    private static void logCommands(List<Command> commands, String title) {
        for(Player.Command command: commands) {
            System.err.println(title + " " + command + " " + command.distance);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Classes and enums">
    static class Command {
        Coordinate coordinate;
        Direction direction;
        int distance;

        public Command(Coordinate coordinate, Direction direction, int distance) {
            this.coordinate = coordinate;
            this.direction = direction;
            this.distance = distance;
        }

        public Command(Coordinate coordinate, Direction direction) {
            this.coordinate = coordinate;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return coordinate.x + " " + coordinate.y + " " + direction;
        }
    }

    static class RotateOption {
        Path path;
        int numberOfRotations; // 0, 1, 2
        List<Command> rotateCommands;

        public RotateOption(Path path, int numberOfRotations, List<Command> rotateCommands) {
            this.path = path;
            this.numberOfRotations = numberOfRotations;
            this.rotateCommands = rotateCommands;
        }
    }

    enum RoomType {
        /* 0*/BLOCK(0, 0, new Path[]{}),
        /* 1*/CROSSROAD(0, 0, new Path[]{Path.TOP_DOWN, Path.RIGHT_DOWN, Path.LEFT_DOWN}),
        /* 2*/STRAIGHT_HORIZONTAL(3, 3, new Path[]{Path.LEFT_RIGHT, Path.RIGHT_LEFT}),
        /* 3*/STRAIGHT_VERTICAL(2, 2, new Path[]{Path.TOP_DOWN}),
        /* 4*/CORNERS_TOP_LEFT_RIGHT_DOWN(5, 5, new Path[]{Path.TOP_LEFT, Path.RIGHT_DOWN}),
        /* 5*/CORNERS_TOP_RIGHT_LEFT_DOWN(4, 4, new Path[]{Path.TOP_RIGHT, Path.LEFT_DOWN}),
        /* 6*/T_UP(9, 7, new Path[]{Path.LEFT_RIGHT, Path.RIGHT_LEFT}),
        /* 7*/T_RIGHT(6, 8, new Path[]{Path.TOP_DOWN, Path.RIGHT_DOWN}),
        /* 8*/T_DOWN(7, 9, new Path[]{Path.LEFT_DOWN, Path.RIGHT_DOWN}),
        /* 9*/T_LEFT(8, 6, new Path[]{Path.LEFT_DOWN, Path.TOP_DOWN}),
        /*10*/CORNER_TOP_LEFT(13, 11, new Path[]{Path.TOP_LEFT}),
        /*11*/CORNER_TOP_RIGHT(10, 12, new Path[]{Path.TOP_RIGHT}),
        /*12*/CORNER_DOWN_RIGHT(11, 13, new Path[]{Path.RIGHT_DOWN}),
        /*13*/CORNER_DOWN_LEFT(12, 10, new Path[]{Path.LEFT_DOWN});

        int leftRotatedId;
        int rightRotatedId;
        Path[] paths;

        RoomType(int leftRotatedId, int rightRotatedId, Path[] paths) {
            this.leftRotatedId = leftRotatedId;
            this.rightRotatedId = rightRotatedId;
            this.paths = paths;
        }

        public static RoomType byId(int id) {
            return RoomType.values()[id];
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

        Direction entrance;
        Direction exit;

        Path(Direction entrance, Direction exit) {
            this.entrance = entrance;
            this.exit = exit;
        }
    }

    enum Direction {
        TOP,
        RIGHT,
        DOWN,
        LEFT
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
    }

    static class Room {
        RoomType roomType;
        boolean rotatable = true;

        public Room(int roomTypeId) {
            this.rotatable = roomTypeId >= 0;
            this.roomType = RoomType.byId(Math.abs(roomTypeId));
        }
    }
    //</editor-fold>
}