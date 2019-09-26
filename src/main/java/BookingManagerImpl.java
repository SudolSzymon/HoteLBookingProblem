package main.java;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class BookingManagerImpl implements BookingManager {

    private final Map<LocalDate, Map<Integer, String>> BOOKINGS = new ConcurrentHashMap<>();
    private final Set<Integer> ROOM_NUMBERS = new HashSet<>();

    private static BookingManager instance;

    private BookingManagerImpl(Integer... roomNumbers) {
        Collections.addAll(ROOM_NUMBERS, roomNumbers);
    }

    public static BookingManager getInstance() {
        return instance;
    }

    /**
     * Creates new booking manager  replacing old one
     * */
    public static void createBookingManager(Integer... roomNumbers) {
        requireNonNull(roomNumbers, "roomNumbers");
        instance = new BookingManagerImpl(roomNumbers);
    }


    @Override
    public boolean isRoomAvailable(Integer room, LocalDate date) {
        requireNonNull(room, "room");
        requireNonNull(date, "date");
        if (!ROOM_NUMBERS.contains(room))
            throw new IllegalArgumentException(String.format("Room %s does not exist", room));

        Map<Integer, String> bookingsForDate = BOOKINGS.get(date);
        return bookingsForDate == null || !bookingsForDate.containsKey(room);
    }

    @Override
    public void addBooking(String guest, Integer room, LocalDate date) {
        requireNonNull(room, "room");
        requireNonNull(date, "date");
        requireNonNull(guest, "guest");
        if (!ROOM_NUMBERS.contains(room))
            throw new IllegalArgumentException(String.format("Room %s does not exist", room));

        Map<Integer, String> bookingsForDate = new ConcurrentHashMap<>();
        if (BOOKINGS.putIfAbsent(date, bookingsForDate) != null)
            bookingsForDate = BOOKINGS.get(date);
        if (bookingsForDate.putIfAbsent(room, guest) != null)
            throw new IllegalArgumentException(String.format("This room [%s] is already booked on %s", room, date.toString()));
    }

    @Override
    public Iterable<Integer> getAvailableRooms(LocalDate date) {
        requireNonNull(date, "No date specified");

        Map<Integer, String> bookingsForDate = BOOKINGS.get(date);
        Set<Integer> bookedRooms = bookingsForDate == null ? Collections.EMPTY_SET : bookingsForDate.keySet();
        Set<Integer> roomNumbersCopy = new HashSet<>(ROOM_NUMBERS);
        roomNumbersCopy.removeAll(bookedRooms);
        return roomNumbersCopy;
    }
}
