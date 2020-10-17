import java.io.*;
import java.util.*;
public class Main{
	public static void main(String[]qqq) throws Exception{
		engine a=new engine();
		System.out.println(a);
		Random r=new Random();
		while(!a.end()) {
			a.move(r.nextInt(4));
			System.out.println(a);
		}
	}
}
