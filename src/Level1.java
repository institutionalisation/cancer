public class Level1 implements Level {
	public String getName() {
		return "Level1";
	}
	public void run() {
		System.out.println("hecc");
		try { Thread.sleep(1000); } catch(Exception e) {}
	}
}