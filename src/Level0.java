public class Level0 implements Level {
	public String getName() {
		return "Level0";
	}
	public void run() {
		System.out.println("hecc");
		try { Thread.sleep(1000); } catch(Exception e) {}
	}
}