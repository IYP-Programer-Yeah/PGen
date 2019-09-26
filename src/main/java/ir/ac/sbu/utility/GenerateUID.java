package ir.ac.sbu.utility;

import java.util.concurrent.atomic.AtomicInteger;

public class GenerateUID
{
    private static AtomicInteger idCounter = new AtomicInteger(1);

    public static int createID()
    {
        return idCounter.getAndIncrement();
    }

    public static void setIdCounter(int value) { idCounter = new AtomicInteger(value);}

    public static int getIdCounter() {
        return idCounter.get();
    }
}
