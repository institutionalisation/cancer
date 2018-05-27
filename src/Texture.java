import java.nio.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static util.Util.*;
public class Texture {
	public int
		width,height,channelCount,
		id;
	public Texture(String fileName) {
		int[]
			width = new int[1],
			height = new int[1],
			channelCount = new int[1];
		out.println("loading image:"+fileName);
		ByteBuffer data = stbi_load(fileName,width,height,channelCount,4);
		this.width = width[0];
		this.height = height[0];
		this.channelCount = channelCount[0];
		System.out.println("reason:"+stbi_failure_reason());
		out.println("channelCount:"+channelCount[0]);
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D,id);
		glTexImage2D(GL_TEXTURE_2D,0,
			GL_RGBA,
			this.width,this.height,0,
			GL_RGBA,
			GL_UNSIGNED_BYTE,data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		System.out.println("texture id:"+id);
	}
}