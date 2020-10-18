import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
class Board{
	public final static int orders[][]={{13,14,15,16,19,20,21,22,25,26,27,28},{9,15,21,27,8,14,20,26,7,13,19,25},{19,20,21,22,13,14,15,16,7,8,9,10},{8,14,20,26,9,15,21,27,10,16,22,28},};
	public final static int all[]={7,8,9,10,13,14,15,16,19,20,21,22,25,26,27,28};
	public final static int dirs[]={-6,1,6,-1};

	public final static int UP=0;
	public final static int RIGHT=1;
	public final static int DOWN=2;
	public final static int LEFT=3;
	public final static int moves[]={UP,RIGHT,DOWN,LEFT};

	private int[] grid=new int[]{-1,-1,-1,-1,-1,-1,-1,0,0,0,0,-1,-1,0,0,0,0,-1,-1,0,0,0,0,-1,-1,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,};
	public boolean changed=false;

	private Random rand=ThreadLocalRandom.current();
	public void unsafe_spawn(){
		while(true){
			int p=rand.nextInt(32);
			if(grid()[p]==0){
				grid()[p]=pickRandomly();
				break;
			}
		}
		// FIXME: Hangs if no spawn is available
	}

	public int pickRandomly(){
		return rand.nextInt(10)==0?2:1;
	}

	public Board spawn(){
		Board board1=copy();
		board1.unsafe_spawn();
		return board1;
	}

	boolean merged[]=new boolean[36];
	public void unsafe_move(int move){
		Arrays.fill(merged,false);
		changed=false;
		int dir=dirs[move];
		for(int src:orders[move]){
			if(grid[src]==0)
				continue;
			int dst=src+dir;
			// Move unto free squares
			while(grid[dst]==0){
				dst+=dir;
			}
			// Merge
			if(grid[dst]==grid[src]&&!merged[dst]){
				grid[dst]+=1;
				merged[dst]=true;
				dst+=dir;
			}
			// Normal termination
			else{
				grid[dst-dir]=grid[src];
			}
			// Move happened
			if(dst!=src+dir){
				grid[src]=0;
				changed=true;
			}
		}
	}

	public Board move(int move){
		Board board=copy();
		board.unsafe_move(move);
		return board;
	}

	public boolean isStuck(){
		for(int p:all){
			if(grid()[p]==0)
				return false;
			for(int dir:dirs){
				if(grid()[p+dir]==grid()[p])
					return false;
			}
		}
		return true;
	}

	public boolean isFull(){
		for(int p:all){
			if(grid()[p]==0)
				return false;
		}
		return true;
	}

	public boolean canDirection(int move){
		int dir=dirs[move];
		for(int p:orders[move]){
			if(grid()[p+dir]==0)
				return true;
			if(grid()[p+dir]==grid()[p])
				return true;
		}
		return false;
	}

	public void print(){
		for(int i=0;i<36;i++){
			if(grid()[i]==0)
				System.out.print(" .");
			if(grid()[i]>0)
				System.out.print(" "+grid()[i]);
			if(i%6==0&&i!=0&&i!=6)
				System.out.println();
		}
		System.out.println();
	}

	public Board copy(){
		Board copy=new Board();
		System.arraycopy(grid(),0,copy.grid(),0,36);
		return copy;
	}

	@Override public int hashCode(){
		return Arrays.hashCode(grid());
	}

	@Override public boolean equals(Object obj){
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Board))
			return false;
		Board other=(Board)obj;
		if(!Arrays.equals(grid(),other.grid()))
			return false;
		return true;
	}

	@Override public String toString(){
		String s="";
		for(int p:all){
			s+=(char)(grid()[p]==0?'.':grid()[p]+'0');
			if(p%6==4)
				s+=" ";
		}
		return "Board [grid="+s+"]";
	}

	public int[] grid(){
		return grid;
	}
}
class BitBoards{
	private static Random rand=new Random();

	public final static int RIGHT=0;
	public final static int DOWN=1;
	public final static int LEFT=2;
	public final static int UP=3;

