package sergey.test.lwjgl;

import java.util.HashSet;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public abstract class WindowApplication {
	
	private long window;
	
	private HashSet<Integer> keysJustDown, keysJustReleased, mouseJustDown, mouseJustReleased;
	
	private int width;
	private int height;
	
	private double mouseX;
	private double mouseY;
	
	public final void initialize(int widthSet, int heightSet) {
		keysJustDown = new HashSet<>();
		mouseJustDown = new HashSet<>();
		keysJustReleased = new HashSet<>();
		mouseJustReleased = new HashSet<>();
		
		width = widthSet;
		height = heightSet;
		
		try {
			//Initialize the window
			init(width, height);
			
			// Set the clear color to black
			GL11.glClearColor(0f, 0f, 0f, 1.0f);
			
			//Call the create/setup method in the subclass
			create();
			
			GLFW.glfwSetWindowRefreshCallback(window, (window)->{
				keysJustDown.clear();
				mouseJustDown.clear();
				keysJustReleased.clear();
				mouseJustReleased.clear();
				
				//Clear the screen
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
				//Call the render method in the subclass
				render();

				//Swap the color buffers
				GLFW.glfwSwapBuffers(window);
			});
			
			GLFW.glfwSetWindowSizeCallback(window, (window, width, height)->{
				this.width = width;
				this.height = height;
				onResize(this.width, this.height);
			});

			//Continue looping until the window has been asked to close by this program or the user
			while ( !GLFW.glfwWindowShouldClose(window) ) {
				keysJustDown.clear();
				mouseJustDown.clear();
				keysJustReleased.clear();
				mouseJustReleased.clear();
				
				//Recieve input events
				GLFW.glfwPollEvents();
				
				//Clear the screen
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
				//Call the render method in the subclass
				render();

				//Swap the color buffers
				GLFW.glfwSwapBuffers(window);
			}

			//Free all of the callbacks
			Callbacks.glfwFreeCallbacks(window);
			
			//Destoy the window
			GLFW.glfwDestroyWindow(window);
		} finally {
			//Terminate GLFW
			GLFW.glfwTerminate();
			
			//Free the error callback
			GLFW.glfwSetErrorCallback(null).free();
		}
	}
	
	private final void init(int width, int height) {
		//Set the error callback to print to the output stream
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		if ( !GLFW.glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		//Set the default winodw hints
		GLFW.glfwDefaultWindowHints();

		//Request minimum OpenGL 3.3
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		
		//Allow the usage of forward compatability
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
		
		//Get a core OpenGL profile
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		
		//Don't show the winow as soon as it is created
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

		//Allow the window to be resized
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

		// Create the window
		window = GLFW.glfwCreateWindow(width, height, "LWJGL Test", MemoryUtil.NULL, MemoryUtil.NULL);
		if ( window == MemoryUtil.NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (handleKeyPress(key, scancode, action, mods)) return;
			if (action == GLFW.GLFW_PRESS) {
				keysJustDown.add(key);
			} else if (action == GLFW.GLFW_RELEASE) {
				keysJustReleased.add(key);
			}
		});
		
		GLFW.glfwSetCharCallback(window, (w, charVal)->handleCharacterInput(Character.toChars(charVal)));
		
		GLFW.glfwSetMouseButtonCallback(window, (w, button, action, mods)->{
			if (handleMouseInput(button, action, mods)) return;
			if (action == GLFW.GLFW_PRESS) {
				mouseJustDown.add(button);
			} else if (action == GLFW.GLFW_RELEASE) {
				mouseJustReleased.add(button);
			}
		});
		
		GLFW.glfwSetCursorPosCallback(window, (w,x,y)->{
			mouseX = x;
			mouseY = y;
			handleMouseMove(x, y);
		});

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		// Center our window
		GLFW.glfwSetWindowPos(
				window,
				(vidmode.width() - width) / 2,
				(vidmode.height() - height) / 2
				);

		// Make the OpenGL context current
		GLFW.glfwMakeContextCurrent(window);
		// Enable v-sync
		GLFW.glfwSwapInterval(1);

		// Make the window visible
		GLFW.glfwShowWindow(window);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities(true);
	}
	
	/**
	 * Used to handle a key press, repeat, or release
	 * 
	 * @param key the key that was pressed
	 * @param scancode the scancode of the key
	 * @param action the action that caused this call
	 * @param mods the modifier keys that are currently down
	 * @return true if this key should be prevented from affecting anything else
	 */
	protected boolean handleKeyPress(int key, int scancode, int action, int mods) {
		return false;
	}
	
	protected void handleCharacterInput(char[] chars) {}
	
	/**
	 * Used to handle a mouse press, repeat, or release
	 * 
	 * @param button the button of the mouse
	 * @param action the action that caused this call
	 * @param mods the modifier keys that are currently down
	 * @return true if this action should be prevented from affecting anything else
	 */
	protected boolean handleMouseInput(int button, int action, int mods) {
		return false;
	}
	
	protected void handleMouseMove(double x, double y) {}
	
	public boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
	}
	
	public boolean isKeyJustDown(int key) {
		return keysJustDown.contains(key);
	}
	
	public boolean isKeyJustUp(int key) {
		return keysJustReleased.contains(key);
	}
	
	public boolean isMouseDown(int button) {
		return GLFW.glfwGetMouseButton(window, button) == GLFW.GLFW_PRESS;
	}
	
	public boolean isMouseJustDown(int button) {
		return mouseJustDown.contains(button);
	}
	
	public boolean isMouseJustUp(int button) {
		return mouseJustReleased.contains(button);
	}
	
	protected void onResize(int width, int height) {}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public double getMouseX() {
		return mouseX;
	}
	
	public double getMouseY() {
		return mouseY;
	}
	
	public abstract void create();
	
	public abstract void render();
	
	public abstract void dispose();
}
