import java.io.*;
import java.util.*;
public class Main{
	public static void main(String[] qqq) throws Exception{
		long board=0;
		board=BitBoards.spawn(board);
		board=BitBoards.spawn(board);
//		board=6917691827489367330l;
		System.out.println(BitBoards.print(board));
		while(!BitBoards.isStuck(board)){
			int dir=mcts.play(board,true);
			System.out.println(dir);
			System.out.println(board);
			System.out.println(BitBoards.print(board));
			board=BitBoards.move(board,dir);
//			System.out.println(BitBoards.print(board));
			board=BitBoards.spawn(board);
//			System.out.println(BitBoards.print(board));
		}
		System.out.println(BitBoards.print(board));

	}
}
