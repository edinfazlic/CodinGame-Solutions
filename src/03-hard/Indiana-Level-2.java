import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Player {

    static int W; // number of columns.
    static int H; // number of rows.
    static int EX; // the coordinate along the X axis of the exit.
    static Map<Coordinate, Room> grid;
    static String answer;
    static int unnecessaryCalls = 0;
    static Direction TOP = Direction.TOP;
    static Direction LEFT = Direction.LEFT;
    static Direction DOWN = Direction.DOWN;
    static Direction RIGHT = Direction.RIGHT;

    public static void main(String args[]) {
        long time = System.currentTimeMillis();

        Scanner in = new Scanner(System.in);
        System.err.println("Created Scanner: " + (System.currentTimeMillis() - time) + "ms");
        W = in.nextInt();
        H = in.nextInt();
        System.err.println("Scanned for (int) W and H: " + (System.currentTimeMillis() - time) + "ms");
        in.nextLine();

        grid = new HashMap<Coordinate, Room>(W * H);
        for (int i = 0; i < H; i++) {
            String[] LINE = in.nextLine().split(" ");
            // each line represents a line in the grid and contains W integers T.
            // The absolute value of T specifies the type of the room. If T is negative, the room cannot be rotated.
            for (int j = 0; j < W; j++) {
                grid.put(new Coordinate(j, i), new Room(Integer.parseInt(LINE[j])));
            }
        }

        EX = in.nextInt();
        in.nextLine();

        int XI = in.nextInt();
        int YI = in.nextInt();
        String POSI = in.next(); // Indy entrance point into the room TOP, LEFT or RIGHT

        Coordinate fromCoordinate = new Coordinate(XI, YI);
        Path startingPath = getStartingLeaveDirection(fromCoordinate, POSI);
        goFrom(fromCoordinate, startingPath, 1, "", -1);
        System.err.println("Done calculations: " + (System.currentTimeMillis() - time) + "ms");
        System.err.println("Unnecessary calls: " + unnecessaryCalls);

        String[] commands = answer.split(";");
        adjustGrid(commands);

        String rockAnswer = solveCrossroads(fromCoordinate, startingPath);
        String[] rockCommands = rockAnswer.split(";");
        System.err.println("Rocks: " + rockAnswer);

        int answerCounter = 0;
        int rockStart = 0;
        int previousDistance = -1;
        if (!rockCommands[answerCounter].equals("")) {
            while (answerCounter < commands.length) {
                String command = commands[answerCounter];
                String[] details = command.split(" ");

                int numberOfElements = Integer.valueOf(details[3]) - previousDistance - 1;
                for (int i = 0; i < numberOfElements && rockStart + i < rockCommands.length; i++) {
                    System.err.println("SP numberOfElements: " + numberOfElements + " previousDistance:" + previousDistance);
                    System.out.println(rockCommands[rockStart + i]);
                }
                rockStart += numberOfElements;
                previousDistance = Integer.valueOf(details[3]);
                System.err.println("GL det3: " + Integer.valueOf(details[3]) + " previousDistance:" + previousDistance);
                System.out.println(details[0] + " " + details[1] + " " + details[2]);
                answerCounter++;
            }
        } else {


            // game loop
            answerCounter = 0;
            while (answerCounter < commands.length) {
                String[] details = commands[answerCounter++].split(" ");
                System.out.println(details[0] + " " + details[1] + " " + details[2]);
            }
        }
//        answerCounter = 0;
//        if(!rockCommands[answerCounter].equals("")) {
//            while (answerCounter < rockCommands.length) {
//                System.out.println(rockCommands[answerCounter++]);
//            }
//        }
        while (true) {
            /*XI = in.nextInt();
            YI = in.nextInt();
            POSI = in.next();
            in.nextLine();
            int R = in.nextInt(); // the number of rocks currently in the grid.
            in.nextLine();
            for (int i = 0; i < R; i++) {
                int XR = in.nextInt();
                int YR = in.nextInt();
                String POSR = in.next(); // rock entrance point into the room
                in.nextLine();
            }*/
            System.out.println("WAIT"); // One line containing on of three commands: 'X Y LEFT', 'X Y RIGHT' or 'WAIT'
        }
    }

    private static void adjustGrid(String[] commands) {
        for (int i = 0; i < commands.length; i++) {
            String[] details = commands[i].split(" ");
            Room current = grid.get(new Coordinate(Integer.valueOf(details[0]), Integer.valueOf(details[1])));
            Room newRoom;
            if (details[2].equals("LEFT")) {
                newRoom = new Room(current.type.left, LEFT);
            } else {
                if (i != 0 && commands[i].equals(commands[i - 1])) {
                    newRoom = new Room(current.type.right, Direction.RIGHT_RIGHT);
                } else {
                    newRoom = new Room(current.type.right, RIGHT);
                }
            }
            grid.put(new Coordinate(Integer.valueOf(details[0]), Integer.valueOf(details[1])), newRoom);
        }
    }

    private static Path getStartingLeaveDirection(Coordinate coordinate, String posi) {
        Path[] paths = grid.get(coordinate).type.paths;
        Direction indiana = Direction.valueOf(posi);
        int i = 0;
        while (paths.length > i && paths[i].enter != indiana) {
            i++;
        }
        return paths[i];
    }

    private static void goFrom(Coordinate fromCoordinate, Path path, int rotationsLeft, String result, int distance) {
        if (null != answer) {
            unnecessaryCalls++;
            return;
        }
        if (fromCoordinate.y == H - 1 && fromCoordinate.x == EX) {
            answer = result;
            System.err.println("Result: " + result);
            return;
        }
        Coordinate nextCoordinate = new Coordinate(fromCoordinate.x, fromCoordinate.y);
        Direction nextRoomEntrance = calculateEntrance(nextCoordinate, path.leave);
        if (nextCoordinate.x < 0 || nextCoordinate.x > W - 1 || nextCoordinate.y > H - 1) {
            return;
        }
        distance++;
        for (Position rotatedRoom : findPossibleRotations(nextCoordinate, nextRoomEntrance, ++rotationsLeft, distance)) {
            goFrom(nextCoordinate, rotatedRoom.path, rotationsLeft - rotatedRoom.numberOfRotations, result + rotatedRoom.rotateText, distance);
        }
    }

    private static String solveCrossroads(Coordinate coordinate, Path startingPath) {
        Room room = grid.get(coordinate);
        String result = "";
//        if(null != room.rotation) {
//            if(room.rotation == Direction.RIGHT_RIGHT) {
//                result += coordinate.x + " " + coordinate.y + " RIGHT;" + coordinate.x + " " + coordinate.y + " RIGHT;";
//            } else {
//                result += coordinate.x + " " + coordinate.y + " " + room.rotation + ";";
//            }
//        }
        if (isCrossroad(room.type)) {
            for (Direction entrance : findOtherEntrances(room.type, startingPath)) {
                result += tryRotate(coordinate, entrance);
            }
        }

        Direction entrance = calculateEntrance(coordinate, startingPath.leave);
        Room nextRoom = grid.get(coordinate);
        if (nextRoom == null) {
            return result;
        }
        Path nextPath = null;
        for (Path path : nextRoom.type.paths) {
            if (path.enter == entrance) {
                nextPath = path;
                break;
            }
        }
        return result + solveCrossroads(coordinate, nextPath);
    }

    static boolean isCrossroad(RoomType roomType) {
        return roomType == RoomType.CROSSROAD || roomType == RoomType.T_DOWN || roomType == RoomType.T_LEFT || roomType == RoomType.T_UP || roomType == RoomType.T_RIGHT;
    }

    private static String tryRotate(Coordinate previousCoordinate, Direction exit) {
        Coordinate coordinate = new Coordinate(previousCoordinate.x, previousCoordinate.y);
        Direction entrance = calculateEntrance(coordinate, exit);
        Room room = grid.get(coordinate);
        if (null == room) {
            return "";
        }
        if (room.rotatable && room.type != RoomType.CROSSROAD) {
            boolean needToRotate = needToRotate(room.type.paths, entrance);
            if (!needToRotate) {
                return "";
            }
            needToRotate = needToRotate(RoomType.byType(room.type.left).paths, entrance);
            if (!needToRotate) {
                return coordinate.x + " " + coordinate.y + " LEFT;";
            }
            needToRotate = needToRotate(RoomType.byType(room.type.right).paths, entrance);
            if (!needToRotate) {
                return coordinate.x + " " + coordinate.y + " RIGHT;";
            }
            return "";
        } else {
            String result = "";
            for (Path path : room.type.paths) {
                if (path.leave == entrance) {
                    result += tryRotate(coordinate, path.enter);
                }
            }
            return result;
        }
    }

    static boolean needToRotate(Path[] paths, Direction match) {
        for (Path path : paths) {
            if (path.leave == match) {
                return true;
            }
        }
        return false;
    }

    private static List<Direction> findOtherEntrances(RoomType roomType, Path indyPath) {
        List<Direction> entrances = new ArrayList<Direction>(2);
        for (Path path : roomType.paths) {
            if (path.enter != indyPath.enter && path.enter != indyPath.leave) {
                entrances.add(path.enter);
            }
        }
        return entrances;
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
                return null; // will not happen
        }
    }

    static List<Position> findPossibleRotations(Coordinate c, Direction enter, int rotationsLeft, int distance) {
        List<Position> rotatedRooms = new ArrayList<Position>();
        Room room = grid.get(c);
        addRotatedRoom(rotatedRooms, room.type, enter, 0, ""); // set for not rotated room
        if (!room.rotatable || rotationsLeft == 0) {
            return rotatedRooms;
        }

        RoomType rightRoom = RoomType.byType(room.type.right);
        addRotatedRoom(rotatedRooms, rightRoom, enter, 1, c.x + " " + c.y + " RIGHT " + distance + ";"); // set for RIGHT rotated room
        RoomType leftRoom = RoomType.byType(room.type.left);
        addRotatedRoom(rotatedRooms, leftRoom, enter, 1, c.x + " " + c.y + " LEFT " + distance + ";"); // set for LEFT rotated room
        if (rotationsLeft == 1) {
            return rotatedRooms;
        }

        addRotatedRoom(rotatedRooms, RoomType.byType(rightRoom.right), enter, 2, c.x + " " + c.y + " RIGHT " + distance + ";" + c.x + " " + c.y + " RIGHT " + distance + ";"); // get for twice RIGHT rotated room
        return rotatedRooms;
    }

    static void addRotatedRoom(List<Position> rotatedRooms, RoomType roomType, Direction enter, int rotations, String text) {
        for (Path path : roomType.paths) {
            if (path.enter == enter) {
                rotatedRooms.add(new Position(roomType, path, rotations, text));
            }
        }
    }

    /*static class Rotation {
        Coordinate coordinate;
        Direction direction;

        public Rotation(Coordinate coordinate, Direction direction) {
            this.coordinate = coordinate;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return coordinate.x + " " + coordinate.y + " " + direction;
        }
    }*/

    static class Position {
        RoomType roomType;
        Path path;
        int numberOfRotations; // 0, 1, 2
        String rotateText;

        public Position(RoomType roomType, Path path, int numberOfRotations, String rotateText) {
            this.roomType = roomType;
            this.path = path;
            this.numberOfRotations = numberOfRotations;
            this.rotateText = rotateText;
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

        int left;
        int right;
        Path[] paths;

        RoomType(int left, int right, Path[] paths) {
            this.left = left;
            this.right = right;
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
        Direction leave;

        Path(Direction enter, Direction leave) {
            this.enter = enter;
            this.leave = leave;
        }
    }

    enum Direction {
        TOP,
        RIGHT,
        DOWN,
        LEFT,
        RIGHT_RIGHT;
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
        RoomType type;
        boolean rotatable = true;
        Direction rotation;

        public Room(int roomType) {
            if (roomType < 0) {
                rotatable = false;
                roomType *= -1;
            }
            this.type = RoomType.byType(roomType);
        }

        public Room(int roomType, Direction rotation) {
            this(roomType);
            this.rotation = rotation;
        }
    }
}