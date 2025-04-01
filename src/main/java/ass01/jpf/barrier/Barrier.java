package ass01.jpf.barrier;

/**
 * 
 * Barrier behaviour
 * 
 * N agents blocks until they *all* arrive to a common sync point ("barrier")
 * 
 */
public interface Barrier {

	void hitAndWaitAll() throws InterruptedException;

}
