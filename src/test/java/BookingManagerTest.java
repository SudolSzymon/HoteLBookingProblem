package test.java;

import main.java.BookingManager;
import main.java.BookingManagerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.Arrays;

public class BookingManagerTest {

    private static final Integer[] ROOM_NUMBERS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 234, 4324, 23239871, 101};
    private static BookingManager manager;

    @BeforeClass
    public static void setup() {
        BookingManagerImpl.createBookingManager(ROOM_NUMBERS);
        manager = BookingManagerImpl.getInstance();
    }

    @Test
    public void generalTest() {
        LocalDate today = LocalDate.parse("2012-07-21");
        LocalDate notToday = LocalDate.parse("2012-07-22");
        Iterable<Integer> available = manager.getAvailableRooms(today);
        for (Integer room : available) {
            Assert.assertTrue(Arrays.asList(ROOM_NUMBERS).contains(room));
        }
        Assert.assertTrue(manager.isRoomAvailable(101, today)); // outputs true
        manager.addBooking("Smith", 101, today);
        Assert.assertFalse(manager.isRoomAvailable(101, today)); // outputs false
        available = manager.getAvailableRooms(today);
        for (Integer room : available) {
            Assert.assertTrue(Arrays.asList(ROOM_NUMBERS).contains(room));
            Assert.assertNotEquals(101, (int) room);
        }
        Assert.assertTrue(manager.isRoomAvailable(1, today)); // outputs true
        manager.addBooking("Smith", 1, today);
        Assert.assertFalse(manager.isRoomAvailable(1, today)); // outputs false

        Assert.assertTrue(manager.isRoomAvailable(101, notToday)); // outputs true
        manager.addBooking("Smith", 101, notToday);
        Assert.assertFalse(manager.isRoomAvailable(101, notToday)); // outputs false

    }

    @Test
    public void multiThreadedTest() {
        LocalDate today = LocalDate.parse("2012-07-25");
        for (Integer room : ROOM_NUMBERS) {
            Runnable runnable = () -> {
                Assert.assertTrue(manager.isRoomAvailable(room, today)); // outputs true
                manager.addBooking("Smith", room, today);
                Assert.assertFalse(manager.isRoomAvailable(room, today)); // outputs false
            };
            Thread t = new Thread(runnable);
            t.start();
        }
        Assert.assertFalse(manager.getAvailableRooms(today).iterator().hasNext()); //check if all rooms were booked as expected
    }
}
