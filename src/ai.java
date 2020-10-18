import java.util.*;
class globalno{
	public static final double tlimit=200;
}
class mcts{
	public static int play(engine e,boolean console) throws Exception{
		long l=System.currentTimeMillis();
		player_node n=new player_node();
		double score=e.score();
		nat_node.smin=score;
		player_node.smin=score;
		while(System.currentTimeMillis()-l<globalno.tlimit){
			for(int i=0;i<100;i++){
				n.run(e.clone());
			}
		}
		if(console){
			System.out.println(n);
			System.out.println(score);
			n.run(e.clone());
		}
		return n.best();

	}

}
class nat_node{
	public double wins;
	public int plays;
	player_node[] ch;
	static double smin;
	nat_node(){
		this.ch=new player_node[32];
		wins=0;
		plays=0;
	}
	double explore(engine g) throws Exception{
		plays++;
		engine g2=g.clone();
		int[] q={0,3,0,1};//R U R D
		while(!g2.end()){
			boolean found=false;
			for(int dir:q){
				if(g2.move(dir)){
					g2.spawn();
					found=true;
				}
			}
			if(!found) {
				if(g2.move(2)){
					g2.spawn();
				}else {
					System.out.println("This should not happen");
					Thread.sleep(10000);
				}
			}
		}
		wins+=g2.score()-smin;
		return g2.score();
	}
	double run(engine g) throws Exception{
		plays++;
		if(g.end()){
			wins+=g.score()-smin;
			return g.score();
		}
		while(true){
			int x=(int)Math.floor(4*Math.random());
			int y=(int)Math.floor(4*Math.random());
			if(g.grid[x][y]==0){
				int p;
				if(Math.random()<0.9){
					p=16*1+4*x+y;
				}else{
					p=16*0+4*x+y;
				}
				if(p>=16){
					g.grid[x][y]=1;
				}else{
					g.grid[x][y]=2;
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
		return String.format("%.4f",Math.pow(2,wins/plays+smin)-Math.pow(2,smin))+" "+plays;
	}

}
class player_node{
	public static final double c=30;
	nat_node[] ch;
	double wins;
	int plays;
	static double smin;
	player_node(){
		this.ch=new nat_node[4];
		wins=0;
		plays=0;
	}
	double run(engine g) throws Exception{
		plays++;
		if(g.end()){
			wins+=g.score()-smin;
			return g.score();
		}
		for(int i=0;i<4;i++){
			engine g2=g.clone();
			if(!g2.move(i)){
				continue;
			}
			if(ch[i]==null){
				//run exploration
				ch[i]=new nat_node();
				double t=ch[i].explore(g);
				wins+=t-smin;
				return t;//less DFS
			}
		}
		double best=-1;//Probability of winning
		int next=-1;
		for(int t=0;t<4;t++){
			//Check if move is valid
			engine g2=g.clone();
			if(!g2.move(t)){
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
		g.move(next);
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
		return String.format("%.4f",Math.pow(2,wins/plays+smin)-Math.pow(2,smin))+" "+plays;
	}
}