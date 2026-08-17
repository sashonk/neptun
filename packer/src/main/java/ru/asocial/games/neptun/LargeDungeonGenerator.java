package ru.asocial.games.neptun;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates a fixed-size dungeon CSV compatible with {@code MapGenerator}.
 */
public class LargeDungeonGenerator {

    private static final int OPEN = 0;
    private static final int CLOSED = 1;
    private static final int G_OPEN = 2;
    private static final int G_CLOSED = 3;
    private static final int IR_OPEN = 8;
    private static final int IT_OPEN = 9;
    private static final int IA_OPEN = 10;

    private final int width;
    private final int height;
    private final int[][] cells;
    private final Random random;

    public LargeDungeonGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.cells = new int[height][width];
        this.random = new Random(seed);
    }

    public void generate() {
        fill(CLOSED);
        carveBorder(G_CLOSED);

        int areaScale = Math.max(1, (width * height) / (150 * 150));
        List<Room> rooms = placeRooms(70 * areaScale, 6, 16);
        connectRooms(rooms);
        addExtraTunnels(120 * areaScale);
        markOpenAreas();
        addExitAreas(areaScale);
    }

    private void fill(int value) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = value;
            }
        }
    }

    private void carveBorder(int value) {
        for (int x = 0; x < width; x++) {
            cells[0][x] = value;
            cells[height - 1][x] = value;
        }
        for (int y = 0; y < height; y++) {
            cells[y][0] = value;
            cells[y][width - 1] = value;
        }
    }

    private List<Room> placeRooms(int attempts, int minSize, int maxSize) {
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < attempts; i++) {
            int rw = minSize + random.nextInt(maxSize - minSize + 1);
            int rh = minSize + random.nextInt(maxSize - minSize + 1);
            int rx = 2 + random.nextInt(Math.max(1, width - rw - 4));
            int ry = 2 + random.nextInt(Math.max(1, height - rh - 4));
            Room room = new Room(rx, ry, rw, rh);
            if (overlapsExisting(rooms, room)) {
                continue;
            }
            rooms.add(room);
            for (int y = ry; y < ry + rh; y++) {
                for (int x = rx; x < rx + rw; x++) {
                    cells[y][x] = IR_OPEN;
                }
            }
        }
        return rooms;
    }

    private boolean overlapsExisting(List<Room> rooms, Room candidate) {
        for (Room room : rooms) {
            if (room.overlaps(candidate, 2)) {
                return true;
            }
        }
        return false;
    }

    private void connectRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return;
        }
        for (int i = 1; i < rooms.size(); i++) {
            Room from = rooms.get(i - 1);
            Room to = rooms.get(i);
            carveCorridor(from.centerX(), from.centerY(), to.centerX(), to.centerY());
        }
        for (int i = 0; i < 8; i++) {
            Room from = rooms.get(random.nextInt(rooms.size()));
            Room to = rooms.get(random.nextInt(rooms.size()));
            carveCorridor(from.centerX(), from.centerY(), to.centerX(), to.centerY());
        }
    }

    private void carveCorridor(int x1, int y1, int x2, int y2) {
        int x = x1;
        int y = y1;
        while (x != x2) {
            setWalkable(x, y, IT_OPEN);
            x += x < x2 ? 1 : -1;
        }
        while (y != y2) {
            setWalkable(x, y, IT_OPEN);
            y += y < y2 ? 1 : -1;
        }
        setWalkable(x, y, IT_OPEN);
    }

    private void addExtraTunnels(int count) {
        for (int i = 0; i < count; i++) {
            int x = 2 + random.nextInt(width - 4);
            int y = 2 + random.nextInt(height - 4);
            int dir = random.nextInt(4);
            int length = 4 + random.nextInt(18);
            for (int step = 0; step < length; step++) {
                setWalkable(x, y, random.nextBoolean() ? IT_OPEN : IA_OPEN);
                switch (dir) {
                    case 0: x++; break;
                    case 1: x--; break;
                    case 2: y++; break;
                    default: y--; break;
                }
                if (x <= 1 || y <= 1 || x >= width - 2 || y >= height - 2) {
                    break;
                }
            }
        }
    }

    private void markOpenAreas() {
        for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
                if (cells[y][x] == CLOSED && countOpenNeighbors(x, y) >= 5) {
                    cells[y][x] = OPEN;
                }
            }
        }
    }

    private int countOpenNeighbors(int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int nx = x + dx;
                int ny = y + dy;
                if (nx < 0 || ny < 0 || nx >= width || ny >= height) {
                    continue;
                }
                if (isWalkable(cells[ny][nx])) {
                    count++;
                }
            }
        }
        return count;
    }

    private void addExitAreas(int areaScale) {
        List<int[]> candidates = new ArrayList<>();
        for (int y = 4; y < height - 4; y++) {
            for (int x = 4; x < width - 4; x++) {
                if (isWalkable(cells[y][x]) && cells[y][x] != G_OPEN) {
                    candidates.add(new int[]{x, y});
                }
            }
        }
        int exitPatches = Math.max(24 * areaScale, candidates.size() / 120);
        for (int i = 0; i < exitPatches && !candidates.isEmpty(); i++) {
            int[] point = candidates.remove(random.nextInt(candidates.size()));
            floodExitPatch(point[0], point[1], 2 + random.nextInt(3));
        }
    }

    private void floodExitPatch(int startX, int startY, int radius) {
        for (int y = startY - radius; y <= startY + radius; y++) {
            for (int x = startX - radius; x <= startX + radius; x++) {
                if (x <= 1 || y <= 1 || x >= width - 2 || y >= height - 2) {
                    continue;
                }
                if (isWalkable(cells[y][x])) {
                    cells[y][x] = G_OPEN;
                }
            }
        }
    }

    private void setWalkable(int x, int y, int type) {
        if (x <= 0 || y <= 0 || x >= width - 1 || y >= height - 1) {
            return;
        }
        if (cells[y][x] == G_CLOSED) {
            return;
        }
        cells[y][x] = type;
    }

    private static boolean isWalkable(int type) {
        return type == OPEN || type == G_OPEN || type == IR_OPEN || type == IT_OPEN || type == IA_OPEN;
    }

    public void writeDungeonFile(Path outputFile) throws IOException {
        Files.createDirectories(outputFile.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            for (int indX = 0; indX < height; indX++) {
                StringBuilder line = new StringBuilder(width * 3);
                for (int indY = 0; indY < width; indY++) {
                    int row = height - indX - 1;
                    int col = indY;
                    line.append(cells[row][col]).append(',');
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int size = 150;
        if (args.length > 0) {
            size = Integer.parseInt(args[0]);
        }

        Path output = Paths.get("assets/dungeons/" + size + ".txt");
        if (args.length > 1) {
            output = Paths.get(args[1]);
        }

        LargeDungeonGenerator generator = new LargeDungeonGenerator(size, size, size * 1_000_000L + size);
        generator.generate();
        generator.writeDungeonFile(output);
        System.out.println("Generated dungeon: " + output.toAbsolutePath());
    }

    private static final class Room {
        private final int x;
        private final int y;
        private final int w;
        private final int h;

        private Room(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        private int centerX() {
            return x + w / 2;
        }

        private int centerY() {
            return y + h / 2;
        }

        private boolean overlaps(Room other, int padding) {
            return x - padding < other.x + other.w
                    && x + w + padding > other.x
                    && y - padding < other.y + other.h
                    && y + h + padding > other.y;
        }
    }
}
