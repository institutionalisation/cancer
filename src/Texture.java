import java.nio.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static util.Util.*;
import java.util.*;
public class Texture {
	public int
		width,height,channelCount,id;
	private static Map<String,Texture> map = new HashMap<>();
	public static Texture fromFile(String modelName,String fileName) {
		Texture t = new Texture();
		int[]
			width = new int[]{-1},
			height = new int[1],
			channelCount = new int[1];
		out.println("loading image:"+modelName+fileName);
		if(map.keySet().contains(modelName+fileName))
			return map.get(modelName+fileName);
		ByteBuffer data = stbi_load(modelName+fileName,width,height,channelCount,4);
		if(width[0] == -1) {
			out.println("failed, retry:"+fileName);
			if(map.keySet().contains(fileName))
				return map.get(fileName);
			data = stbi_load(fileName,width,height,channelCount,4);
			map.put(fileName,t);
		} else
			map.put(modelName+fileName,t);
		t.width = width[0];
		t.height = height[0];
		t.channelCount = channelCount[0];
		System.out.println("reason:"+stbi_failure_reason());
		out.println("channelCount:"+channelCount[0]);
		t.id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D,t.id);
		glTexImage2D(GL_TEXTURE_2D,0,
			GL_RGBA,
			t.width,t.height,0,
			GL_RGBA,
			GL_UNSIGNED_BYTE,data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		System.out.println("texture id:"+t.id);
		return t;
	}
}