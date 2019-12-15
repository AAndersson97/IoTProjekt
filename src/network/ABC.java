package network;

import net.sourceforge.sizeof.SizeOf;

public class ABC {
    long a;
    long d;
    ABC() {
        System.out.println(SizeOf.sizeOf(this));
        a = 5;
        System.out.println(SizeOf.sizeOf(this));
    }
}
