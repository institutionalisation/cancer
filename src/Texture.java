import java.nio.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
public class Texture {
	public int
		width,height,channelCount,
		id;
	public Texture(String fileName) {
		int[]
			width = new int[1],
			height = new int[1],
			channelCount = new int[1];
		ByteBuffer data = stbi_load(fileName,width,height,channelCount,3);
		System.out.println("tex width: "+width[0]+", height:"+height[0]);
		this.width = width[0];
		this.height = height[0];
		this.channelCount = channelCount[0];
		System.out.println("tex:"+width[0]+","+height[0]);
		System.out.println("reason:"+stbi_failure_reason());
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D,id);
		glTexImage2D(GL_TEXTURE_2D,0,GL_RGB,this.width,this.height,0,GL_RGB,GL_UNSIGNED_BYTE,data);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		System.out.println("texture id:"+id);
	}
}