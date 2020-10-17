import java.io.*;
import java.util.*;
public class Main{
	public static void main(String[]qqq) throws Exception{
//		engine a=new engine();
//		System.out.println(a);
//		Random r=new Random();
//		while(!a.end()) {
//			a.move(r.nextInt(4));
//			System.out.println(a);
//		}
		engine e=new engine();
//		int[][]q={{1,1,2,1},{0,2,4,6},{0,4,6,7},{4,6,7,8}};
//		e.grid=q;
		System.out.println(e);
		while(!e.end()&&e.win()!=1) {
			int dir=mcts.play(e,true);
			System.out.println(dir);
			e.move(dir);
			e.spawn();
			System.out.println(e);
		}
		
	}
}
