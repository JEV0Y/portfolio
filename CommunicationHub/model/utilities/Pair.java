package model.utilities;

/* Uses Java Generics (the same as used in an ArrayList) to define a Pair: each pair has two elements.
  The elements of the pair may only be pointer/object types. 
 * 
 * As an example, to declare an instantiate a pair the following may be used:
 * 		Pair<String, Integer> aPair = new Pair<String, Integer>("A string", 5); 
 */
public class Pair<F, S> {

	public final F first;
	public final S second;

	public Pair(F f, S s) {
		first = f;
		second = s;
	}

}