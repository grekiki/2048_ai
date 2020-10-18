import java.io.*;
import java.util.*;
public class Main{
	public static void main(String[] qqq) throws Exception{
//		engine a=new engine();
//		System.out.println(a);
//		Random r=new Random();
//		while(!a.end()) {
//			a.move(r.nextInt(4));
//			System.out.println(a);
//		}
		engine e=new engine();
//		int[][] q={{0,0,0,0},{4,6,7,4},{1,8,4,3},{0,2,6,1}};
//		e.grid=q;
//		5 3 6 1 
//		3 7 9 2 
//		8 6 2 5 
//		2 4 2 1 
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
