import java.util.Arrays;

public class engine{
	public int[][] grid;
	public engine(){
		grid=new int[4][4];
		spawn();
		spawn();
	}

	public boolean move(int dir){
		//morda je lepsa resitev. Ni ocitna
		boolean did_work=false;
		if(dir==0){
			for(int x=2;x>=0;x--){
				for(int y=0;y<4;y++){
					if(grid[x][y]!=0){
						int x2=x;
						while(x2<3){
							if(grid[x2+1][y]==0){
								grid[x2+1][y]=grid[x2][y];
								grid[x2][y]=0;
								did_work=true;
							}else if(grid[x2+1][y]==grid[x2][y]){
								grid[x2+1][y]++;
								grid[x2][y]=0;
								did_work=true;
							}else{
								break;
							}
							x2++;
						}
					}
				}
			}
		}
		if(dir==1){
			for(int x=3;x>=0;x--){
				for(int y=2;y>=0;y--){
					if(grid[x][y]!=0){
						int y2=y;
						while(y2<3){
							if(grid[x][y2+1]==0){
								grid[x][y2+1]=grid[x][y2];
								grid[x][y2]=0;
								did_work=true;
							}else if(grid[x][y2+1]==grid[x][y2]){
								grid[x][y2+1]++;
								grid[x][y2]=0;
								did_work=true;
							}else{
								break;
							}
							y2++;
						}
					}
				}
			}
		}
		if(dir==2){
			for(int x=1;x<4;x++){
				for(int y=0;y<4;y++){
					if(grid[x][y]!=0){
						int x2=x;
						while(x2>0){
							if(grid[x2-1][y]==0){
								grid[x2-1][y]=grid[x2][y];
								grid[x2][y]=0;
								did_work=true;
							}else if(grid[x2-1][y]==grid[x2][y]){
								grid[x2-1][y]++;
								grid[x2][y]=0;
								did_work=true;
							}else{
								break;
							}
							x2--;
						}
					}
				}
			}
		}
		if(dir==3){
			for(int x=0;x<4;x++){
				for(int y=1;y<4;y++){
					if(grid[x][y]!=0){
						int y2=y;
						while(y2>0){
							if(grid[x][y2-1]==0){
								grid[x][y2-1]=grid[x][y2];
								grid[x][y2]=0;
								did_work=true;
							}else if(grid[x][y2-1]==grid[x][y2]){
								grid[x][y2-1]++;
								grid[x][y2]=0;
								did_work=true;
							}else{
								break;
							}
							y2--;
						}
					}
				}
			}
		}
		return did_work;
	}
	/**
	 * Will run forever if spawning on full board
	 */
	public void spawn(){
		while(true){
			int x=(int)Math.floor(4*Math.random());
			int y=(int)Math.floor(4*Math.random());
			if(grid[x][y]==0){
				if(Math.random()<0.9){
					grid[x][y]=1;
				}else{
					grid[x][y]=2;
				}
				return;
			}
		}

	}
	public boolean end(){
		engine c=this.clone();
		for(int x=0;x<4;x++){
			if(c.move(x)){
				return false;
			}
		}
		return true;
	}

	@Override public String toString(){
		StringBuilder out=new StringBuilder("");
		for(int y=0;y<4;y++){
			for(int x=0;x<4;x++){
				if(grid[x][y]<10){
					out.append(grid[x][y]+" ");
				}else {
					out.append((char)('a'-10+grid[x][y])+" ");
					
				}
			}
			out.append("\n");
		}
		return out.toString();
	}
	@Override public engine clone(){
		engine e=new engine();
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				e.grid[i][j]=grid[i][j];
			}
		}
		return e;
	}

	@Override public int hashCode(){
		return Arrays.deepHashCode(grid);
	}

	@Override public boolean equals(Object obj){
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(getClass()!=obj.getClass())
			return false;
		engine other=(engine)obj;
		if(!Arrays.deepEquals(grid,other.grid))
			return false;
		return true;
	}

	public double score(){
		int score=0;
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				score+=Math.pow(2,grid[i][j]);
//				score=Math.max(score,grid[i][j]);
			}
		}
		return Math.log(score)/Math.log(2);
//		return score;
	}

}
