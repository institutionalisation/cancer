import static util.Util.*;
import java.util.*;
public class ButtonBuilder {
	public static enum Color {
		RED,BLUE,YELLOW;
		//public int value;
	};
	private static String[] colorNames = new String[]{"red","blue","yellow"};
	public Map<Color,Runnable> colorCallbacks = new HashMap<>();
	private Program program;
	private static ModelNode base;
	private static ModelNode[]
		pressed = new ModelNode[3],
		unpressed = new ModelNode[3];
	public ButtonBuilder(Program program) {
		this.program = program;
		base = new Model("button/base","obj",program).rootNode;
		for(int i = 0; i < colorNames.length; ++i) {
			pressed[i] = new Model("button/center-pressed/"+colorNames[i],"obj",program).rootNode;
			unpressed[i] = new Model("button/center-unpressed/"+colorNames[i],"obj",program).rootNode;
		}
	}
	private int stickTime;
	public ButtonBuilder setStickTime(int stickTime) {
		this.stickTime = stickTime;
		return this;
	}
	public class Button extends ModelNode {
		private boolean[] isPressed = new boolean[]{false};
		public Button(Color color) {
			children.add(new ModelNode(){{ set(base); }});
			children.add(new ModelNode(){final ModelNode center = this;{
				center.set(unpressed[color.ordinal()]);
				collisionCallbacks.add(()->{
					if(!isPressed[0]) {
						colorCallbacks.get(color).run();
						isPressed[0]=true;
						center.set(pressed[color.ordinal()]);
						new Thread(()->{exPrint(()->{
							Thread.sleep(stickTime);
							center.set(unpressed[color.ordinal()]);
							isPressed[0]=false;
						});}).start();
					}
				});
			}});
		}
	}
}