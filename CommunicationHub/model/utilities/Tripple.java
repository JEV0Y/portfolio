package model.utilities;

public class Tripple <F, S, T> extends Pair <F, S>{
	
	public final T third;

	public Tripple(F f, S s, T t) {
		super(f,s);
		third = t;
	}
}
