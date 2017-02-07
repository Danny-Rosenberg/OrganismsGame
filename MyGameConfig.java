import java.util.concurrent.ThreadLocalRandom;

public class MyGameConfig implements GameConfig {
	
	private int s; //is it always 1?
	private int v;
	private int u;
	private int M;
	private int K;
	
	/**
	 * This constructor will initialize all the random parameters for a game
	 */
	public MyGameConfig(){
		s = s();
		v = v();
		u = u();
		M = M();
		K = K();
	}
	
	/**
	 * The energy consumed in staying put
	 * @return should always return 1 (other parameters scale)
	 */
	@Override
	public int s(){
		//what does it mean, 'other parameters scale?'
		s = 1;
		return s;
	}

	/**
	 * The energy consumed in moving or reproducing
	 * @return the value of v
	 */
	@Override
	public int v(){
		//between 2 - 20
		v = ThreadLocalRandom.current().nextInt(2, 21); //upperbound is exclusive
		return v;
	}
	
	/**
	 * The energy per unit of food
	 * @return the value of u
	 */
	@Override
	public int u(){
		u = ThreadLocalRandom.current().nextInt(10, 501);
		return u;
	}
	
	/**
	 * The maximum energy per organisms
	 * @return the value of M
	 */
	@Override
	public int M(){
		M = ThreadLocalRandom.current().nextInt(100, 1001);
		return M;
		
	}

	/**
	 * The maximum food units per cell
	 * @return the value of K
	 */
	@Override
	public int K(){
		K = ThreadLocalRandom.current().nextInt(10, 51);
		return K;
		
	}
	

	/**
	 * @return the s
	 */
	public int getS() {
		return s;
	}

	/**
	 * @return the v
	 */
	public int getV() {
		return v;
	}

	/**
	 * @return the u
	 */
	public int getU() {
		return u;
	}

	/**
	 * @return the m
	 */
	public int getM() {
		return M;
	}

	/**
	 * @return the k
	 */
	public int getK() {
		return K;
	}


}
