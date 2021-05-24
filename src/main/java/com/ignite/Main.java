package com.ignite;

import com.ignite.models.Player;
import com.ignite.models.SportClub;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteBiPredicate;

import javax.cache.Cache;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        Ignite ignite = Ignition.start();
        IgniteCache<UUID, Player> playersMap = ignite.getOrCreateCache("players");
        IgniteCache<UUID, SportClub> clubsMap = ignite.getOrCreateCache("clubs");
        while (true) {
            Integer choice = printMenu();
            clearScreen();
            System.out.println(choice);
            if (choice > 0 && choice < 9) {
                switch (choice) {
                    case 1:
                        addElementToDatabase(playersMap, clubsMap);
                        break;
                    case 2:
                        editElement(playersMap, clubsMap);
                        break;
                    case 3:
                        getElementById(playersMap, clubsMap);
                        break;
                    case 4:
                        getAll(playersMap, clubsMap);
                        break;
                    case 5:
                        removeElement(playersMap, clubsMap);
                        break;
                    case 6:
                        calculateAveragePlayerSalary(playersMap);
                        break;
                    case 7:
                        getElementByName(playersMap, clubsMap);
                        break;
                }
                System.out.println("Press enter to continue...");
                System.in.read();
            } else System.out.println("Wrong number, choose again.");
        }
    }

    private static void getAll(IgniteCache<UUID, Player> players, IgniteCache<UUID, SportClub> clubs) throws IOException {
        System.out.println("Getting all values");
        Integer s = printSubMenu();
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    for (Cache.Entry<UUID, Player> player : players) {
                        System.out.println(player.getKey() + " => " + player.getValue());
                    }
                    break;
                case 2:
                    for (Cache.Entry<UUID, SportClub> club : clubs) {
                        System.out.println(club.getKey() + " => " + club.getValue());
                    }
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementById(IgniteCache<UUID, Player> players, IgniteCache<UUID, SportClub> clubs) throws IOException {
        System.out.println("Getting by id");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (isValidUUID(playerId) && players.containsKey(UUID.fromString(playerId))) {
                        Player player = players.get(UUID.fromString(playerId));
                        System.out.println(playerId + " => " + player.toString());
                    } else System.out.printf("Player with id %s not found.%n", playerId);
                    break;
                case 2:
                    String clubId = scanner.next();
                    if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
                        SportClub club = clubs.get(UUID.fromString(clubId));
                        System.out.println(clubId + " => " + club.toString());
                    } else System.out.printf("Club with id %s not found.%n", clubId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementByName(IgniteCache<UUID, Player> players, IgniteCache<UUID, SportClub> clubs) throws IOException {
        System.out.println("Getting by name");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write name:");
            switch (s) {
                case 1:
                    String playerName = scanner.next();
                    ScanQuery<UUID, Player> scan = new ScanQuery<>((IgniteBiPredicate<UUID, Player>) (uuid, c) -> c.getFirstname().equals(playerName));
                    QueryCursor<Cache.Entry<UUID, Player>> playersCollection = players.query(scan);
                    playersCollection.forEach(player -> System.out.println(player.getValue()));
                    break;
                case 2:
                    String clubName = scanner.next();
                    ScanQuery<UUID, SportClub> clubsScan = new ScanQuery<>((IgniteBiPredicate<UUID, SportClub>) (uuid, c) -> c.getName().equals(clubName));
                    QueryCursor<Cache.Entry<UUID, SportClub>> clubsCollection = clubs.query(clubsScan);
                    clubsCollection.forEach(player -> System.out.println(player.getValue()));
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void editElement(IgniteCache<UUID, Player> players, IgniteCache<UUID, SportClub> clubs) {
        System.out.println("Editing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (isValidUUID(playerId) && players.containsKey(UUID.fromString(playerId))) {
                        Player player = getPlayerFromUser(clubs, scanner);
                        players.put(UUID.fromString(playerId), player);
                        System.out.println(playerId + " => " + player.toString());
                    } else System.out.printf("Player with id %s not found.%n", playerId);
                    break;
                case 2:
                    String clubId = scanner.next();
                    if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
                        SportClub sportClub = getSportClub(scanner);
                        clubs.put(UUID.fromString(clubId), sportClub);
                        System.out.println(clubId + " => " + sportClub.toString());
                    } else System.out.printf("Club with id %s not found.%n", clubId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void removeElement(IgniteCache<UUID, Player> players, IgniteCache<UUID, SportClub> clubs) {
        System.out.println("Removing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (isValidUUID(playerId) && players.containsKey(UUID.fromString(playerId))) {
                        players.remove(UUID.fromString(playerId));
                    } else System.out.printf("Player with id %s not found.%n", playerId);
                    break;
                case 2:
                    String clubId = scanner.next();
                    if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
                        clubs.remove(UUID.fromString(clubId));
                    } else System.out.printf("Club with id %s not found.%n", clubId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private final static Pattern UUID_REGEX_PATTERN =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(str).matches();
    }

    private static void addElementToDatabase(IgniteCache<UUID, Player> players, IgniteCache<UUID, SportClub> clubs) {
        System.out.println("Adding to database");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Player player = getPlayerFromUser(clubs, scanner);
                    players.put(UUID.randomUUID(), player);
                    break;
                case 2:
                    SportClub sportClub = getSportClub(scanner);
                    clubs.put(UUID.randomUUID(), sportClub);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void calculateAveragePlayerSalary(IgniteCache<UUID, Player> players) {
        System.out.println("Calculate average salary");
        List<Player> playersList = new ArrayList<>();
        for (Cache.Entry<UUID, Player> player : players) {
            playersList.add(player.getValue());
        }
        double averageSalary = playersList.stream()
                .mapToDouble(Player::getSalary)
                .average()
                .orElse(0);
        System.out.println("Average player salary: " + averageSalary);
    }

    private static SportClub getSportClub(Scanner scanner) {
        System.out.println("Write club name:");
        String name = scanner.next();
        System.out.println("Write creation year:");
        Integer creationYear = scanner.nextInt();
        return SportClub.builder()
                .name(name)
                .creationYear(creationYear)
                .build();
    }

    private static Player getPlayerFromUser(IgniteCache<UUID, SportClub> clubs, Scanner scanner) {
        System.out.println("Write player first name:");
        String firstname = scanner.next();
        System.out.println("Write player surname:");
        String surname = scanner.next();
        System.out.println("Write club id:");
        String clubId = scanner.next();
        System.out.println("Write player salary:");
        Integer playerSalary = scanner.nextInt();
        SportClub club = null;
        if (isValidUUID(clubId) && clubs.containsKey(UUID.fromString(clubId))) {
            club = clubs.get(UUID.fromString(clubId));
            System.out.println(clubId + " => " + club.toString());
        } else System.out.printf("Club with id %s not found.%n", clubId);
        return Player.builder()
                .firstname(firstname)
                .surname(surname)
                .club(club)
                .salary(playerSalary)
                .build();
    }

    private static Integer printMenu() {
        System.out.println("\nSPORTS CLUB - HAZELCAST");
        System.out.println("\nChoose operation:");
        System.out.println("1.ADD");
        System.out.println("2.EDIT");
        System.out.println("3.GET BY ID");
        System.out.println("4.GET ALL");
        System.out.println("5.REMOVE");
        System.out.println("6.CALCULATE AVERAGE PLAYER SALARY");
        System.out.println("7.GET BY NAME");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    private static Integer printSubMenu() {
        System.out.println("\nChoose table:");
        System.out.println("1.PLAYERS");
        System.out.println("2.SPORT CLUBS");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
