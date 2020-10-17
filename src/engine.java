
public class engine{
	public int[][] grid;

	public engine(){
		grid=new int[4][4];
	}

	@Override public String toString(){
		StringBuilder out=new StringBuilder("");
		for(int[] q:grid){
			for(int j:q){
				out.append((char)('0'+j)+" ");
			}
			out.append("\n");
		}
		return out.toString();
	}

}
