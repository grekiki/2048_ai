import java.io.*;
import java.util.*;
public class Main{
	public static void main(String[] qqq) throws Exception{
		engine e=new engine();
//		int[][] q={{1,8,5,1},{10,6,11,2},{2,12,6,7},{2,1,3,1}};
//		e.grid=q;
//		int[][] q={{0,0,0,2},{0,2,1,4},{1,3,5,1},{2,5,7,2}};
//		e.grid=q;

		System.out.println(e);
		while(!e.end()){
			int dir=mcts.play(e,true);
			System.out.println(dir);
			System.out.println(e);
			e.move(dir);
			e.spawn();
		}

	}
}