	private final static short[] move_table=new short[0x10000];
	static{
		Board board=new Board();
		for(int row=0;row<=0xffff;row++){
			board.grid()[Board.all[0]]=row&0xf;
			board.grid()[Board.all[1]]=row>>>4&0xf;
			board.grid()[Board.all[2]]=row>>>8&0xf;
			board.grid()[Board.all[3]]=row>>>12&0xf;
			board=board.move(Board.RIGHT);
			short out=(short)board.grid()[Board.all[0]];
			out|=(short)board.grid()[Board.all[1]]<<4;
			out|=(short)board.grid()[Board.all[2]]<<8;
			out|=(short)board.grid()[Board.all[3]]<<12;
			move_table[row]=out;
		}
	}
	/**
	 * Transposes the board, one diagonal at the time.
	 */
	public static long trans(long board){
		long res=0;
		res|=board<<4*9&0x000f000000000000L;
		res|=board<<4*6&0x00f0000f00000000L;
		res|=board<<4*3&0x0f0000f0000f0000L;
		res|=board&0xf0000f0000f0000fL;
		res|=board>>>4*3&0x0000f0000f0000f0L;
		res|=board>>>4*6&0x00000000f0000f00L;
		res|=board>>>4*9&0x000000000000f000L;
		return res;
	}

	/**
	 * Reverses the board along the row axis.
	 */
	public static long reverse(long board){
		board=(board<<8&0xff00ff00ff00ff00L)|(board>>>8&0x00ff00ff00ff00ffL);
		board=(board<<4&0xf0f0f0f0f0f0f0f0L)|(board>>>4&0x0f0f0f0f0f0f0f0fL);
		return board;
	}

	/**
	 * Isolates the row'th row, moves it with the move_table, and shifts it back.
	 */
	private static long move_row_right(long board,int row){
		int b=(int)(board>>>16*row)&0xffff;
		// move_table is a short[], so to avoid sign issues, we need to and with 0xffff
		board=(long)move_table[b]&0xffff;
		return board<<16*row;
	}

	private static long move_up(long board){
		return trans(move_left(trans(board)));
	}

	private static long move_right(long board){
		return move_row_right(board,0)|move_row_right(board,1)|move_row_right(board,2)|move_row_right(board,3);
	}

	private static long move_down(long board){
		return trans(move_right(trans(board)));
	}

	private static long move_left(long board){
		return reverse(move_right(reverse(board)));
	}

	public static long move(long board,int move){
		switch(move){
			case RIGHT:
				return move_right(board);
			case DOWN:
				return move_down(board);
			case LEFT:
				return move_left(board);
			case UP:
				return move_up(board);
			default:
				return 0;
		}
	}

	static int frees(long x){
		if(x==0) {
			return 16;
		}
		x|=(x>>2)&0x3333333333333333l;
		x|=(x>>1);
		x=~x&0x1111111111111111l;
		// At this point each nibble is:
		//  0 if the original nibble was non-zero
		//  1 if the original nibble was zero
		// Next sum them all
		x+=x>>32;
		x+=x>>16;
		x+=x>>8;
		x+=x>>4; // this can overflow to the next nibble if there were 16 empty positions
		return (int)(x&0xf);
	}

	public static long spawn(long board){
		// asserts there is a free spot 
		int p=rand.nextInt(frees(board));
		int i=0;
		while(p!=-1){
			if((board>>>i*4&0xf)==0)
				p--;
			i++;
		}
		return board|pickRandomly()<<4*(i-1);
	}

	public static long pickRandomly(){
		return rand.nextInt(10)==0?2:1;
	}

	/**
	 * Not a very performant way of doing this.
	 */
	public static boolean isStuck(long board){
		boolean res=false;
		for(int move=0;move<4;move++){
			res|=canDirection(board,move);
		}
		return !res;
	}

	public static boolean canDirection(long board,int move){
		return move(board,move)!=board;
	}

