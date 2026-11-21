class MySemaphore {
    private int permits;

    public MySemaphore(int permits) {
        this.permits = permits;
    }

    public synchronized void acquire() {
        while (permits == 0) {
            try { wait(); } catch (InterruptedException e) { }
        }
        permits--;
    }

    public synchronized void release() {
        permits++;
        notifyAll();
    }
}

class Clerk extends Thread {
    int from, to, amount, times;

    public Clerk(int from, int to, int amount, int times) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.times = times;
    }

    public void run() {
        int first = Math.min(from, to);
        int second = Math.max(from, to);
        for (int i = 0; i < times; i++) {
            Main.locks[first].acquire();
            Main.locks[second].acquire();
            Main.balance[to] += amount;
            try { Thread.sleep(2); } catch (InterruptedException e) { }
            Main.balance[from] -= amount;
            Main.locks[second].release();
            Main.locks[first].release();
        }
    }
}

public class Main {
    static int[] balance = {1000, 1000, 1000};
    static MySemaphore[] locks = {
        new MySemaphore(1), new MySemaphore(1), new MySemaphore(1)
    };

    public static void main(String[] args) throws InterruptedException {
        String[] names = {"Ali", "Mona", "Omar"};

        Clerk[] clerks = {
            new Clerk(0, 1, 50, 20),
            new Clerk(1, 2, 30, 25),
            new Clerk(2, 0, 40, 15),
            new Clerk(1, 0, 20, 10)
        };

        for (Clerk c : clerks) c.start();
        for (Clerk c : clerks) c.join();

        int total = 0;
        for (int i = 0; i < 3; i++) {
            System.out.println("Balance " + names[i] + ": " + balance[i]);
            total += balance[i];
        }
        System.out.println("Total: " + total);
    }
}