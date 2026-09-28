package co.edu.unicauca.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    void shouldNotifyObserver() {

        Subject subject = new Subject();

        TestObserver observer = new TestObserver();

        subject.addObserver(observer);

        subject.notifyObservers();

        assertEquals(1,observer.getNotificationCount());
    }

    @Test
    void shouldNotifyMultipleObservers() {

        Subject subject = new Subject();

        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();

        subject.addObserver(observer1);
        subject.addObserver(observer2);

        subject.notifyObservers();

        assertEquals(1,observer1.getNotificationCount());

        assertEquals(1,observer2.getNotificationCount());
    }

    @Test
    void shouldNotifyObserverMultipleTimes() {

        Subject subject = new Subject();

        TestObserver observer = new TestObserver();

        subject.addObserver(observer);

        subject.notifyObservers();
        subject.notifyObservers();
        subject.notifyObservers();

        assertEquals(3,observer.getNotificationCount());
    }

    @Test
    void shouldNotNotifyRemovedObserver() {

        Subject subject = new Subject();

        TestObserver observer = new TestObserver();

        subject.addObserver(observer);

        subject.notifyObservers();

        subject.removeObserver(observer);

        subject.notifyObservers();

        assertEquals(1,observer.getNotificationCount());
    }

    private static class TestObserver
            implements Observer {

        private int notificationCount = 0;

        @Override
        public void update() {
            notificationCount++;
        }

        public int getNotificationCount() {
            return notificationCount;
        }
    }
}