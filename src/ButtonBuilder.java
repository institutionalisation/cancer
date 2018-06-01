import static util.Util.*;
public class ButtonBuilder {
	public static class Color {
		public static int
			RED=0, BLUE=1, YELLOW=2;
		private static String[] names = new String[]{"red","blue","yellow"};
	}
	private Program program;
	private static ModelNode base;
	private static ModelNode[]
		pressed = new ModelNode[3],
		unpressed = new ModelNode[3];
	public ButtonBuilder(Program program) {
		this.program = program;
		base = new Model("button/base","obj",program).rootNode;
		for(int i = 0; i < Color.names.length; ++i) {
			pressed[i] = new Model("button/center-pressed/"+Color.names[i],"obj",program).rootNode;
			unpressed[i] = new Model("button/center-unpressed/"+Color.names[i],"obj",program).rootNode;
		}
	}
	private int stickTime;
	public ButtonBuilder setStickTime(int stickTime) {
		this.stickTime = stickTime;
		return this;
	}
	public ModelNode create(int color,Runnable pressCallback) {
		return new ModelNode(){
			boolean[] isPressed = new boolean[]{false};
		{
			children.add(new ModelNode(){{ set(base); }});
			children.add(new ModelNode(){{
				set(unpressed[color]);
				collisionCallbacks.add(()->{
					if(!isPressed[0]) {
						pressCallback.run();
						isPressed[0]=true;
						set(pressed[color]);
						new Thread(()->{exPrint(()->{
							Thread.sleep(stickTime);
							set(unpressed[color]);
							isPressed[0]=false;
						});}).start();
					}
				});
			}});
		}};
	}
}