import java.util.*;
class globalno{
	public static final double tlimit=2500;
}
class mcts{
	public static final int runde=1000;
	public static int play(engine e,boolean console) throws Exception{
		long l=System.currentTimeMillis();
		player_node n=new player_node();
		int cikli=0;
		while(System.currentTimeMillis()-l<globalno.tlimit){
			for(int i=0;i<1;i++){
				n.run(e.clone());
			}
			cikli++;
		}
		if(console){
			System.out.println(cikli);
			System.out.println(n);
		}
		return n.best();

	}

}
class nat_node{
	public int wins;
	public int plays;
	player_node[] ch;
	nat_node(){
		this.ch=new player_node[32];
		wins=0;
		plays=0;
	}
	int explore(engine g){
		plays++;
		engine g2=g.clone();
		while(!g2.end()){
			int dir=(int)Math.floor(4*Math.random());
			if(g2.move(dir)){
				g2.spawn();
			}
		}
		int t=g2.win();
		if(t==1) {
			wins++;
		}
		return t;
	}
	int run(engine g){
		plays++;
		if(g.win()==1) {
			wins++;
			return 1;
		}
		if(g.end()){
			int t=g.win();
			if(t==1){
				wins++;
			}
			return t;
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
				if(p>16){
					g.grid[x][y]=1;
				}else{
					g.grid[x][y]=2;
				}
				if(ch[p]==null){
					ch[p]=new player_node();
				}
				int t=ch[p].run(g);
				if(t==1) {
					wins++;
				}
				return t;
			}
		}
	}
	@Override public String toString(){
		return wins+"/"+plays;
	}
}
class player_node{
	public static final double c=1.5;
	nat_node[] ch;
	int wins;
	int plays;
	player_node(){
		this.ch=new nat_node[4];
		wins=0;
		plays=0;
	}
	int run(engine g){
		plays++;
		if(g.end()){
			if(g.win()==1){
				wins+=1;
			}
			return g.win();
		}
		for(int i=0;i<4;i++){
			engine g2=g.clone();
			if(!g2.move(i)){
				continue;
			}
			if(ch[i]==null){
				//run exploration
				ch[i]=new nat_node();
				int t=ch[i].explore(g);
				plays++;
				if(t==1){
					wins++;
				}
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
			double score=((double)n.wins)/n.plays+c*Math.sqrt(Math.log(plays)/n.plays);
			if(score>best){
				best=score;
				next=t;
			}
		}
		if(next==-1){
			return g.win();
		}
		g.move(next);
		int t=ch[next].run(g);
		if(t==1){
			wins++;
		}
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
		return wins+"/"+plays;
	}
}