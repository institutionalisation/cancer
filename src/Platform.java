import org.joml.*;
import java.lang.Math;
import static util.Util.*;
class Platform extends ModelNode {
	public long lastRaise;// = System.currentTimeMillis();
	public static int
		RAISE_TIME = 2000,
		HOLD_TIME = 6000;
	public int totalTime() { return RAISE_TIME*2 + HOLD_TIME; }
	public int delta() { return (int)(System.currentTimeMillis()-lastRaise); }
	private Matrix4f currentTransform = new Matrix4f();
	public Matrix4f getLocalTransform() {
		float f = Math.abs(delta()-totalTime()/2);
		f-=HOLD_TIME/2;
		f=f<0?0:f;
		f/=1000;
		return identityMatrix
			.translate(new Vector3f(0,-f*f,0),currentTransform);
	}
	public Runnable raise() {
		return ()->{
			long now = System.currentTimeMillis();
			int since = Math.abs((int)(now - lastRaise));
			if(since<totalTime())
				lastRaise = now
					- (totalTime()/2
						- Math.abs(totalTime()/2 - since));
			else
				lastRaise = now;
		};
	}
}