	public static String print(long board){
		StringBuffer s=new StringBuffer("");
		for(int i=0;i<16;i++){
			long val=board>>>4*i&0xf;
			if(val==0)
				s.append(" .");
			if(val>0)
				s.append(" "+val);
			if((i+1)%4==0)
				s.append("\n");
		}
		s.append("\n");
		return s.toString();
	}
	public static double score(long board){
		int score=0;
		for(int i=0;i<16;i++){
			long val=board>>>4*i&0xf;
			score+=Math.pow(2,val);
		}
		return Math.log(score)/Math.log(2);// upto 12

	}
}
//public class engine{
//	public int[][] grid;
//	public engine(){
//		grid=new int[4][4];
//		spawn();
//		spawn();
//	}
//
//	public boolean move(int dir){
//		//morda je lepsa resitev. Ni ocitna
//		boolean did_work=false;
//		if(dir==0){
//			for(int x=2;x>=0;x--){
//				for(int y=0;y<4;y++){
//					if(grid[x][y]!=0){
//						int x2=x;
//						while(x2<3){
//							if(grid[x2+1][y]==0){
//								grid[x2+1][y]=grid[x2][y];
//								grid[x2][y]=0;
//								did_work=true;
//							}else if(grid[x2+1][y]==grid[x2][y]){
//								grid[x2+1][y]++;
//								grid[x2][y]=0;
//								did_work=true;
//							}else{
//								break;
//							}
//							x2++;
//						}
//					}
//				}
//			}
//		}
//		if(dir==1){
//			for(int x=3;x>=0;x--){
//				for(int y=2;y>=0;y--){
//					if(grid[x][y]!=0){
//						int y2=y;
//						while(y2<3){
//							if(grid[x][y2+1]==0){
//								grid[x][y2+1]=grid[x][y2];
//								grid[x][y2]=0;
//								did_work=true;
//							}else if(grid[x][y2+1]==grid[x][y2]){
//								grid[x][y2+1]++;
//								grid[x][y2]=0;
//								did_work=true;
//							}else{
//								break;
//							}
//							y2++;
//						}
//					}
//				}
//			}
//		}
//		if(dir==2){
//			for(int x=1;x<4;x++){
//				for(int y=0;y<4;y++){
//					if(grid[x][y]!=0){
//						int x2=x;
//						while(x2>0){
//							if(grid[x2-1][y]==0){
//								grid[x2-1][y]=grid[x2][y];
//								grid[x2][y]=0;
//								did_work=true;
//							}else if(grid[x2-1][y]==grid[x2][y]){
//								grid[x2-1][y]++;
//								grid[x2][y]=0;
//								did_work=true;
//							}else{
//								break;
//							}
//							x2--;
//						}
//					}
//				}
//			}
//		}
//		if(dir==3){
//			for(int x=0;x<4;x++){
//				for(int y=1;y<4;y++){
//					if(grid[x][y]!=0){
//						int y2=y;
//						while(y2>0){
//							if(grid[x][y2-1]==0){
//								grid[x][y2-1]=grid[x][y2];
//								grid[x][y2]=0;
//								did_work=true;
//							}else if(grid[x][y2-1]==grid[x][y2]){
//								grid[x][y2-1]++;
//								grid[x][y2]=0;
//								did_work=true;
//							}else{
//								break;
//							}
//							y2--;
//						}
//					}
//				}
//			}
//		}
//		return did_work;
//	}
//	/**
//	 * Will run forever if spawning on full board
//	 */
//	public void spawn(){
//		while(true){
//			int x=(int)Math.floor(4*Math.random());
//			int y=(int)Math.floor(4*Math.random());
//			if(grid[x][y]==0){
//				if(Math.random()<0.9){
//					grid[x][y]=1;
//				}else{
//					grid[x][y]=2;
//				}
//				return;
//			}
//		}
//
//	}
//	public boolean end(){
//		engine c=this.clone();
//		for(int x=0;x<4;x++){
//			if(c.move(x)){
//				return false;
//			}
//		}
//		return true;
//	}
//
//	@Override public String toString(){
//		StringBuilder out=new StringBuilder("");
//		for(int y=0;y<4;y++){
//			for(int x=0;x<4;x++){
//				if(grid[x][y]<10){
//					out.append(grid[x][y]+" ");
//				}else{
//					out.append((char)('a'-10+grid[x][y])+" ");
//
//				}
//			}
//			out.append("\n");
//		}
//		return out.toString();
//	}
//	@Override public engine clone(){
//		engine e=new engine();
//		for(int i=0;i<4;i++){
//			for(int j=0;j<4;j++){
//				e.grid[i][j]=grid[i][j];
//			}
//		}
//		return e;
//	}
//
//	@Override public int hashCode(){
//		return Arrays.deepHashCode(grid);
//	}
//
//	@Override public boolean equals(Object obj){
//		if(this==obj)
//			return true;
//		if(obj==null)
//			return false;
//		if(getClass()!=obj.getClass())
//			return false;
//		engine other=(engine)obj;
//		if(!Arrays.deepEquals(grid,other.grid))
//			return false;
//		return true;
//	}
//
//	public double score(){
//		int score=0;
//		for(int i=0;i<4;i++){
//			for(int j=0;j<4;j++){
//				score+=Math.pow(2,grid[i][j]);
////				score=Math.max(score,grid[i][j]);
//			}
//		}
//
//		return Math.log(score)/Math.log(2);// upto 12
////		return score;
//	}
//
//}
