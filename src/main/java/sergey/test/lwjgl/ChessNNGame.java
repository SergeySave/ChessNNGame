package sergey.test.lwjgl;

import java.io.File;
import java.net.URISyntaxException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import sergey.lib.api.lwjgl.ShaderProgram;
import sergey.lib.api.lwjgl.Texture;
import sergey.lib.api.lwjgl.gl.GLAttributeType;
import sergey.lib.api.lwjgl.gl.GLConnectionMode;
import sergey.lib.api.lwjgl.gl.GLDataType;
import sergey.lib.api.lwjgl.gl.VertexDataDef;
import sergey.lib.api.lwjgl.gl.util.GLUtil;
import sergey.lib.api.lwjgl.mesh.Mesh;
import sergey.lib.api.math.Vector4f;
import sergeysav.neuralnetwork.chess.ChessBoard;

public class ChessNNGame extends WindowApplication {

	private ShaderProgram shaderProgram;

	private Mesh[][] tiles;

	private Texture textureAtlas;

	private ChessBoard board;

	private int fromX = -1;
	private int fromY;
	
	private boolean whiteTeamMoving = true;

	private final Vector4f NO_TINT = new Vector4f(0,0,0,0);
	private final Vector4f FROM_TINT = new Vector4f(1f,0,0,0.4f);
	private final Vector4f HIGHLIGHT_TINT = new Vector4f(0,1f,0,0.4f);
	private final Vector4f BOTH_TINT = new Vector4f(1f,1f,0,0.4f);
	private final Vector4f LEGAL_TINT = new Vector4f(0f,0f,1f,0.4f);

	@Override
	public void create() {
		board = new ChessBoard();

		GL11.glClearColor(1f, 1f, 1f, 1.0f);

		shaderProgram = new ShaderProgram("shader", null, null);

		try {
			textureAtlas = new Texture(new File(getClass().getResource("/atlas.png").toURI()).getAbsolutePath(), Texture.Type.RGBA, true, true);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		tiles = new Mesh[8][8];
		for (int i = 0; i<8; i++) {
			for (int j = 0; j<8; j++) {
				tiles[i][j] = genMesh(i, j);
			}
		}

		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//GL11.gl
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	@Override
	public void render() {
		textureAtlas.bind();

		int xGrid = (int) Math.floor(getMouseX()/getWidth()*10 - 1);
		int yGrid = 7-(int) Math.floor(getMouseY()/getHeight()*10 - 1);

		for (int i = 0; i<8; i++) {
			for (int j = 0; j<8; j++) {
				int piece = board.getPieceAt(i, j);
				GLUtil.setUniform(shaderProgram.getUniform("u_tint"), (fromX == xGrid && fromY == yGrid && fromX == j && fromY == i ? BOTH_TINT : (fromX == j && fromY == i ? FROM_TINT : (xGrid == j && yGrid == i ? HIGHLIGHT_TINT : fromX > -1 && board.isLegalMove(fromY + "" + fromX, i + "" + j) ? LEGAL_TINT : NO_TINT))));
				GL20.glUniform1f(shaderProgram.getUniform("u_team"), (i + j) % 2 == 0 ? 1 : 0);
				GL20.glUniform1f(shaderProgram.getUniform("u_type"), 0);
				tiles[i][j].draw();
				GLUtil.setUniform(shaderProgram.getUniform("u_tint"), NO_TINT);
				if (Math.abs(piece) != 7 && piece != 0) {
					GL20.glUniform1f(shaderProgram.getUniform("u_team"), piece < 0 ? 0 : 1);
					GL20.glUniform1f(shaderProgram.getUniform("u_type"), Math.abs(piece));
					tiles[i][j].draw();
				}
			}
		}

		Texture.unbind();
	}

	@Override
	public void dispose() {
		for (int i = 0; i<8; i++) {
			for (int j = 0; j<8; j++) {
				tiles[i][j].dispose();
			}
		}

		textureAtlas.dispose();
		shaderProgram.dispose();
	}

	private Mesh genMesh(int j, int i) {
		Mesh mesh = new Mesh();
		mesh.setDrawingMode(GLConnectionMode.TRIANGLE_STRIP.glMode);

		float[] vertexBuffer = new float[]{
				toGLSpace((i+1)/10f), toGLSpace((j+1)/10f), 0f, 0f,
				toGLSpace((i+1)/10f), toGLSpace((j+2)/10f), 0f, 1f,
				toGLSpace((i+2)/10f), toGLSpace((j+1)/10f), 1f, 0f,
				toGLSpace((i+2)/10f), toGLSpace((j+2)/10f), 1f, 1f
		};

		int[] indexBuffer = new int[]{0,1,2,3};

		mesh.setVertexBuffer(vertexBuffer, GLDataType.STATIC, new VertexDataDef("a_position", GLAttributeType.VEC2), new VertexDataDef("a_uv", GLAttributeType.VEC2));
		mesh.setElementBuffer(indexBuffer, GLDataType.STATIC);

		mesh.setShader(shaderProgram);

		return mesh;
	}

	private float toGLSpace(float v) {
		return 2*v - 1;
	}

	@Override
	protected boolean handleMouseInput(int button, int action, int mods) {
		if (action == GLFW.GLFW_RELEASE && button == GLFW.GLFW_MOUSE_BUTTON_1) {
			int xGrid = (int) Math.floor(getMouseX()/getWidth()*10 - 1);
			int yGrid = 7-(int) Math.floor(getMouseY()/getHeight()*10 - 1);
			if (xGrid >= 0 && yGrid >= 0 && xGrid < 8 && yGrid < 8) {
				if (fromX == -1) {
					if ((whiteTeamMoving ? board.getPieceAt(yGrid, xGrid) > 0 : board.getPieceAt(yGrid, xGrid) < 0)) {
						fromX = xGrid;
						fromY = yGrid;
					}
				} else {
					tryMove(fromX, fromY, xGrid, yGrid);
					fromX = -1;
				}
			} else {
				fromX = -1;
			}
			//board.isLegalMove(from, to)
			return true;
		}
		return super.handleMouseInput(button, action, mods);
	}
	
	private boolean tryMove(int fX, int fY, int tX, int tY) {
		if ((whiteTeamMoving ? board.getPieceAt(fY, fX) > 0 : board.getPieceAt(fY, fX) < 0) && board.isLegalMove(fY + "" + fX, tY + "" + tX)) {
			board.applyConvertedMove(fromY + "" + fromX + ";" + tY + "" + tX + ";" + board.getPieceAt(fromY, fromX));
			whiteTeamMoving = !whiteTeamMoving;
			return true;
		}
		return false;
	}
}
