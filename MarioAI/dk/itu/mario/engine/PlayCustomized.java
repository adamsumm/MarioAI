package dk.itu.mario.engine;

import java.awt.*;

import javax.swing.*;

import cmps244.CustomizedGenerator;
import cmps244.LevelNode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import org.json.*;
public class PlayCustomized {

	public static void main(String[] args)
	{
		try {
			String tileFile = (CustomizedGenerator.readFile("../Mario Levels/tiles.json",Charset.defaultCharset()));
			JSONObject json = new JSONObject(tileFile);
			String[] tiles = JSONObject.getNames(json);
			LevelNode node = null;
			ArrayList<LevelNode> list = new ArrayList<LevelNode>();
			for (String tile : tiles){
				
				node = new LevelNode(tile,json);
				list.add(node);
			}
			Random rand = new Random();
			node = null;
			while (node == null || node.potentialRight.size() == 0){
				node = list.get(rand.nextInt(list.size()));
			}
			CustomizedGenerator.printLevelChunk(CustomizedGenerator.stringToLevelChunk(node.levelChunk));
			node = node.selectAction();
			CustomizedGenerator.printLevelChunk(CustomizedGenerator.stringToLevelChunk(node.levelChunk));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame frame = new JFrame("Mario Experience Showcase");
		MarioComponent mario = new MarioComponent(640, 480,true);

		frame.setContentPane(mario);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);

		frame.setVisible(true);

		mario.start();   
	}	

}
