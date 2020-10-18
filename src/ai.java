import java.util.*;
class globalno{
	public static final double tlimit=200;
}
class mcts{
	public static int play(long g,boolean console) throws Exception{
		long l=System.currentTimeMillis();
		player_node n=new player_node();
		double score=BitBoards.score(g);
		nat_node.smin=score;
		player_node.smin=score;
		while(System.currentTimeMillis()-l<globalno.tlimit){
			for(int i=0;i<100;i++){
				n.run(g);
			}
		}
		if(console){
			System.out.println(n);
			System.out.println(score);
			n.run(g);
		}
		return n.best();

	}

}
class nat_node{
	public double wins;
	public int plays;
	player_node[] ch;
	static double smin;
	long hash=-1;
	nat_node(){
		this.ch=new player_node[32];
		wins=0;
		plays=0;
	}
	double explore(long g) throws Exception{
		if(hash==-1) {
			hash=g;
		}
		if(hash!=g) {
			System.out.println("inconsistent");
		}
		plays++;
		int[] q={0,3,0,1};//R U R D
		while(!BitBoards.isStuck(g)){
			boolean found=false;
			for(int dir:q){
				if(BitBoards.canDirection(g,dir)){
					g=BitBoards.move(g,dir);
					g=BitBoards.spawn(g);
					found=true;
				}
			}
			if(!found){
				if(BitBoards.canDirection(g,2)){
					g=BitBoards.move(g,2);
					g=BitBoards.spawn(g);
				}else{
					System.out.println("This should not happen");
					Thread.sleep(10000);
				}
			}
		}
		wins+=BitBoards.score(g)-smin;
		return BitBoards.score(g);
	}
	double run(long g) throws Exception{
		if(hash==-1) {
			hash=g;
		}
		if(hash!=g) {
			System.out.println("inconsistent");
		}
		plays++;
		if(BitBoards.isStuck(g)){
			wins+=BitBoards.score(g)-smin;
			return BitBoards.score(g);
		}
		while(true){
			int x=(int)Math.floor(4*Math.random());
			int y=(int)Math.floor(4*Math.random());
			if((g>>>4*(4*y+x)&0xf)==0){
				int p;
				if(Math.random()<0.9){
					p=16*1+4*y+x;
				}else{
					p=16*0+4*y+x;
				}
				if(p>=16){
					g=g|1l<<4*(4*y+x);
				}else{
					g=g|2l<<4*(4*y+x);
				}
				if(ch[p]==null){
					ch[p]=new player_node();
				}
				double t=ch[p].run(g);
				wins+=t-smin;
				return t;
			}
		}
	}
	@Override public String toString(){
		return String.format("%.0f",Math.pow(2,wins/plays+smin)-Math.pow(2,smin))+" "+String.format("%.4f",smin*wins/plays)+" "+plays;
	}

}
class player_node{
	public static final double c=150;
	nat_node[] ch;
	double wins;
	int plays;
	static double smin;
	long hash=-1;
	player_node(){
		this.ch=new nat_node[4];
		wins=0;
		plays=0;
	}
	double run(long g) throws Exception{
		if(hash==-1) {
			hash=g;
		}
		if(hash!=g) {
			System.out.println("inconsistent");
		}
		plays++;
		if(BitBoards.isStuck(g)){
			wins+=BitBoards.score(g)-smin;
			return BitBoards.score(g);
		}
		for(int i=0;i<4;i++){
			if(!BitBoards.canDirection(g,i)){
				continue;
			}
			if(ch[i]==null){
				//run exploration
				ch[i]=new nat_node();
				g=BitBoards.move(g,i);
				double t=ch[i].explore(g);
				wins+=t-smin;
				return t;//less DFS
			}
		}
		double best=-1;//Probability of winning
		int next=-1;
		for(int t=0;t<4;t++){
			//Check if move is valid
			if(!BitBoards.canDirection(g,t)){
				continue;
			}
			nat_node n=ch[t];
			double score=Math.pow(2,n.wins/n.plays+smin)-Math.pow(2,smin)+c*Math.sqrt(Math.log(plays)/n.plays);
			if(score>best){
				best=score;
				next=t;
			}
		}
		if(next==-1){
			System.out.println("That should never happen");
			Thread.sleep(1000000);
			return -1;
		}
		g=BitBoards.move(g,next);
		double t=ch[next].run(g);
		wins+=t-smin;
		return t;
	}

	int best(){
		double best=-1;
		int ans=-1;
		for(int i=0;i<4;i++){
			if(ch[i]!=null){
				double curr=((double)ch[i].wins)/ch[i].plays;
				if(curr>best){
					best=curr;
					ans=i;
				}
			}
		}
		return ans;
	}
	@Override public String toString(){
		return String.format("%.0f",Math.pow(2,wins/plays+smin)-Math.pow(2,smin))+" "+String.format("%.4f",smin*wins/plays)+" "+plays;
	}
}